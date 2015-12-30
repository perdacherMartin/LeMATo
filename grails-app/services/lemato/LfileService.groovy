package lemato

import grails.transaction.Transactional

import grails.core.*
import lemato.es.ElasticsearchService
import lemato.es.ImportService

class LfileService {

	GrailsApplication grailsApplication
    ElasticsearchService elasticsearchService
    ImportService importService 

    @Transactional
    LfileStatistics getStatisticForLfile(Lfile lfile){

        Long sentenceCount = 0

        lfile.documents.each { doc -> 
            sentenceCount += doc.paragraphs.sum{ paragraph -> 
                paragraph.sentences.size()
            }
        }
        
        return new LfileStatistics( docCount: lfile.documents.size(),
                        paragraphCount: lfile.documents.sum { it.paragraphs.size() }, 
                        sentenceCount: sentenceCount,                        
                        wordCount: elasticsearchService.getWordCountForDocumentIds(lfile.documents*.id.collect{ it.toString() }, lfile.corpus )
                    ).save(flush:true, failOnError:true)
    }

	@Transactional
    def importFile(Corpus c, File f, String tags, String filename) {
    	// ElasticsearchRepository repo = new ElasticsearchRepository(c)
        println "importing: ${filename}"
    	Lfile lfile = new Lfile(name:filename, corpus: c, tags:tags).save(flush:true, failOnError:true)

        importService.importFileToElasticsearch(lfile, f, tags)        

        lfile.save(flush:true, failOnError:true)

        lfile.stats = getStatisticForLfile(lfile)        
    	lfile.save(flush:true, failOnError:true)
    }

    @Transactional
    def updateFileStatistics() {
        def lfiles = Lfile.list()

        lfiles.each { lfile -> 
            lfile.stats = getStatisticForLfile(lfile)
            lfile.stats.save( failOnError: true)
            lfile.save(failOnError: true)                        
        }
    }    

    @Transactional
    def deleteFile(Lfile lfile, Boolean rdbDelete = true){
        if ( rdbDelete ){
            lfile.delete flush:true
        }
    }
}
