<%@ page import="lemato.es.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.frequency.label" /></title>
        <g:if test="${textUnits[0].text.typeName == DocumentRdb.typeName}">
            <g:set var="textUnitName" value="${message(code: 'default.document.plural', default: 'documents')}" />
        </g:if>
        <g:if test="${textUnits[0].text.typeName == ParagraphRdb.typeName}">
            <g:set var="textUnitName" value="${message(code: 'default.paragraph.plural', default: 'paragraphs')}" />
        </g:if>
        <g:if test="${textUnits[0].text.typeName == SentenceRdb.typeName}">
            <g:set var="textUnitName" value="${message(code: 'default.sentence.plural', default: 'sentences')}" />
        </g:if>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>       
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
              <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
              <li><g:link class="index" action="index"><g:message code="default.frequency.label" /></g:link></li>
              <li class="active"> 
                <a href="${request.getRequestURL}">
                    <g:message code="default.text.list" args="[textUnitName]"></g:message>                
                </a>          
                <!-- <g:message code="default.frequency.details.label"/></a> -->
              </li>
            </ul>
        </div>  
            <div class="container">
                <h1>List of ${textUnitName} for the keyword '${keyWord}' in the year ${params.year}</h1>
                <g:if test="${params.query}" >
                    <div class="alert alert-success alert-dismissible" role="alert">
                      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      <g:message code="default.filtering.byquery.label" args="[params.query]"/>
                    </div>                 
                </g:if>
            </div>
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
            <g:if test="${textUnits.size() == maxResults }">
                <div class="alert alert-warning alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  <strong>Warning!</strong> Only ${maxResults} results are displayed in this page!
                </div>
            </g:if>

            <div class="container">
                <div class="bootstrap-table">
                    <div class="fixed-table-container" style="padding-bottom: 0px;">
                        <div class="fixed-table-header" style="display: none;">
                            <table></table>
                        </div>
                        <div class="fixed-table-body">
                            <div class="fixed-table-loading" style="top: 42 px; ">Loading please wait...</div>
                            <table id="textunitstable" 
                                class="table table-hover"                                 
                                data-pagination="true" 
                                data-search="true"                                
                                >
                                <thead>
                                    <tr>
                                        <th data-field="state" data-sortable="true" style="" >
                                            <div class="th-inner">Tags</div>
                                            <div class="fht-cell"></div>
                                        </th>                                        
                                        <th data-field="rank" data-sortable="true" style="" >
                                            <div class="th-inner">Date</div>
                                            <div class="fht-cell"></div>
                                        </th>                                    
                                        <th data-field="name" style="">
                                            <div class="th-inner">Starts with</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="docFreq" data-sortable="true" style="text-align:right">
                                            <div class="th-inner">Keyword frequency</div>
                                            <div class="fht-cell"></div>
                                        </th>   
                                    </tr>
                                </thead>                            
                                <tbody> 
                                    <% textUnits.eachWithIndex{ textUnit, i -> %> 
                                    <tr>                                        
                                        <td style=""><input id="tags" type="text" value="${textUnit.text.tags}" name="tags" class="form-control" data-role="tagsinput" readonly disabled ></td>
                                        <td style="" > <%= textUnit.text.publishDate.format('yyyy-MM-dd') %> </td>
                                        <td style="" >
                                            <a href="/view/text/${textUnit.text.id}?highlight=${params.key}">
                                                <g:getTitle textBody="${textUnit.text.textBody}" n="100" />
                                            </a>
                                        </td>
                                        <td style="" align="right"> <%= textUnit.count %> </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                                <tfoot>

                                </tfoot>
                            </table>
                        </div>                   
                    </div>
                </div>
            </div> 

            <script>
                $('#textunitstable').bootstrapTable({});
                $('#textunitstable').bootstrapTable({                    
                    }).on('page-change.bs.table', function (e, size, number) {
                        jQuery("[id=tags]").tagsinput({});
                }) 
            </script>
    </body>
</html>