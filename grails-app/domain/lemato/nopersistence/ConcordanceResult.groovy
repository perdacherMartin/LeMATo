package lemato.nopersistence

import lemato.es.ElasticObject

class ConcordanceResult {
	
	static mapWith = "none"

	String fragment // whole fragment with <mark></mark>
	String stemmedCenter // stemmed version of centered word
	ElasticObject textUnit

}