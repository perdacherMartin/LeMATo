package lemato

import org.springframework.web.servlet.*
import org.springframework.beans.factory.annotation.*
import grails.transaction.Transactional
import lemato.Corpus
import lemato.es.*
import lemato.nopersistence.SignificanceBucket
import lemato.nopersistence.Cooccurrence 
import edu.ucla.sspace.matrix.Matrix
import lemato.clustering.VectorSpace
import lemato.clustering.DendrogramNode
import static edu.ucla.sspace.common.Similarity.SimType
import static edu.ucla.sspace.clustering.HierarchicalAgglomerativeClustering.ClusterLinkage
import org.apache.commons.io.FileUtils

class CharacteristicsController {

    @Value('${lemato.clustering.simType}')
    String simTypeString

    @Value('${lemato.clustering.clusterLinkage}')
    String clusterLinkageString

    SignificanceService significanceService
	ElasticsearchService elasticsearchService

    def index() { 
    }

    def run() {
        Boolean abort = false
        Long size = 0
        Map<String,List<SignificanceBucket>> significanceBuckets = [:]
        Map<String,String> jsonData = [:]
    	Corpus c = Corpus.get(params.int('corpus.id'))

        ClusterLinkage clusterLinkage = ClusterLinkage.valueOf(clusterLinkageString)
        SimType simType = SimType.valueOf(simTypeString)

        if (c == null) {            
            flash.error = "Choose a valid corpus!"
            respond "", view:'index'
            return
        }

        List<String> tags = elasticsearchService.getStemmedVersionsOfString(c, params.tags, "unigram")

        if (tags.size() <= 1 ) {            
            flash.error = "Choose at least two tags."
            respond "", view:'index'
            return
        }

        Map<String, Long> definedTags = elasticsearchService.getExistingTags(c)

        tags.each { tag ->
        	if ( ! definedTags.containsKey(tag) && ! abort ) {
                println "tag: ${tag} - " + definedTags
        		flash.error = "Tag ${tag} is not defined in this corpus."
            	respond "", view:'index'
                // here no return possible because of .each closure
                abort = true
        	}
        }

        if ( abort ){
            return
        }

        try {
        	size = Long.parseLong(params.size)
        }catch(NumberFormatException e){
			flash.error = "Error in parsing size '${params.size}' is not a valid number."
            respond "", view:'index'
            return
        }
        
        if ( params.textUnit == null ){
            flash.error = "Choose a valid textUnit."
            respond "", view:'index'
            return            
        }

        tags.eachWithIndex { tag, i ->

            List<SignificanceBucket> buckets = significanceService.getTopSignificantWordsForQuery(c,
                    "tags: '${tag}'",
                    size,
                    params.textUnit)

            // build a dendrogram for each specified subcorpus
            // 1) build up a co-occurrence matrix using the
            // dice-coefficient as co-occurrence measure
            Matrix matrix = significanceService.getCooccurrenceMatrix(c, 
                                                        buckets, 
                                                        Cooccurrence.Measure.DiceCoefficient,
                                                        params.textUnit,
                                                        "tags: '${tag}'")

            // 2) use the matrix to build the dendrogram using s-space            
            VectorSpace vs = new VectorSpace(matrix)
            DendrogramNode rootNode = vs.getDendrogramHierachy(buckets, clusterLinkage, simType)

			significanceBuckets.put(tag, buckets)
            // println rootNode.toJson()
            jsonData.put(tag,rootNode.toJson())
            
        }

        return new ModelAndView("/characteristics/run", 
            [significanceBuckets: significanceBuckets, jsonData: jsonData, corpusId: params.int('corpus.id')])
    }
}
