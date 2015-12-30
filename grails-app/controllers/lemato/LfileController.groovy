package lemato

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.validation.ValidationException 

import lemato.es.ElasticsearchService

@Transactional(readOnly = true)
class LfileController {

    def lfileService
    ElasticsearchService elasticsearchService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        respond Lfile.list(), model:[lfileCount: Lfile.count()]
    }

    def show(Lfile lfile) {
        respond lfile
    }

    def create() {
        // respond new Lfile(params)
    }

    def reloadStats() {
        lfileService.updateFileStatistics()
        redirect(action: 'index')
    }

    @Transactional
    def save() {
        Integer fileCount = 0        
        String tags = params.tags
        // tags = tags.replaceAll("\\[","").replaceAll("\\]","")
        Corpus c = Corpus.get(params.int('corpus.id'))

        if (c == null) {
            transactionStatus.setRollbackOnly()
            flash.error = "Choose a valid corpus!"
            respond "", view:'create'
            return
        }

        request.getFiles("lexisfile").each { file ->
            File tFile = File.createTempFile("tmpfile", "txt", new File(System.getProperty("java.io.tmpdir") ) )
            def lexisfile = file

            lexisfile.transferTo(tFile)

            tFile.deleteOnExit()
            
            try{
                lfileService.importFile(c, tFile, tags, file.getOriginalFilename() )
                fileCount++
            }catch(ValidationException e){
                flash.error = e.getMessage()
            }                                
        }

        redirect(action: 'index')        

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'lfile.imported.message', args: [fileCount, message(code: 'lfile.label', default: 'file')])                 
            }
            '*' { [status: CREATED] }
        }
    }

    def edit(Lfile lfile) {
        respond lfile
    }

    @Transactional
    def update(Lfile lfile) {
        if (lfile == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (lfile.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond lfile.errors, view:'edit'
            return
        }

        lfile.save flush:true

        println "" + lfile.name

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'lfile.label', default: 'Lfile'), lfile.id])
                redirect lfile
            }
            '*'{ respond lfile, [status: OK] }
        }
    }

    @Transactional
    def delete(Lfile lfile) {

        if (lfile == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        lfileService.deleteFile(lfile)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'lfile.label', default: 'Lfile'), lfile.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'lfile.label', default: 'Lfile'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
