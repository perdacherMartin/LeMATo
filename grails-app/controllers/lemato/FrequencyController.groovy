package lemato

import org.springframework.web.servlet.ModelAndView
import lemato.nopersistence.*
import grails.converters.JSON 
import lemato.es.*
import lemato.es.FrequencyService
import java.util.Date
import java.util.Locale
import java.text.*
import groovy.time.*

class FrequencyController {

    FrequencyService frequencyService

    def index() {
    }

    def show(Integer corpusId, Integer size, String query) { 
        
        if ( corpusId == null ) {
            corpusId = params.int('corpus.id')
        }

    	Corpus c = Corpus.get(corpusId)

        if (c == null) {            
            flash.error = "Choose a valid corpus!"
            respond "", view:'index'
            return
        }         

        if ( query == null ){
            if ( params.query != null ){
                query = params.query  
            }else{
                query = ""
            }
        }

        if ( ! size ){
            try {
                size = Long.parseLong(params.size)
            }catch(NumberFormatException e){
                flash.error = "Error in parsing size '${size}' is not a valid number."
                respond "", view:'index'
                return
            } 
        }

        Date start = new Date()

        Long totalFrequencies = frequencyService.getHistogram(c, "", FrequencyService.FrequencyType.TermFrequency).values().sum()
    	Map<Integer, Long> tfHist = frequencyService.getHistogram(c, query, FrequencyService.FrequencyType.TermFrequency)
        Map<Integer, Long> dfHist = frequencyService.getHistogram(c, query, FrequencyService.FrequencyType.DocumentFrequency)
        Map<Integer, Long> pfHist = frequencyService.getHistogram(c, query, FrequencyService.FrequencyType.ParagraphFrequency)
        Map<Integer, Long> sfHist = frequencyService.getHistogram(c, query, FrequencyService.FrequencyType.SentenceFrequency)
    	List<FrequencyResult> frequencies = frequencyService.getFrequencyResults(c, size, query )

        // println "size: " + size + " ; " + TimeCategory.minus(new Date(), start)

    	return new ModelAndView("/frequency/show", 
    		[tfHist: tfHist, dfHist: dfHist, pfHist: pfHist, sfHist: sfHist,
             frequencies: frequencies, corpusId: corpusId, luceneQuery: query, totalFrequencies: totalFrequencies]
    	)
    }

    def detail(Integer corpusId, String keys, String query) {        
        List<String> termlist = params.keys.split(",")


        if ( termlist.isEmpty() ){
            flash.error = "No keys specified for action details."
            respond "", view:'index'
            return            
        }

        Corpus c = Corpus.get(corpusId)

        if (c == null) {            
            flash.error = "Not a valid corpus for action details."
            respond "", view:'index'
            return
        } 

        List<FrequencyDetail> details = frequencyService.getDetailsForTerms(termlist,c, query)
        
        return [details: details]
    }

    def filterTextUnits(Integer year, String key, Integer corpusId, String textUnit, String query){
        List<FrequencyFilterText> textUnits = []
        Corpus c = Corpus.get(corpusId)

        // println "enterd: "
        // println "${year} - ${key} - ${corpusId} - ${textUnit} ${query}"
        // println ""

        if (c == null) {            
            flash.error = "Not a valid corpus for action filterTextUnits."
            respond "", view:'index'
            return
        }

        switch ( textUnit ){
            case 'document':
                textUnits = frequencyService.getTextsForYearAndKeyword(year,key,c, query, DocumentRdb )
                break;
            case 'paragraph': 
                textUnits = frequencyService.getTextsForYearAndKeyword(year,key,c, query, ParagraphRdb )
                break;
            case 'sentence':
                textUnits = frequencyService.getTextsForYearAndKeyword(year,key,c, query, SentenceRdb )
                break;
            default:
                flash.error = "Not a valid textUnit specified for action filterTextUnits."
                respond "", view:'index'
                return
        }

        Integer maxResults = Integer.parseInt(grailsApplication.config.getProperty('elasticsearch.response.size'))

        return new ModelAndView("/frequency/filterTextUnit", 
            [textUnits : textUnits, keyWord: key, maxResults: maxResults ] )          
        
    }

}
