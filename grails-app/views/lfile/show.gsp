<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'lfile.label', default: 'Lfile')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-lfile" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="alert alert-success alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  ${flash.message}
                </div>
            </g:if>
            <div class="container">
                <div class="panel panel-primary" >
                    <div class="panel-body">
                        <g:form resource="${this.lfile}" method="DELETE">

                            <div class="form-group">                                       
                                <label for="lfilename">
                                    <g:message code="lfile.filename.label" default="Filename:" />
                                    %{-- <span class="required-indicator">*</span>                        --}%
                                </label>
                                <input id="lfilename" type="text" value="${lfile.name}" name="lfilename" class="form-control" placeholder="filename" readonly disabled>
                            </div>
                            <div class="form-group">                                       
                                <label for="corpus">
                                    <g:message code="corpus.entity.label" default="Corpus:" />
                                    %{-- <span class="required-indicator">*</span>                        --}%
                                </label>
                                <input id="corpus" type="text" value="${lfile.corpus.name}" name="corpus" class="form-control" placeholder="corpus" readonly disabled>
                            </div>
                            <div class="form-group">                                                      
                                <label for="tags" >
                                    <g:message code="lfile.tags.label" default="Tags:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="lfilename" type="text" value="${lfile.tags}" name="lfilename" class="form-control" data-role="tagsinput" readonly disabled >
                            </div>
                            <!--
                            <div class="form-group">        
                                <ul class="list-group">
                                    <% lfile.documents.eachWithIndex { document, i -> %>
                                        <li class="list-group-item"><g:link controller="frequency" action="filterTextUnits" id="${document.id}" params="[corpus: '${lfile.corpus.id}']"> document </g:link> </li>
                                    <% } %>
                                </ul>
                            </div>
                            -->
                            <fieldset class="buttons">
                                <g:link class="edit" action="edit" resource="${this.lfile}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                                <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                            </fieldset>
                        </g:form>
        </div>
    </body>
</html>
