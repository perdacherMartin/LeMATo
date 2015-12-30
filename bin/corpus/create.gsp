<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'corpus.label', default: 'Corpus')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-corpus" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li class="active"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-corpus" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:if test="${flash.error}">
              
            </g:if>              
            <g:hasErrors bean="${this.corpus}">
                <g:eachError bean="${this.corpus}" var="error">                
                    <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <g:message error="${error}"/>
                    </div>  
                </g:eachError>            
            </g:hasErrors>
            <div class="container">
                <div class="panel panel-primary" >
                    <div class="panel-body">
                        <g:form action="save">
                            <div class="form-group">                                                      
                                <label for="name" >
                                    <g:message code="corpus.name.label" default="Name:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="name" type="text" value="" name="name" class="form-control"  placeholder="Name">
                            </div> 
                            <div class="form-group">                                                      
                                <label for="description" >
                                    <g:message code="lfile.description.label" default="Description:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="description" type="text" value="" name="description" class="form-control"  placeholder="Description">                                
                            </div>                             
                            <fieldset class="buttons">
                                <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                            </fieldset>
                        </g:form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
