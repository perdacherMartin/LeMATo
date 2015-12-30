package lemato.es

import lemato.Lfile
import lemato.Corpus

class DocumentRdb extends ElasticObject {

	static String typeName = "document"

	static belongsTo = [lfile: Lfile]
	static hasMany = [paragraphs: ParagraphRdb]

	Corpus getCorpus() { return lfile.corpus }

  def afterInsert() {
    elasticsearchService.save(this)
  }

  def beforeDelete() {
    elasticsearchService.delete(this)
  }

  static constraints = {
  }

  /* elasticsearch mapping properties for document */
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
      fields = {
        word_count = {
          type = "token_count"
          store = "yes"
          analyzer = "standard"
        }
        bigram = {
          type = "string"
          analyzer = "bigram"
          null_value = "-"
          index = "analyzed"
          term_vector = "with_positions_offsets"
          term_statistics = true
        }
        trigram = {
          type = "string"
          analyzer = "trigram"
          null_value = "-"
          index = "analyzed"
          term_vector = "with_positions_offsets"
          term_statistics = true
        }
      }
    }
  }  
}
