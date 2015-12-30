<%@ page import="lemato.es.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.frequency.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>       
        <script src="https://cdn.datatables.net/1.10.8/js/jquery.dataTables.min.js"></script>
        <g:if test="${ textUnit instanceof DocumentRdb}">
            <g:set var="textUnitName" value="${message(code: 'default.document.label', default: 'document')}" />
        </g:if>
        <g:if test="${ textUnit instanceof ParagraphRdb}">
            <g:set var="textUnitName" value="${message(code: 'default.paragraph.label', default: 'paragraph')}" />
        </g:if>
        <g:if test="${ textUnit instanceof SentenceRdb}">
            <g:set var="textUnitName" value="${message(code: 'default.sentence.label', default: 'sentence')}" />
        </g:if>        
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
              <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
              <li class="active"> 
                <a href="${request.getRequestURL}">
                    <g:message code="default.text.show" args="[textUnitName]"></g:message>                
                </a>          
                <!-- <g:message code="default.frequency.details.label"/></a> -->
              </li>
            </ul>
        </div>
        <h1><g:message code="default.text.show" args="[textUnitName]"/> </h1>
        <div class="container">            
            <nav class="navbar navbar-default">
                <div class="container-fluid">
                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                        <ul class="nav navbar-nav navbar-left">                            
                            <li>
                                <g:getParrentLink esObject="${textUnit}"/>
                            </li>                            
                        </ul>
                        <g:if test="${ ! ( textUnit instanceof SentenceRdb ) }" >
                        <ul class="nav navbar-nav navbar-right">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"> <g:getChildrenName esObject="${textUnit}"/> <span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <g:getChildrenLi esObject="${textUnit}" /> 
                                </ul>
                            </li>
                        </ul>
                        </g:if>
                    </div>
                </div>
            </nav>
        </div>

    	<div class="container">
    		<div class="panel panel-primary">
    			<div class="panel-body">
    				<div class="form-group">
    					<label for="tags">Tags:</label>
    					<input type="tags" id="tags" value="${textUnit.tags}" data-role="tagsinput" readonly disabled >
  					</div>
    				<div class="form-group">
    					<label for="publishDate">Publish date:</label>                        
    					<%= textUnit.publishDate.format( 'dd. MM yyyy' ) %>                   
  					</div>
  					
  						<g:formatTextOutput textBody="${textUnit.textBody}" />
  					
    			</div>
    		</div>
    	</div>
    <p></p>
    </body>
</html>