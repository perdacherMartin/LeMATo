<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'corpus.label', default: 'Corpus')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-corpus" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.corpus}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.corpus}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <div class="container">
                <div class="panel panel-primary" >
                    <div class="panel-body">
                        <g:form resource="${this.corpus}" method="PUT">
                            <g:hiddenField name="version" value="${this.corpus?.version}" />
                            
                            <div class="form-group">                                                      
                                <label for="name" >
                                    <g:message code="corpus.name.label" default="Name:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="name" type="text" value="${corpus.name}" name="name" class="form-control"  placeholder="Name" readonly disabled>                                
                            </div> 
                            <div class="form-group">                                                      
                                <label for="description" >
                                    <g:message code="corpus.description.label" default="Description:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="description" type="text" value="${corpus.description}" name="description" class="form-control"  placeholder="Description" >
                            </div> 
                            <fieldset class="buttons">
                                <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                            </fieldset>
                        </g:form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
