package lemato.es

import lemato.Corpus

class SentenceRdb extends ElasticObject {

	static String typeName = "sentence"

	static belongsTo = [paragraph: ParagraphRdb]

    static constraints = {
    }

    Corpus getCorpus() { return paragraph.corpus }

   	def afterInsert() {
   		elasticsearchService.save(this)
   	}

   	def beforeDelete() {
   		elasticsearchService.delete(this)
   	}

   	/* elasticsearch mapping for sentence */ 
	static Closure properties = {
		tags = {
			type = "string"
			analyzer = "unigram"
			index = "analyzed"
		}
		publishDate = { type = "date" }
		textBody = {
			type = "string"
			analyzer = "unigram"
			null_value = "-"
			index = "analyzed"
			term_vector = "with_positions_offsets"
			term_statistics = true
		}
	}    
}
