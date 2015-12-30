

import lemato.LfileService
import lemato.Corpus
import groovy.time.*
import lemato.es.ElasticsearchClientFactoryBean

class BootStrap {

    LfileService lfileService
    def esClientService

    def importMattisekCorpus(){

        Date start = new Date()

        Corpus c = new Corpus(name:"Mattisek2008", description:'Dieser Corpus wurde verwendet in "Die neoliberale Stadt" von Annika Mattisek im Jahre 2008' ).save(flush:true, failOnError:true)
        
  //    // Frankfurt
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Frankfurt/DerSpiegel/Presse_-_Alle_Sprachen2014-08-06_08-38.TXT"), "DerSpiegel, Frankfurt", "Presse_-_Alle_Sprachen2014-08-06_08-38.TXT")
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Frankfurt/Stern/Presse_-_Alle_Sprachen2014-08-06_08-42.TXT"), "Stern, Frankfurt", "Presse_-_Alle_Sprachen2014-08-06_08-42.TXT")

        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Frankfurt/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-25.TXT"), "TAZ, Frankfurt", "Presse_-_Alle_Sprachen2014-08-06_08-25.TXT")
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Frankfurt/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-26.TXT"), "Stern, Frankfurt", "Presse_-_Alle_Sprachen2014-08-06_08-26.TXT")
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Frankfurt/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-28.TXT"), "Stern, Frankfurt", "Presse_-_Alle_Sprachen2014-08-06_08-28.TXT")
        
        // Köln
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/DerSpiegel/Presse_-_Alle_Sprachen2014-08-06_09-01.TXT"), "DerSpiegel, Koeln", "Presse_-_Alle_Sprachen2014-08-06_09-01.TXT")

        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/Stern/Presse_-_Alle_Sprachen2014-08-06_10-00.TXT"), "Stern, Koeln", "Presse_-_Alle_Sprachen2014-08-06_10-00.TXT")

        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-50.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-50.TXT") 
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-51.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-51.TXT")       
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-53.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-53.TXT")       
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-54.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-54.TXT")       
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-55.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-55.TXT") 
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-56.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-56.TXT")       
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-57.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-57.TXT")       
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Koeln/TAZ/Presse_-_Alle_Sprachen2014-08-06_08-59.TXT"), "TAZ, Koeln", "Presse_-_Alle_Sprachen2014-08-06_08-59.TXT")  

        // Leipzig
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Leipzig/DerSpiegel/Presse_-_Alle_Sprachen2014-08-06_10-05.TXT"), "DerSpiegel, Leipzig", "Presse_-_Alle_Sprachen2014-08-06_10-05.TXT")

        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Leipzig/Stern/Presse_-_Alle_Sprachen2014-08-06_10-06.TXT"), "Stern, Leipzig", "Presse_-_Alle_Sprachen2014-08-06_10-06.TXT")

        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Leipzig/TAZ/Presse_-_Alle_Sprachen2014-08-06_10-03.TXT"), "TAZ, Leipzig", "Presse_-_Alle_Sprachen2014-08-06_10-03.TXT")
        lfileService.importFile(c, new File("/Users/martin/Documents/University/Master/Masterarbeit/corpora/lexisnexis/Leipzig/TAZ/Presse_-_Alle_Sprachen2014-08-06_10-02.TXT"), "TAZ, Leipzig", "Presse_-_Alle_Sprachen2014-08-06_10-02.TXT")        

        println "Elapased time for complete corpus:" + TimeCategory.minus(new Date(), start)
    }

    def init = { servletContext ->
    	// Corpus mattisek2008 = new Corpus(
    	// 								name: "Mattisek2008", 
    	// 								description: 'Dieser Corpus wurde verwendet in "Die neoliberale Stadt" von Annika Mattisek im Jahre 2008'
    	// 							).save(failOnError: true, flush: true)

        

        // importMattisekCorpus()
    }

    def destroy = {
        ElasticsearchClientFactoryBean.instance.close()
    }
}
