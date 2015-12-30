package lemato.es

import java.net.*
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.Settings
import java.io.*
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import grails.core.GrailsApplication

import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT


class ElasticsearchInitRest {

	GrailsApplication grailsApplication
	String host
	int port 
	def elasticsearchInitRest

	public ElasticsearchInitRest(GrailsApplication grailsApplication){
		this.grailsApplication = grailsApplication
		this.host = grailsApplication.config.getProperty('elasticsearch.cluster.url')		
		this.port = Integer.parseInt(grailsApplication.config.getProperty('elasticsearch.cluster.port'))
	}

	def setupIndexTemplate(){		
      	String templateName = grailsApplication.config.getProperty('elasticsearch.template.name')
    	
		String url = host + ":" + port  
		String path = "/_template/" + templateName

		HTTPBuilder http = this.getHttpBuilder(url);

		if ( !uriPathExists(http,path) ){
			println "creating index template"
			createLematoIndexTemplate(http, path)
		}
	}

	def setupIndexedScript(){
		String url = host + ":" + port

		String path = "/_scripts/groovy/termFrequency"

		HTTPBuilder http = this.getHttpBuilder(url);		

		if ( !uriPathExists(http,path) ) {
			println "creating indexed script"
			createIndexedScript(http,path,"_index['textBody'][term].tf()")
		}
	}

	private Boolean uriPathExists(HTTPBuilder http, String path){		
		http.request( GET, JSON ) { 
		    uri.path = path
		    requestContentType = JSON;
		    headers.query = ''
		    headers.Accept = 'application/json';

		    response.success = { resp, json ->
		        return true
		    }

		    response.failure = { resp, json -> 
		    	return false
		    }
		}
	}

	private HTTPBuilder getHttpBuilder(String url){
		HTTPBuilder http  = new HTTPBuilder( url )
      	String shieldUser = grailsApplication.config.getProperty('elasticsearch.transport.user')

		if ( grailsApplication.config.getProperty('elasticsearch.client.mode') == "transport" && 
			 shieldUser != null ){
			println "added auth"
			http.auth.basic shieldUser.substring(0, shieldUser.lastIndexOf(":")) , shieldUser.substring(shieldUser.lastIndexOf(":")+1)
		}

		return http
	}

	private createLematoIndexTemplate(HTTPBuilder http, String path){

		String templateprefix = grailsApplication.config.getProperty('elasticsearch.template.prefix')

		Closure templateIndexBody = {              
			template templateprefix
			settings = ElasticObject.settings
            mappings {
                document {
                    properties = DocumentRdb.properties
                }
                paragraph {
                    properties = ParagraphRdb.properties
                }
                sentence {
                    properties = SentenceRdb.properties
                }
            }
        }

        http.request( POST, JSON ) { req ->
		    uri.path = path
		    requestContentType = JSON;
		    headers.query = ''
		    headers.Accept = 'application/json';        	
    		body = templateIndexBody.asJsonString()

    		response.success = { resp, json ->
    			println "createLematoIndexTemplate success"
    		}

    		response.failure = { resp, json ->
    			println "createLematoIndexTemplate failure"
    			println json
    		}
		}
	}

	private createIndexedScript(HTTPBuilder http, String path, String content){
		
		Closure indexedScriptContent = {
  			script = "_index['textBody'][term].tf()"
		}

		http.request( POST, JSON ) { req ->
		    uri.path = path
		    requestContentType = JSON;
		    headers.query = ''
		    headers.Accept = 'application/json';
			body = indexedScriptContent.asJsonString()

			response.success = { resp, json ->
				println "createIndexedScript success"
    		}

    		response.failure = { resp, json ->
    			println "createIndexedScript failure"
    			println json
    		}
		}
	}


}