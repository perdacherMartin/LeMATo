package lemato.es

import groovy.time.*
import grails.transaction.Transactional
import grails.core.*
import org.elasticsearch.search.SearchHit
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.search.SearchResponse
import edu.ucla.sspace.matrix.Matrix
import edu.ucla.sspace.matrix.ArrayMatrix
import org.elasticsearch.action.count.CountResponse
import org.elasticsearch.client.Client
import lemato.nopersistence.*
import lemato.Corpus


@Transactional
class SignificanceService {

    GrailsApplication grailsApplication
    def esClientService

    @Lazy Client client = esClientService

    Matrix getCooccurrenceMatrix(Corpus c, List<SignificanceBucket> buckets, Cooccurrence.Measure coocMeasure, String typeName, String luceneQuery){
        double[][] matrix = new double[buckets.size()][buckets.size()]
        Date timeStart = new Date()

        CountResponse cr = client.count {
            indices c.getIndexName()
            types typeName
            source {
              query {
                query_string {
                    default_field = "textBody"
                    query = luceneQuery
                }
              }
            }
        }.actionGet()   

        Long n = cr.getCount()

        // calculate cooccurrence matrix, all buckets against all other buckets
        // the symmetric nature of the matrix allows to calculate only the upper triangle 
        // and copy the value to the lower triangle        
        buckets.eachWithIndex{ bucketA, i ->
            // upper triangle
            for ( int j = i + 1 ; j < buckets.size() ; j++ ){

                cr = client.count {
                    indices c.getIndexName()
                    types typeName
                    source {
                        query {
                            bool {
                                must = [
                                    {
                                        query_string {
                                            default_field = "textBody"
                                            query = luceneQuery
                                        }
                                    },
                                    {
                                        query_string {
                                            default_field = "textBody"
                                            query = "${bucketA.key} AND ${buckets[j].key}"
                                        }
                                    }
                                ]
                            }
                        }
                    }
                }.actionGet()

                matrix[i][j] = new Cooccurrence(bucketA.docCount, buckets[j].docCount, cr.getCount(), n).getCooccurrence(coocMeasure)
                matrix[j][i] = matrix[i][j] // copy to lower triangle
            }
        }

        Matrix aMatrix = new ArrayMatrix(matrix)
        println "duration:" + TimeCategory.minus(new Date(), timeStart)

        return aMatrix          
    }


    /**
     * [getTopSignificantWordsForQuery foreground is the @luceneQuery and the background is the complement to @luceneQuery]
     * @param  corpus      [Corpus]
     * @param  luceneQuery [lucene query]
     * @param  responseSize[response size]
     * @param  typeName    [text unit to measure co-occurrence]
     * @return             [returns the top @size significant words for the corpus @corpus and its query @luceneQuery]
     */
    List<SignificanceBucket> getTopSignificantWordsForQuery(Corpus corpus, String luceneQuery, Long responseSize, String typeName){
        List<SignificanceBucket> list = []

        SearchResponse sr = client.search{
            indices corpus.getIndexName()
            types typeName
            searchType SearchType.COUNT
            source {
                query{
                    filtered{
                        filter{
                            query{
                                query_string {
                                    default_field = "textBody"
                                    query = luceneQuery
                                }
                            }
                        }
                    }
                }
                aggs{
                    termagg{
                        significant_terms {
                            chi_square = {
                                background_is_superset = false
                            }
                            field = "textBody"                            
                            background_filter {
                                and = [
                                    {
                                        not {
                                            query{
                                                query_string {
                                                    default_field = "textBody"
                                                    query = luceneQuery
                                                }
                                            }
                                        }
                                    },
                                    {
                                        type {
                                            value = typeName
                                        }
                                    }
                                ]
                            }
                            size = responseSize
                        }
                    }
                }                             
            }            
        }.actionGet()

        sr.getAggregations().get("termagg").getBuckets().each { bucket ->

            list << new SignificanceBucket(key: bucket.getKey(),
                docCount: bucket.getDocCount(),
                score: bucket.getSignificanceScore(), 
                subsetDf: bucket.getSubsetDf(),
                subsetSize: bucket.getSubsetSize(),
                supersetDf: bucket.getSupersetDf(),
                supersetSize: bucket.getSupersetSize())
        }

        return list
    }


    List<SignificanceBucket> getTopSignificantWordsForTags(Corpus corpus, String foregroundTag, List<String> backgroundTags, String typeName, Long responseSize){
        List<SignificanceBucket> list = []
        SearchResponse sr = client.search {
            indices corpus.getIndexName()
            types typeName
            searchType SearchType.COUNT
            source {
                query {            
                    term {
                        tags = foregroundTag
                    }
                }
                aggs {
                    signiaggs {
                        significant_terms {
                            chi_square = {
                                background_is_superset = false
                            }
                            field = "textBody"                            
                            background_filter {
                                and = [
                                    {
                                        terms {
                                            tags = backgroundTags
                                        }
                                    },
                                    {
                                        type {
                                            value = typeName
                                        }
                                    }
                                ]
                            }
                            size = responseSize
                        }          
                    }  
                }
            }
        }.actionGet()  

        sr.getAggregations().get("signiaggs").getBuckets().each { bucket ->
            list << new SignificanceBucket(key: bucket.getKey(),
                docCount: bucket.getDocCount(),
                score: bucket.getSignificanceScore(), 
                subsetDf: bucket.getSubsetDf(),
                subsetSize: bucket.getSubsetSize(),
                supersetDf: bucket.getSupersetDf(),
                supersetSize: bucket.getSupersetSize())
        }

        return list
    }
}
