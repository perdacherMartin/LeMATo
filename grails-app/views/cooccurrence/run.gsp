<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.characteristics.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>
        <link rel="stylesheet" href="${resource(dir: 'stylesheets', file: 'dendrogramLayout.css')}" type="text/css">
        <script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1.1','packages':['corechart']}]}"></script>
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />

        <g:set var="textUnitName" value="-" />
        <g:if test="${params.textUnit == 'document'}">
            <g:set var="textUnitName" value="${g.message(code: "default.document.label")}"/>
        </g:if>
        <g:if test="${params.textUnit == 'paragraph'}">
            <g:set var="textUnitName" value="${g.message(code: "default.paragraph.label")}"/>
        </g:if>
        <g:if test="${params.textUnit == 'sentence'}">
            <g:set var="textUnitName" value="${g.message(code: "default.sentence.label")}"/>
        </g:if>

        
        <g:buildScatterChart significanceBucketList="${buckets}" 
            textUnitName="${textUnitName}" divTagName="scatter-container" />
        

    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="index" action="index"><g:message code="default.cooccurrence.select.label"/></g:link></li>
                <li class="active"><g:link action=""><g:message code="default.cooccurrence.label"></g:message></g:link></li>
            </ul>
        </div>
        <div class="content scaffold-list" role="main">
            <h1><g:message code="default.cooccurrence.label" /></h1>
            
            <div class="container">
                <g:if test="${params.luceneQuery}" >
                    <div class="alert alert-success alert-dismissible" role="alert">
                      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      <g:message code="default.filtering.byquery.label" args="[params.luceneQuery]"/>
                    </div>                 
                </g:if> 

                <div id="dendrogram-container" style="width: 900px; height: 500px;">
                </div>
            </div>
            <div class="container">
                <div id="scatter-container" style="width: 900px; height: 500px;">
                </div>
            </div>
            
            <g:form url="[action:'kwic',controller:'concordance']" >
                <g:hiddenField id="corpusId" name="corpus.id" value="${params.int('corpus.id')}" />
                <g:hiddenField id="keyword" name="keyword" value ="" />
                <g:hiddenField id="query" name="query" value="${params.luceneQuery}" />
                <input id="hiddenSubmit" type="submit" hidden="true">
            </g:form>

            <div class="container">
                <div class="bootstrap-table">
                    <div class="fixed-table-container" style="padding-bottom: 0px;">
                        <div class="fixed-table-header" style="display: none;">
                            <table></table>
                        </div>
                        <div class="fixed-table-body">
                            <div class="fixed-table-loading" style="top: 42 px; ">Loading please wait...</div>
                            <table id="significance-table" 
                                class="table table-hover" 
                                data-pagination="true" 
                                data-search="true">
                                <thead>
                                    <tr>
                                        <th data-field="0" style="" >
                                            <div class="th-inner">Keyword</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="1">
                                            <div class="th-inner">TextUnit count</div>
                                            <div class="fht-cell"></div>                                       
                                        </th>                                        
                                        <th data-field="2">
                                            <div class="th-inner">Score</div>
                                            <div class="fht-cell"></div>
                                        </th>                                    
                                        <th data-field="3" class="text-center">
                                            <div class="th-inner">Subset df</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="4" >
                                            <div class="th-inner">Subset size</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="5" >
                                            <div class="th-inner">Superset df</div>
                                            <div class="fht-cell"></div>
                                        </th>  
                                        <th data-field="6" >
                                            <div class="th-inner">Superset size</div>
                                            <div class="fht-cell"></div>
                                        </th>                                                                                  
                                    </tr>
                                </thead>                            
                                <tbody> 
                                <% buckets.eachWithIndex{ bucket, j -> %>
                                    <tr>                                        
                                        <td>
                                            <button type="button" class="btn btn-link" onClick="kwicSubmit('${bucket.key}')"><%= bucket.key %></button>
                                        </td>
                                        <td style=""> <%= bucket.docCount %></td>
                                        <td class="text-right"> <%= bucket.score %> </td>
                                        <td class="text-left"> <%= bucket.subsetDf %> </td>
                                        <td class="text-left"> <%= bucket.subsetSize %> </td>
                                        <td class="text-left"> <%= bucket.supersetDf %> </td>
                                        <td class="text-left"> <%= bucket.supersetSize %> </td>
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
        </div>

        <asset:javascript src="d3jsdendrogram.js" />

        <script type="text/javascript">                
         	
         	$('#significance-table').bootstrapTable({                    
            });

            dendrogram('#dendrogram-container', <%= jsonData %> );

            kwicSubmit = function(keyword){            
                $('#keyword').val(keyword)
                $('#hiddenSubmit').click()
            }

         	
        </script>
    </body>
</html>