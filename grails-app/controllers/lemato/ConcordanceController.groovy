package lemato

import org.springframework.web.servlet.ModelAndView
import lemato.nopersistence.ConcordanceResult
import lemato.Corpus
import lemato.es.ElasticsearchService
import lemato.es.*

class ConcordanceController {

	ConcordanceService concordanceService
    ElasticsearchService elasticsearchService

    def index() { 
    }

    /**
     * performing a Key-Word-In-Context analysis
     */
    def kwic(Integer corpusId, String query, String keyword) {
        
        if ( keyword == null ){
            if ( params.keyword == null ){
                flash.error = "No centered word is defined."
                respond "", view:'index'
                return     
            }
            keyword = params.keyword
        }


        if ( corpusId == null ) {
            corpusId = params.int('corpus.id')
        }

        if ( query == null ){
            if ( params.query != null ){
                query = params.query  
            }
        }

        Corpus c = Corpus.get(corpusId)

        if (c == null) {            
            flash.error = "Choose a valid corpus!"
            respond "", view:'index'
            return
        }      

        List<String> stemming = elasticsearchService.getStemmedVersionsOfString(c, keyword, "unigram")

        if ( stemming.size() != 1 ){
            flash.error = "Use a single centerd word only! " + stemming
            respond "", view:'index'
            return            
        }

        params.keyword = stemming[0]
    	
    	List<ConcordanceResult> results = concordanceService.getConcordanceResults(query, c, keyword, DocumentRdb)
        
        if ( results.isEmpty() ){
            flash.error = "The query returned zero results."
            respond "", view:'index'
            return
        }

    	return new ModelAndView("/concordance/kwic", 
    		[results: results]) 	
    }
}
