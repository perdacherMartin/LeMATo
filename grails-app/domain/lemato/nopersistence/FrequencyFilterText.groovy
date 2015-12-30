package lemato.nopersistence

import lemato.es.*

class FrequencyFilterText {

	static mapWith = "none"

	static belongsTo = [text: ElasticObject] 
	Long count // how often keyword occurs (see FrequencyController/filterDocument)

}