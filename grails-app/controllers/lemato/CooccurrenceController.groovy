package lemato

import org.springframework.web.servlet.*
import org.springframework.beans.factory.annotation.*
import lemato.es.*
import lemato.nopersistence.SignificanceBucket
import lemato.nopersistence.Cooccurrence 
import edu.ucla.sspace.matrix.Matrix
import lemato.clustering.VectorSpace
import lemato.clustering.DendrogramNode
import static edu.ucla.sspace.common.Similarity.SimType
import static edu.ucla.sspace.clustering.HierarchicalAgglomerativeClustering.ClusterLinkage
import org.apache.commons.io.FileUtils
import java.util.Date
import java.util.Locale
import java.text.*
import groovy.time.*

class CooccurrenceController {

    @Value('${lemato.clustering.simType}')
    String simTypeString

    @Value('${lemato.clustering.clusterLinkage}')
    String clusterLinkageString

    SignificanceService significanceService
	ElasticsearchService elasticsearchService

    def index() { }

    def run() {
    	Long size = 0
    	Corpus c = Corpus.get(params.int('corpus.id'))

        ClusterLinkage clusterLinkage = ClusterLinkage.valueOf(clusterLinkageString)
        SimType simType = SimType.valueOf(simTypeString)        

        if (c == null) {            
            flash.error = "Choose a valid corpus!"
            respond "", view:'index'
            return
        }

        if ( ! params.luceneQuery ){
        	flash.error = "Enter a valid centered word."
            respond "", view:'index'
            return        	
        }

        try {
        	size = Long.parseLong(params.size)
        }catch(NumberFormatException e){
			flash.error = "Error in parsing size '${params.size}' is not a valid number."
            respond "", view:'index'
            return
        }

        if ( ! params.textUnit ){
            flash.error = "Choose a valid textUnit."
            respond "", view:'index'
            return          	
        }

        List<String> keywords = elasticsearchService.getStemmedVersionsOfString(c,params.luceneQuery,"unigram");
        // Date start = new Date()

        List<SignificanceBucket> buckets = significanceService.getTopSignificantWordsForQuery(c,params.luceneQuery,size,params.textUnit)

        if ( buckets.size() < size ){
            flash.error = "Your query returned zero (or too less) results."
            respond "", view:'index'
            return            
        }

        Matrix matrix = significanceService.getCooccurrenceMatrix(c, 
                buckets, 
                Cooccurrence.Measure.DiceCoefficient,
                params.textUnit,
                params.luceneQuery)

        // 2) use the matrix to build the dendrogram using s-space            
        VectorSpace vs = new VectorSpace(matrix)
        DendrogramNode rootNode = vs.getDendrogramHierachy(buckets, clusterLinkage, simType)

        // println "size: " + size + " ; " + TimeCategory.minus(new Date(), start)

        return new ModelAndView("/cooccurrence/run", 
            [buckets: buckets, jsonData: rootNode.toJson(), corpusId: params.int('corpus.id')])

    }
}
