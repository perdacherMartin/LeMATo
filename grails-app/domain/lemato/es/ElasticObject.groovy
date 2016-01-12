package lemato.es

import lemato.Corpus

abstract class ElasticObject{

	ElasticsearchService elasticsearchService

	String tags 
	Date publishDate
	String textBody	

	static String typeName = "undefined"

    static mapping = {
        tablePerHierarchy false
    }

    static constraints = {
    }

	static transients = ['corpus', 'textBody', 'publishDate', 'tags', 'elasticsearchService']
    abstract Corpus getCorpus()

    abstract def afterInsert() 

    abstract def beforeDelete() 

	static Closure settings = {
		number_of_shards = 1 // https://www.elastic.co/guide/en/elasticsearch/guide/current/relevance-is-broken.html
		analysis {
			filter {
				german_stop {
					type = "stop"
					stopwords = "_german_"
				}
				german_keywords {
					type = "keyword_marker"
					keywords = [""]
				}
				german_stemmer {
					type = "stemmer"
					language = "light_german"
				}
				shingle_bigram {
					type = "shingle"
					max_shingle_size = 2
					min_shingle_size = 2
					output_unigrams = false
				}
				shingle_trigram {
					type = "shingle"
					max_shingle_size = 3
					min_shingle_size = 3
					output_unigrams = false
				}
			}
			analyzer {
				unigram = {
					tokenizer = "standard"
					filter = [
						"lowercase",
						"german_stop",
						"german_keywords",
						"german_normalization",
						"german_stemmer"
					]
				}
				bigram = {
					tokenizer = "standard"
					filter = [
						"lowercase",
						"german_stop",
						"german_keywords",
						"german_normalization",
						"german_stemmer",
						"shingle_bigram"
					]
				}
				trigram = {
					tokenizer = "standard"
					filter = [
						"lowercase",
						"german_stop",
						"german_keywords",
						"german_normalization",
						"german_stemmer",
						"shingle_trigram"
					]
				}
			}
		}
	}    
}
