package lemato

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CorpusController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def corpusService

    def index(Integer max) {
        respond Corpus.list(params), model:[corpusCount: Corpus.count()]
    }

    def show(Corpus corpus) {
        respond corpus
    }

    def create() {
        respond new Corpus(params)
    }

    @Transactional
    def save(Corpus corpus) {
        if (corpus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (corpus.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond corpus.errors, view:'create'
            return
        }

        corpus.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'corpus.label', default: 'Corpus'), corpus.id])
                redirect corpus
            }
            '*' { respond corpus, [status: CREATED] }
        }
    }

    def edit(Corpus corpus) {
        respond corpus
    }

    @Transactional
    def update(Corpus corpus) {
        if (corpus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (corpus.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond corpus.errors, view:'edit'
            return
        }

        corpus.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'corpus.label', default: 'Corpus'), corpus.id])
                redirect corpus
            }
            '*'{ respond corpus, [status: OK] }
        }
    }

    @Transactional
    def delete(Corpus corpus) {

        if (corpus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        corpusService.deleteCorpus(corpus)

        corpus.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'corpus.label', default: 'Corpus'), corpus.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'corpus.label', default: 'Corpus'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
