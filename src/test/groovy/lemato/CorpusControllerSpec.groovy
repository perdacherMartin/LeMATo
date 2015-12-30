package lemato

import grails.test.mixin.*
import spock.lang.*

@TestFor(CorpusController)
@Mock(Corpus)
class CorpusControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.corpusList
            model.corpusCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.corpus!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def corpus = new Corpus()
            corpus.validate()
            controller.save(corpus)

        then:"The create view is rendered again with the correct model"
            model.corpus!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            corpus = new Corpus(params)

            controller.save(corpus)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/corpus/show/1'
            controller.flash.message != null
            Corpus.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def corpus = new Corpus(params)
            controller.show(corpus)

        then:"A model is populated containing the domain instance"
            model.corpus == corpus
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def corpus = new Corpus(params)
            controller.edit(corpus)

        then:"A model is populated containing the domain instance"
            model.corpus == corpus
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/corpus/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def corpus = new Corpus()
            corpus.validate()
            controller.update(corpus)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.corpus == corpus

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            corpus = new Corpus(params).save(flush: true)
            controller.update(corpus)

        then:"A redirect is issued to the show action"
            corpus != null
            response.redirectedUrl == "/corpus/show/$corpus.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/corpus/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def corpus = new Corpus(params).save(flush: true)

        then:"It exists"
            Corpus.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(corpus)

        then:"The instance is deleted"
            Corpus.count() == 0
            response.redirectedUrl == '/corpus/index'
            flash.message != null
    }
}
