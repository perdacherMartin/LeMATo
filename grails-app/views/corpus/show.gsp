<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'corpus.label', default: 'Corpus')}" />
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
        <div id="show-corpus" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="alert alert-success alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  ${flash.message}
                </div>                
            </g:if>
            <g:if test="${flash.error}">
                <div class="alert alert-danger alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  ${flash.error}
                </div>                
            </g:if>   
            <div class="container">
                <div class="panel panel-primary" >
                    <div class="panel-body">
                            <div class="form-group">                                                      
                                <label for="name" >
                                    <g:message code="lfile.tags.label" default="Tags:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="name" type="text" value="${corpus.name}" name="name" class="form-control"  placeholder="Name" readonly disabled>                                
                            </div> 
                            <div class="form-group">                                                      
                                <label for="description" >
                                    <g:message code="lfile.tags.label" default="Tags:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="description" type="text" value="${corpus.description}" name="description" class="form-control"  placeholder="Description" readonly disabled>
                            </div>  
                            <div class="list-group">
                                <label for="list" >
                                    <g:message code="corpus.lfiles.label" default="Lfiles:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <% corpus.lfiles.each { lfile -> %>
                                <g:link class="list-group-item" resource="lfile" action="show" id="${lfile.id}"><%=lfile.name%></g:link>                                
                                <%}%>                                
                            </div>
                            <g:form resource="${this.corpus}" method="DELETE">
                                <fieldset class="buttons">
                                    <g:link class="edit" action="edit" resource="${this.corpus}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                                </fieldset>
                            </g:form>
                    </div>
                </div>
            </div>

        </div>
    </body>
</html>
