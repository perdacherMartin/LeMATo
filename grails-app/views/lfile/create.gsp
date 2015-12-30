<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'lfile.label', default: 'Lfile')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li class="active"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-lfile" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
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
            <g:hasErrors bean="${this.lfile}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.lfile}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <div class="container">
                <div class="panel panel-primary" >
                    <div class="panel-body">
                        <g:uploadForm action="save" class="form-horizontal" role="form" >
                            <div class="form-group">                                       
                                <label for="corpus">
                                    <g:message code="corpus.entity.label" default="Corpus:" />
                                    %{-- <span class="required-indicator">*</span>                        --}%
                                </label>
                                <g:select id="corpus" name="corpus.id" class="form-control" from="${lemato.Corpus.list()}" optionKey="id" 
                                    required="" value="${lfileInstance?.corpus?.id}" class="many-to-one" 
                                    noSelection="${['null':'Select One...']}"
                                    optionValue="name" />
                            </div>

                            <div class="form-group">
                                <label for="lexisfile" >
                                    <g:message code="lfile.file.label" default="Filename:" />
                                    %{-- <span class="required-indicator">*</span> --}%
                                </label>
                                <input type="file" id="lexisfile" class="form-control" name="lexisfile" multiple="true" accept=".txt">
                                <p class="help-block"><g:message code="lfile.file.choose" default="Choose file(s)..." /></p>
                            </div>                    

                            <div class="form-group">                                                      
                                <label for="tags" >
                                    <g:message code="lfile.tags.label" default="Tags:" />
                                    %{-- <span class="required-indicator">*</span>  --}%                      
                                </label>
                                <input id="tags" type="text" value="" name="tags" class="form-control" data-role="tagsinput" placeholder="Tags">
                                <p class="help-block"><g:message code="lfile.tags.help" default="Tags are used to divide into different subcorpora." /></p>
                            </div>                      
                            
                                <g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.button.create.label', default: 'Create')}" />

                        </g:uploadForm>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
