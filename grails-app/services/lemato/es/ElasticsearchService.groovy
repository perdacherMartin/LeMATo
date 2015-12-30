package lemato.es

import grails.transaction.Transactional
import grails.core.*
import lemato.Corpus
import lemato.es.ElasticObject
import lemato.nopersistence.*
import lemato.es.ElasticObject
import lemato.*
import org.elasticsearch.search.SearchHit
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.client.Client
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse

import org.springframework.beans.factory.annotation.*
import org.elasticsearch.node.Node
import static org.elasticsearch.node.NodeBuilder.nodeBuilder
import groovyx.net.http.HTTPBuilder
import java.util.Date
import java.util.Locale
import java.text.*
import groovy.time.*

import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT


class ElasticsearchService {

    GrailsApplication grailsApplication
    def esClientService

    // @Lazy Client client = elasticsearchNodeService.getNodeclient()
    @Lazy Client client = esClientService


    def save(ElasticObject esObject) {
        def indexR = client.index {
            index esObject.getCorpus().getIndexName()
            type esObject.typeName
			id esObject.getId().toString()
            source {
                tags = esObject.tags                
                publishDate = esObject.publishDate
                textBody = esObject.textBody
            }
        } 
    }

    def delete(ElasticObject esObject) {
        // println "deleting: " + esObject.typeName + " with Id: " + esObject.getId()
        def deleteR = client.delete {
            index esObject.getCorpus().getIndexName()
            type esObject.typeName
			id esObject.getId().toString()
        }
    }


    ElasticObject load(ElasticObject text, List<String> highlightList){

        Closure boolClosure = {}
        Boolean textContainsHighlight = textContainsAllWords(text, highlightList)

        if ( highlightList.isEmpty() || !textContainsHighlight ){
            boolClosure = {                 
                must = [
                    { match { _id = text.getId() } }                
                ]
            }
        }else{
            boolClosure = { 
                must = [
                    { match { _id = text.getId() } },
                    { match { textBody = highlightList.toString().replaceAll('(,)?(\\[)?(\\])?', '') } }
                ] 
            }            
        }

        SearchResponse sr = client.search {
            indices text.getCorpus().getIndexName()
            types text.typeName
            source {
                query {
                    bool = boolClosure
                }
                highlight {
                    preTags = ["<mark>"]
                    postTags = ["</mark>"]
                    fields {
                        textBody = {
                            number_of_fragments = 0
                            force_source = true 
                            type = "fvh"
                        }
                    }
                }
            }
        }.actionGet()

        SearchHit hit = sr.getHits().getHits()[0] 
        
        text.tags = hit.getSource().get("tags")           
        try{
            text.publishDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("DE")).parse(hit.getSource().get("publishDate"))
        }catch(ParseException e){
            println "error in parsing publishDate"
        }

        if ( highlightList.isEmpty() || !textContainsHighlight ){
            text.textBody = hit.getSource().get("textBody")
        }else{
            text.textBody = ""
            hit.getHighlightFields().get("textBody").getFragments().each { fragment ->
                text.textBody = text.textBody + fragment.string()
            }            
        }

        return text
    }    

    List<String> getStemmedVersionsOfString(Corpus c, String concatWords, String analyzer){
        AnalyzeResponse ar = analyze(c.getIndexName(), concatWords, analyzer)
        List<String> stemmedWords = []

        ar.getTokens().each { token ->
            stemmedWords << token.getTerm()
        }
        
        // get textBody with highlights
        return stemmedWords
    }

    protected AnalyzeResponse analyze(String index, String text, String analyzer){
        return client.admin().indices().
            prepareAnalyze(index, text).setAnalyzer(analyzer).execute().actionGet();
    }

    private Boolean textContainsAllWords(ElasticObject text, List<String> highlightList){
        SearchResponse sr = client.search {
            indices text.getCorpus().getIndexName()
            types text.typeName
            source {
                query {
                    bool { 
                        must = [
                            { match { _id = text.getId() } },
                            { match { textBody = highlightList.toString().replaceAll('(,)?(\\[)?(\\])?', '') } }
                       ] 
                    }
                }
                highlight {
                    preTags = ["<mark>"]
                    postTags = ["</mark>"]
                    fields {
                        textBody = {
                            number_of_fragments = 0
                            force_source = true 
                            type = "fvh"
                        }
                    }
                }
            }
        }.actionGet()

        if ( sr.getHits().getHits().size() == 0 ) 
            return false
        else
            return true
    }
    // List<FrequencyFilterText> getTextsForYearAndKeyword(Integer year, String keyWord, Corpus c, String queryString, Class esClass ){
    //     List<FrequencyFilterText> textUnits = []
    //     Integer resultsize = Integer.parseInt(grailsApplication.config.getProperty('elasticsearch.response.size'))
    //     List<Closure> filterList = []
        
    //     filterList << {
    //                     range {
    //                         publishDate {                                    
    //                             from = year.toString()
    //                             lte = (year + 1).toString()
    //                             format = "yyyy"
    //                         }
    //                     }
    //                 }

    //     filterList << {
    //                     term {
    //                         textBody {
    //                             value = keyWord
    //                         }
    //                     }
    //                 }

    //     if ( queryString ){
    //         filterList << {
    //             query_string {
    //                 default_field = "textBody"
    //                 query = queryString
    //             }
    //          }
    //     }

    //     SearchResponse sr = client.search {
    //         indices c.getIndexName()
    //         types esClass.typeName
    //         source {
    //             size = resultsize
    //             explain = true
    //             query {            
    //                 bool {
    //                     must = filterList                        
    //                 }
    //             }          
    //         }
    //     }.actionGet()

    //     // println "size: " + sr.getHits().getHits().size()

    //     for ( SearchHit hit : sr.getHits().getHits() )
    //     {
    //         def esObject = ElasticObject.get(Long.parseLong( hit.getId()) )
    //         esObject.tags = hit.getSource().get("tags")
    //         esObject.textBody = hit.getSource().get("textBody")
    //         try{
    //             esObject.publishDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("DE")).parse(hit.getSource().get("publishDate"))
    //         }catch(ParseException e){
    //             println "error in parsing publishDate"
    //         }

    //         textUnits << new FrequencyFilterText(
    //             text: esObject,
    //             count: Long.parseLong(hit.explanation().toString().find("(?<=tf\\(freq=)(\\d*)(?=\\.\\d*\\))"))
    //         )
            
    //     }

    //     return textUnits

    // }

    /**
     * [getWordCountForDocumentIds returns the count of words for a list of doc-ids]
     * @param  docIds [description]
     * @param  c      [description]
     * @return        [description]
     */

    Long getWordCountForDocumentIds(List<String> docIds, Corpus c){
        // println docIds[0] + "," + docIds[1] + "," + docIds[2]
		SearchResponse sr = client.search {
		    indices c.getIndexName()
		    types DocumentRdb.typeName
		    searchType SearchType.COUNT
		    source {
		        query {
		            ids {
		                values = docIds 
		            }
		        }
		        aggs {
		            counting {
		                sum {
		                    field = "textBody.word_count"
		                }
		            }
		        }       
		    }
		}.actionGet()  

		return sr.aggregations.get("counting").value.longValue()  
	}

    /**
     * [getExistingTags get all tags and their document count for this corpus, called from characteristics controller]
     * @param  c [Corpus]
     * @return   [Map with all tags and their document count]
     */
    
    @Transactional(readOnly = true)
    Map<String, Long> getExistingTags(Corpus c){

        Map<String, Long> tagMap = [:]

        SearchResponse sr = client.search {
            indices c.getIndexName()
            types DocumentRdb.typeName
            source {
                aggs {
                    tagcounts {
                        terms {
                            field = "tags"
                            size = 10000 // if nothing specified 10 is default
                        }
                    }
                }
            }
        }.actionGet()

        sr.getAggregations().get("tagcounts").getBuckets().each { bucket ->
            tagMap.put(bucket.getKey(), bucket.getDocCount())
        }      

        return tagMap  
    }


}
