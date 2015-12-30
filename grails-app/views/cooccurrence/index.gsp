<%@ page import="lemato.es.TextUnit" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <title><g:message code="default.characteristics.select.label" /></title>
    </head>
    <body>        
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li class="active"><g:link class="index" action="index"><g:message code="default.cooccurrence.label" /></g:link></li>
            </ul>
        </div>
        <div id="create-corpus" class="content scaffold-create" role="main">
            <h1><g:message code="default.cooccurrence.label"/></h1>
            <g:if test="${flash.message}">
            	<div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:if test="${flash.error}">
                <div class="alert alert-danger alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  ${flash.error}
                </div>                
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
                        <g:form action="run">
                            <div class="form-group">                                       
                                <label for="corpus">
                                    <g:message code="corpus.entity.label" default="Corpus:" />
                                </label>
                                <g:select id="corpus" name="corpus.id" class="form-control" from="${lemato.Corpus.list()}" optionKey="id" 
                                    required="" value="${lfileInstance?.corpus?.id}" class="many-to-one" 
                                    noSelection="${['null':'Select One...']}"
                                    optionValue="name" />
                            </div>                        
                            <div class="form-group">                                                      
                                <label for="luceneQuery" >
                                    <g:message code="cooccurrence.query.label" default="Centered word:" />
                                </label>
                                <input id="luceneQuery" type="text" value="" name="luceneQuery" class="form-control" >
                                <p class="help-block"><g:message code="cooccurrence.query.help" default="Define your foreground with a lucene query."/></p>
                            </div> 
                            <div class="form-group">
                                <label for="size" >
                                    <g:message code="default.size.label" default="Size:" />
                                </label>
                                <g:select id="size" noSelection="${['null':'Select One...']}" from="${[10,20,50,100]}" name="size">
                                </g:select>                                 
                            </div>
                            <div class="form-group">                                                      
                                <label for="textUnit" >
                                    <g:message code="default.textUnit.label" default="TextUnit:" />
                                </label>
                                <g:select name="textUnit" id="textUnit" from="${TextUnit.values()}" optionKey="key" noSelection="${['null':'Select One...']}"/>
                            </div>                                           
                            <fieldset class="buttons">
                                <g:submitButton name="create" class="save" value="${message(code: 'default.button.submit', default: 'Start analysis')}" />
                            </fieldset>
                        </g:form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
