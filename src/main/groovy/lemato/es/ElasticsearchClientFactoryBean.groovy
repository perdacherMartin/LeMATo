
package lemato.es

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import static org.elasticsearch.node.NodeBuilder.nodeBuilder
import lemato.es.*


@Singleton
class ElasticsearchClientFactoryBean implements FactoryBean<Client>, InitializingBean {

	def grailsApplication
	private Client esClientService
	private Node node = null

 	static final SUPPORTED_MODES = ['transport', 'node' ]


	Client getObject() { 
		return esClientService
	}

	Class<Client> getObjectType() { Client }

	boolean isSingleton() { true }

  	void afterPropertiesSet() {
    	String configMode = grailsApplication.config.getProperty('elasticsearch.client.mode')
    	String host = grailsApplication.config.getProperty('elasticsearch.cluster.url')
      int port = Integer.parseInt(grailsApplication.config.getProperty('elasticsearch.cluster.port'))

    	switch ( configMode ){
    		case "node" : this.esClientService = this.getNodeClient()
    			break;

    		case "transport" : 
    			if ( host == null ){
    				throw new NoSuchElementException("There is something wrong with the configuration file. Transport host is null ")
    			}
          if ( port == null ){
            throw new NoSuchElementException("There is something wrong with the configuration file. Transport port is null ")
          }
    			this.esClientService = getTransportClient(host, port)
    			break;

    		default: 
    			throw new NoSuchElementException("There is something wrong with the configuration file. No such element '${configMode}' for property elasticsearch.client.mode.")
    			break;
    	}
    	
    	ElasticsearchInitRest esRest = new ElasticsearchInitRest(grailsApplication)
    	esRest.setupIndexTemplate()
      esRest.setupIndexedScript()    	
   	}

   	def close(){
   		String configNode = grailsApplication.config.getProperty('elasticsearch.client.mode')

   		switch(configNode){
	   		case "node": 
	   			this.node.close()
	   			break;
	   		case "transport":
	   			this.esClientService.close()
	   			break;
			default: break;	   			
   		}   		
   	}

   	/**
   	 * [getNodeClient returns a node client, often used to connect to localhost elasticsearch instance]
   	 * @return [elasticsearch node client]
   	 */

	private Client getNodeClient(){

/*
    // still some issues with elasticseach 2.0:
    
    String clusterName = grailsApplication.config.getProperty('elasticsearch.cluster.name')

      this.node = nodeBuilder().settings {
      path{
        home = "/Users/martin/Documents/University/Master/Masterarbeit/elasticsearch-2.0.0"
      }
      cluster {
        name = clusterName
      }
      node {
        data = false
        master = false
        client = true
      }
    }.node()
    
*/
		String clusterName = grailsApplication.config.getProperty('elasticsearch.cluster.name')
	    this.node = nodeBuilder().settings {
			cluster {
				name = clusterName
			}
			node {
				client = true
			}
		}.node()

        return node.client
	}

	/**
	 * [getTransportClient return a transport client using shield (https://www.elastic.co/guide/en/shield/current/_using_elasticsearch_java_clients_with_shield.html)
	 * often used to connect to an elastic found instance]
	 * @param  urls [list of urls from the applicaton.yml]
	 * @return      [elastcsearch client]
	 */
	private Client getTransportClient(String host, int port){

		String clusterId = grailsApplication.config.getProperty('elasticsearch.transport.clusterId')
		boolean transportSniff = grailsApplication.config.getProperty('elasticsearch.transport.sniff')
		String shieldUser = grailsApplication.config.getProperty('elasticsearch.transport.user')
    boolean enableSsl = grailsApplication.config.getProperty('elasticsearch.transport.enableSsl')

/*
    // still some issues with elasticsearch 2.0:     
    Settings settings = Settings.builder()
        .put("path.home", "/Users/martin/Documents/University/Master/Masterarbeit/elasticsearch-2.0.0")
        .put("transport.ping_schedule", "5s")
        //.put("transport.sniff", false)
        .put("cluster.name", clusterId)
        .put("action.bulk.compress", false)
        .put("client.transport.nodes_sampler_interval", "30s")
        .put("client.transport.ping_timeout", "30s")            
        .put("shield.transport.ssl", enableSsl)
        .put("request.headers.X-Found-Cluster", clusterId)
        .put("shield.user", shieldUser)
        .build();

    Client client = TransportClient.builder()
        .settings(settings).build()        
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port))          
 */

    Settings settings = ImmutableSettings.settingsBuilder()
        .put("transport.ping_schedule", "5s")
        //.put("transport.sniff", false)
        .put("cluster.name", clusterId)
        .put("action.bulk.compress", false)
        .put("client.transport.nodes_sampler_interval", "30s")
        .put("client.transport.ping_timeout", "30s")            
        .put("shield.transport.ssl", enableSsl)
        .put("request.headers.X-Found-Cluster", clusterId)
        .put("shield.user", shieldUser)
        .build();

    Client client = new TransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress(host, port));            
		
		return client
	}

}