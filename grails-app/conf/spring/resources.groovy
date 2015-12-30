

// Place your Spring DSL code here
beans = {

	esClientService(lemato.es.ElasticsearchClientFactoryBean){ bean ->
        bean.factoryMethod = "getInstance"
        bean.singleton = true
		grailsApplication = ref('grailsApplication')
	}
}
