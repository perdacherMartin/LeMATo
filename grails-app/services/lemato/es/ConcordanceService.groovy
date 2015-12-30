package lemato.es

import grails.transaction.Transactional
import grails.core.*
import lemato.Corpus
import lemato.nopersistence.*
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import java.util.Date
import java.util.Locale
import java.text.*
import groovy.time.*
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit
import org.elasticsearch.client.Client



@Transactional
class ConcordanceService {

    GrailsApplication grailsApplication
    def esClientService
    ElasticsearchService elasticsearchService

    // @Lazy Client client = elasticsearchNodeService.getNodeclient()
    @Lazy Client client = esClientService

    /**
     * get results for "/concordance/kwic" view
     * @param  luceneQuery [lucene query string]
     * @param  c           [lemato.Corpus]
     * @return             [A list of ConcordanceResult]
     */
    @Transactional(readOnly = true)
    List<ConcordanceResult> getConcordanceResults(String luceneQuery, Corpus c, String keyword, Class esClass){        
        List<ConcordanceResult> result = []
        Long fragmentCount = Long.parseLong(grailsApplication.config.getProperty('elasticsearch.response.size'))
        Long frSize = Long.parseLong(grailsApplication.config.getProperty('lemato.concordancer.fragment.size'))
		List<String> highlights = []

        Closure filterClosure = {}

        if ( luceneQuery ) {
            filterClosure = {
                query {
                    query_string {
                        default_field = "textBody"
                        query = luceneQuery
                    }
                }
            }
        }
        SearchResponse sr = client.search {
            indices c.getIndexName()
            types esClass.typeName
            source {
                query {
                    filtered{
                        query {
                            match {
                                textBody = keyword
                            }
                        }
                        filter = filterClosure   
                    }                          
                }
                highlight {
                    preTags = ["<mark>"]
                    postTags = ["</mark>"]
                    fields {
                        textBody = {
                            number_of_fragments = fragmentCount
                            fragment_size = frSize
                            force_source = true 
                            type = "fvh"
                        }
                    }
                }
                size = 1000000
            }            
        }.actionGet()

        for ( SearchHit hit : sr.getHits().getHits() )
        {
            List<String> highlightList = []
			String myid = hit.getId()
			
            ElasticObject esObject = esClass.get(Long.parseLong(hit.getId()))
            esObject.tags = hit.getSource().get("tags")

            try{
                esObject.publishDate = new SimpleDateFormat("yyyy-MM-dd", new Locale("DE")).parse(hit.getSource().get("publishDate"))
            }catch(ParseException e){
                println "error in parsing publishDate"
            }            

            hit.getHighlightFields().get("textBody").getFragments().each { fragment ->

                AnalyzeResponse ar = elasticsearchService.analyze(c.getIndexName(), 
                        fragment.toString().findAll("(?<=<mark>)([^\\s]*)(?=</mark>)").toString().replaceAll("(\\[)|(\\])", ""), 
                        "unigram")

                ar.getTokens().each { token ->
                    highlights << token.getTerm()
                }
                
				// get textBody with highlights
				esObject = elasticsearchService.load(esObject, highlights.unique() )
	           
                String fragmentStr = fragment.toString().trim()
                fragmentStr = fragmentStr.replaceAll("\n", "")
                // fragmentStr = fragmentStr.replaceAll("&quot;", "\"")
                // String stemmedCenter = highlights.unique().decodeHTML()
				result << new ConcordanceResult(fragment: fragmentStr, stemmedCenter: highlights.unique()[0] ,
					textUnit: esObject)
				
            }
            
        }
        return result
    }

}
