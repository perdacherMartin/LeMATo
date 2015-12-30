package lemato

import org.springframework.web.servlet.ModelAndView
import lemato.elasticsearch.repository.service.domain.*
import lemato.es.*

class ViewController {

    ElasticsearchService elasticsearchService

    // def index() { }

    def text(Long id, Integer corpusId) {
        List<String> highlightList = []

        if ( params.highlight )
    	   highlightList = params.highlight.split(",")

    	Corpus c = Corpus.get(corpusId)

        ElasticObject textUnit = ElasticObject.get(id)

        textUnit = elasticsearchService.load(textUnit,highlightList)

        // if (c == null) {
        //     transactionStatus.setRollbackOnly()
        //     flash.error = "Choose a valid corpus!"
        //     respond "", view:'sentence'
        //     return
        // }
        // ElasticsearchRepository repo = new ElasticsearchRepository(c)
        // Sentence s = repo.getSentence(sentenceId, highlightList)

        return new ModelAndView("/view/textUnit", 
            [textUnit : textUnit ] ) 
    }
}
