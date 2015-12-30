package lemato

class Corpus {

    def grailsApplication

	static hasMany = [lfiles: Lfile]

	String name
	String description 
    Boolean hasChanged = true

    String getIndexName(){
        String prefix = grailsApplication.config.getProperty('elasticsearch.template.prefix')  
        prefix = prefix.replaceAll("\\*", "")
        prefix + name.replaceAll("\\s", "").toLowerCase()
    }

    static transients = ['grailsApplication', 'indexName']

    static constraints = {
    	name blank:false, nullable:false, unique:true
    	description blank:true, nullable:true
    }

    static mapping = {
    	description type: 'text'
    }
}
