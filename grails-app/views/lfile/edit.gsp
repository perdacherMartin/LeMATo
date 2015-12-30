<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'lfile.label', default: 'Lfile')}" />
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
        <div id="edit-lfile" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="alert alert-success" role="alert">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.lfile}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.lfile}" var="error">
                    <div class="alert alert-danger" role="alert"><g:message error="${error}"/></div>
                %{-- <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li> --}%
                </g:eachError>
            </ul>
            </g:hasErrors>
            <div class="container">
                <div class="panel panel-primary" >
                    <div class="panel-body">
                <g:form resource="${this.lfile}" method="PUT">
                <g:hiddenField name="version" value="${this.lfile?.version}" />
                
                <div class="form-group">                                       
                    <label for="lfilename">
                        <g:message code="lfile.filename.label" default="Filename:" />
                    </label>
                    <input id="name" type="text" value="${lfile.name}" name="name" class="form-control" placeholder="filename" />
                </div>

                <div class="form-group">                                                      
                    <label for="tags" >
                        <g:message code="lfile.tags.label" default="Tags:" />
                        %{-- <span class="required-indicator">*</span>  --}%                      
                    </label>
                    <input id="tags" type="text" value="${lfile.tags}" name="tags" class="form-control" data-role="tagsinput" >
                </div>   
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
