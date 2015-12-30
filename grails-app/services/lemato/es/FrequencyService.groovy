package lemato.es

import grails.core.*
import grails.transaction.Transactional
import lemato.*
import lemato.es.*
import lemato.nopersistence.*
import java.util.Date
import java.util.Locale
import java.text.*
import groovy.time.*
import org.elasticsearch.search.SearchHit
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.client.Client
import org.elasticsearch.search.aggregations.bucket.terms.*
// import org.elasticsearch.search.aggregations.bucket.histogram.Histogram // ES:2.0
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram.Interval
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import org.apache.commons.math3.stat.correlation.KendallsCorrelation 


class FrequencyService {

    GrailsApplication grailsApplication
    def esClientService

    @Lazy Client client = esClientService
    
    static enum FrequencyType {
        TermFrequency,
        DocumentFrequency,
        ParagraphFrequency,
        SentenceFrequency
    }


    private Closure getFilterClosureForQuerystring(String queryString){
        Closure filterClosure = {} 

        if ( queryString ){
            filterClosure = {
                query_string {
                    default_field = "textBody"
                    query = queryString
                }
             }            
        }

        return filterClosure
    }

    private Closure getQueryForQuerystring(String queryString){
        Closure querystr = {} 

        if ( queryString ){
            querystr = {
                query_string {
                    default_field = "textBody"
                    query = queryString
                }
             }            
        }else{
            querystr = {
                match_all = {}
            }
        }

        return querystr
    }


    Map<Integer, Long> getHistogram(Corpus c, String queryString, FrequencyType frqType){
        Map<Integer, Long> perYear = [:]
        String typeName = ""
        Long frequencyValue = 0
        Closure filterQuery = getQueryForQuerystring(queryString)

        switch ( frqType ) {
            case FrequencyType.DocumentFrequency:
            case FrequencyType.TermFrequency:
                typeName = DocumentRdb.typeName
                break;
            case FrequencyType.ParagraphFrequency:
                typeName = ParagraphRdb.typeName
                break;
            case FrequencyType.SentenceFrequency:
                typeName = SentenceRdb.typeName
                break;
            default:
                println "default called which should never ever be called!"
                break;
        }

        SearchResponse sr = client.search {
            indices c.getIndexName()
            types typeName
            searchType SearchType.COUNT
            source {
                query = filterQuery
                aggs {
                    countPerYear {
                        date_histogram {
                            field = "publishDate"
                            interval = Interval.YEAR
                        }
                    }
                }
            }
        }.actionGet()


        DateHistogram dHist = sr.getAggregations().get("countPerYear");
        // Histogram dHist = sr.getAggregations().get("countPerYear"); // elasticsearch 2.0

        dHist.getBuckets().each{ entry->

            sr = client.search {
                indices c.getIndexName()
                types typeName
                searchType SearchType.COUNT
                source {
                    query {
                        filtered {
                            query = filterQuery
                            filter {
                                bool {
                                    must {
                                        range {
                                            publishDate{    
                                                from = entry.getKeyAsDate()
                                                to = entry.getKeyAsDate().plusYears(1)
                                            }
                                        }
                                    }                        
                                }
                            }                
                        }            
                    }
                    aggs {
                        sumYear {
                            sum {                    
                                field = "textBody.word_count"                    
                            }
                        }            
                    }        
                }
            }.actionGet()            

            if ( frqType == FrequencyType.TermFrequency ){
                frequencyValue = sr.aggregations.get("sumYear").getValue()
            }else{
                frequencyValue = sr.getHits().getTotalHits()
            }

            perYear.put(
                entry.getKeyAsDate().getYear(),
                frequencyValue
            )
        }

        return perYear
    }

    List<FrequencyResult> getFrequencyResults(Corpus c, Integer responseSize, String queryString){
        List<FrequencyResult> frqResults = []
                
        Closure queryClosure = getQueryForQuerystring(queryString)

        SearchResponse sr = client.search {
            indices c.getIndexName()
            types DocumentRdb.typeName
            searchType SearchType.COUNT
            source {
                query = queryClosure
                aggs {
                    wordcounts {
                      terms { 
                        field = "textBody"
                        size = responseSize
                      }
                    }
                }    
            }
        }.actionGet()

        StringTerms stringTerms = sr.aggregations.get("wordcounts")
        
        for ( StringTerms.Bucket entry : stringTerms.getBuckets() ) {
            frqResults << getFrequencyResultForTerm(c, entry.getKey(), queryString)
        }

        return frqResults
    }

    private FrequencyDetail getDetailsForTerm(String keyword, FrequencyResult result, Corpus c, String queryString){
        Map<Integer, Long> termFrequencies = [:]
        
        Closure filterClosure = getFilterClosureForQuerystring(queryString)

        // get all years for the corpus
        SearchResponse sr = client.search {
            indices c.getIndexName()
            types DocumentRdb.typeName
            searchType SearchType.COUNT 
            source {
                aggs {
                    perYear {
                        date_histogram {
                            field = "publishDate"
                            interval = Interval.YEAR
                        }
                    }
                }
            }
        }.actionGet()

        DateHistogram dHist = sr.getAggregations().get("perYear");
        // Histogram dHist = sr.getAggregations().get("perYear"); // elasticsearch 2.0

        dHist.getBuckets().each { entry ->

            List<Closure> filterList = []
            filterList << { term { textBody = keyword } }
            filterList << {
                            range {
                                publishDate {
                                    from = entry.getKeyAsDate()
                                    to = entry.getKeyAsDate().plusYears(1)
                                }
                            }
                        }

            if ( queryString ){
                filterList << filterClosure
            }

            SearchResponse searchResponse = client.search {
                indices c.getIndexName()
                types DocumentRdb.typeName
                searchType SearchType.COUNT
                source {
                    query {
                        bool {
                            must = filterList
                        }            
                    }
                    aggs {
                        sumYear {
                            sum {                    
                                script_id = "termFrequency"
                                lang = "groovy"
                                params {
                                    term = "${keyword}"
                                }
                            }
                        }
                    }        
                }
            }.actionGet()

            termFrequencies.put(
                entry.getKeyAsDate().getYear(),
                searchResponse.aggregations.get("sumYear").getValue()
            )
        }        

        return new FrequencyDetail(result: result, 
            termFrequencies: termFrequencies,
            docFrequencies: getFrequenciesForTermAndType(keyword, DocumentRdb.typeName,c, queryString),
            paragraphFrequencies: getFrequenciesForTermAndType(keyword, ParagraphRdb.typeName,c, queryString),
            sentenceFrequencies: getFrequenciesForTermAndType(keyword, SentenceRdb.typeName,c, queryString) )
    }


    private Map<Integer, Long> getFrequenciesForTermAndType(String keyword, String typeName,Corpus c, String queryString){
        Map<Integer, Long> frequencies = [:]
        Closure filterClosure = getFilterClosureForQuerystring(queryString)

        SearchResponse sr = client.search {
            indices c.getIndexName()
            types typeName
            searchType SearchType.COUNT 
            source {
                query {
                    filtered {
                        query{
                            term { textBody = keyword }
                        }
                        filter = filterClosure
                    }                
                }
                aggs {
                    perYear {
                        date_histogram {
                            field = "publishDate"
                            interval = Interval.YEAR
                        }
                    }
                }
            }
        }.actionGet()

        DateHistogram dHist = sr.getAggregations().get("perYear");
        // Histogram dHist = sr.getAggregations().get("perYear"); // elasticsearch 2.0

        dHist.getBuckets().each { entry ->
            frequencies.put(
                entry.getKeyAsDate().getYear(),
                entry.getDocCount()
            )
        }

        return frequencies
    }

    List<FrequencyDetail> getDetailsForTerms(List<String> termlist,Corpus c, String queryString){
        List<FrequencyDetail> details = []

        termlist.each{ term ->                      
            details << getDetailsForTerm(
                term, 
                getFrequencyResultForTerm(c,term, queryString),
                c,
                queryString
            )
            
        }
        return details
    }

    private FrequencyResult getFrequencyResultForTerm(Corpus c, String searchterm, String queryString){ 
        
        Closure queryClosure = getQueryForQuerystring(queryString)

        List<Closure> queryList = []

        queryList << {
                        match {
                            textBody = searchterm
                        } 
                    }
        queryList << queryClosure

        SearchResponse termSr = client.search {
            indices c.getIndexName()
            types DocumentRdb.typeName
            searchType SearchType.COUNT
            source {
                query {
                    filtered {
                        query {
                            bool {
                                must = queryList
                            }
                               
                        }                        
                    }     
                }
                aggs {
                    perYear {
                      date_histogram { 
                        field = "publishDate"
                        interval = Interval.YEAR
                      }
                    }
                    tf_sum {
                            sum {                    
                                script_id = "termFrequency"
                                lang = "groovy"
                                params {
                                    term = "${searchterm}"
                                }
                            }
                    }
                }        
            }
        }.actionGet()

        DateHistogram dHist = termSr.getAggregations().get("perYear"); //elasticsearch 1.7.3
        // Histogram dHist = termSr.getAggregations().get("perYear"); // elasticsearch 2.0        


        if ( dHist.getBuckets().size() > 0 ){
            double[] years = new double[dHist.getBuckets().size()]
            double[] docFrequencies = new double[dHist.getBuckets().size()]
            Long sumDocFrequencies = 0 

            dHist.getBuckets().eachWithIndex{ subentry, i ->
                years[i] = subentry.getKeyAsDate().getYear().doubleValue()
                docFrequencies[i] = subentry.getDocCount().doubleValue()
                sumDocFrequencies += subentry.getDocCount()
            }

            Double kendallTau = new KendallsCorrelation().correlation(
                docFrequencies,years)

            return new FrequencyResult(keyword: searchterm, 
                docCount: sumDocFrequencies, termFrequency: termSr.getAggregations().get("tf_sum").getValue(), kendallTauOnDocCount: kendallTau) 
        }else{
            // some terms get removed by the stop-words filter
            return new FrequencyResult(keyword: searchterm, 
                docCount: 0, termFrequency: 0, kendallTauOnDocCount: 0)                 
        }        
    }

    List<FrequencyResult> getFrequencyResultsTable(Integer responseSize, Corpus c){
        List<FrequencyResult> frqResults = []
        SearchResponse sr = client.search {
            indices c.getIndexName()
            types DocumentRdb.typeName
            searchType SearchType.COUNT
            source {
                aggs {
                    wordcounts {
                      terms { 
                        field = "textBody"
                        size = responseSize
                      }
                    }
                }    
            }
        }.actionGet()

        StringTerms stringTerms = sr.aggregations.get("wordcounts")
        
        for ( StringTerms.Bucket entry : stringTerms.getBuckets() ) {
            frqResults << getFrequencyResultForTerm(c, entry.getKey(), "")
        }

        return frqResults
    } 

    List<FrequencyFilterText> getTextsForYearAndKeyword(Integer year, String keyWord, Corpus c, String queryString, Class esClass ){
        List<FrequencyFilterText> textUnits = []
        Integer resultsize = Integer.parseInt(grailsApplication.config.getProperty('elasticsearch.response.size'))
        List<Closure> filterList = []
        
        filterList << {
                        range {
                            publishDate {                                    
                                from = year.toString()
                                lte = (year + 1).toString()
                                format = "yyyy"
                            }
                        }
                    }

        filterList << {
                        term {
                            textBody {
                                value = keyWord
                            }
                        }
                    }

        if ( queryString ){
            filterList << {
                query_string {
                    default_field = "textBody"
                    query = queryString
                }
             }
        }

        SearchResponse sr = client.search {
            indices c.getIndexName()
            types esClass.typeName
            source {
                size = resultsize
                explain = true
                query {            
                    bool {
                        must = filterList                        
                    }
                }          
            }
        }.actionGet()

        // println "size: " + sr.getHits().getHits().size()

        for ( SearchHit hit : sr.getHits().getHits() )
        {
            def esObject = ElasticObject.get(Long.parseLong( hit.getId()) )
            esObject.tags = hit.getSource().get("tags")
            esObject.textBody = hit.getSource().get("textBody")
            try{
                esObject.publishDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("DE")).parse(hit.getSource().get("publishDate"))
            }catch(ParseException e){
                println "error in parsing publishDate"
            }

            textUnits << new FrequencyFilterText(
                text: esObject,
                count: Long.parseLong(hit.explanation().toString().find("(?<=tf\\(freq=)(\\d*)(?=\\.\\d*\\))"))
            )
            
        }
        return textUnits
    }
}
