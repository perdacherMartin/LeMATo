<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.characteristics.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>
        <link rel="stylesheet" href="${resource(dir: 'stylesheets', file: 'dendrogramLayout.css')}" type="text/css">
        <link rel="stylesheet" href="/webjars/jquery-ui/1.11.4/jquery-ui.min.css">
        <link rel="stylesheet" href="/webjars/footable/2.0.3/css/footable.core.min.css">
        <script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1.1','packages':['corechart']}]}"></script>
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />
        <asset:javascript src="/webjars/footable/2.0.3/js/footable.js" />


        <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>  

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

        <% significanceBuckets.eachWithIndex{ tag, bucketList, i -> %>
            <g:buildScatterChart significanceBucketList="${bucketList}" 
                textUnitName="${textUnitName}" divTagName="scatter-container-${tag}" />
        <% } %>
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="index" action="index"><g:message code="default.characteristics.select.label"/></g:link></li>
                <li class="active"><g:link class="index" action="run"><g:message code="default.frequency.label" /></g:link></li>
            </ul>
        </div>
        <div class="content scaffold-list" role="main">
            <h1><g:message code="default.characteristics.label" /></h1>
            <% significanceBuckets.eachWithIndex{ tag, bucketList, i -> %>
            <h2><g:message code="default.characteristics.tag.label" args="[tag]" /></h2>

            <div id="dendro-container-1" class="container">
                <div id="dendrogram-container-${tag}">
                </div>
            </div>
            <div class="container">
                <div id="scatter-container-${tag}" style="width: 900px; height: 500px;">
                </div>
            </div>
            <div class="container">
                <div class="bootstrap-table">
                    <div class="fixed-table-container" style="padding-bottom: 0px;">
                        <div class="fixed-table-header" style="display: none;">
                            <table></table>
                        </div>
                        <div class="fixed-table-body">
                            <div class="fixed-table-loading" style="top: 42 px; ">Loading please wait...</div>
                            <table id="${tag}" 
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
                                <% bucketList.eachWithIndex{ bucket, j -> %>
                                    <tr>                                        
                                        <td>
                                            <a href="/concordance/kwic?corpusId=${corpusId}&keyword=${bucket.key}&query=tags:${tag}"><%= bucket.key %></a>
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
            <% } %>
        </div>

        <asset:javascript src="d3jsdendrogram.js" />

        <script type="text/javascript">                
         	<% significanceBuckets.keySet().each{ key -> %>
         		$('#${key}').bootstrapTable({                    
                });

                var jsonData = <%= jsonData.get(key) %> 
                dendrogram('#dendrogram-container-${key}', jsonData)
         	<% } %>

            $(function () {
                $('.footable').footable();
            });

        </script>
    </body>
</html>