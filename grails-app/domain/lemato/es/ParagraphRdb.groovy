package lemato.es

import lemato.Corpus
import lemato.es.DocumentRdb

class ParagraphRdb extends ElasticObject {

	static String typeName = "paragraph"	

	static belongsTo = [document: DocumentRdb]
	static hasMany = [sentences: SentenceRdb]

	Corpus getCorpus() { return document.corpus }

  def afterInsert() {
  	elasticsearchService.save(this)
  }

  def beforeDelete() {
  	elasticsearchService.delete(this)
  }

  static constraints = {
  }

  /* elasticsearch mapping for paragraph */
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
