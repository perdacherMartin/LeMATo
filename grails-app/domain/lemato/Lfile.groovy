package lemato

import lemato.es.DocumentRdb

class Lfile {
	static belongsTo = [corpus: Corpus]
	static hasMany = [documents: DocumentRdb]

	String name
	String tags

	LfileStatistics stats

    static constraints = {
    	name(blank:false, nullable:false, unique: true)
    	stats(nullable:true)
    }
}
