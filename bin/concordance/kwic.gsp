<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.frequency.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>       
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li class="active"><g:link class="index" action="index"><g:message code="default.frequency.label" /></g:link></li>                
            </ul>
        </div>
        <div id="list-corpus" class="content scaffold-list" role="main">
            <h1><g:message code="default.frequency.label" /></h1>
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
                <div id="chart_div">
                </div>
            </div>
            <div class="container">
                <div class="alert alert-warning alert-dismissible" role="alert">
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <strong>Warning!</strong> Some keywords have 0 frequency. These keywords got filterd by the stop-words filter.
                </div>
            </div>      
            <div class="container">
                <div id="toolbar">
                    <button id="viewDetail" class="btn btn-primary" >
                        <i class="glyphicon glyphicon-check"></i>Show detail for selected
                    </button>
                </div>
                <div class="bootstrap-table">
                    <div class="fixed-table-container" style="padding-bottom: 0px;">
                        <div class="fixed-table-header" style="display: none;">
                            <table></table>
                        </div>
                        <div class="fixed-table-body">
                            <div class="fixed-table-loading" style="top: 42 px; ">Loading please wait...</div>
                            <table id="filetable" 
                                class="table 
                                table-hover" 
                                data-show-footer="true" 
                                data-pagination="true" 
                                data-search="true"
                                data-click-to-select="true" 
                                data-select-item-name="selectItemName" >
                                <thead>
                                    <tr>
                                        <th data-field="state" data-checkbox="true" style="" >
                                            <div class="th-inner">Date</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="termFreq">
                                            <div class="th-inner">Tag</div>
                                            <div class="fht-cell"></div>                                       
                                        </th>                                        
                                        <th data-field="rank" style="" >
                                            <div class="th-inner"><!-- fragment left --></div>
                                            <div class="fht-cell"></div>
                                        </th>                                    
                                        <th data-field="name" style="">
                                            <div class="th-inner">Fragment</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="docFreq">
                                            <div class="th-inner"><!-- fragment right --></div>
                                            <div class="fht-cell"></div>
                                        </th>   
                                    </tr>
                                </thead>                            
                                <tbody> 
                                    <% results.eachWithIndex{ result, i -> %> 
                                    <tr>                                        
                                        <td>
		                                	<%= result.textUnit.publishDate %>
                                        </td>
                                        <td style="" > <%= result.textUnit.tags %></td>
                                        <td style="" align="right"> <g:getFragment fragment="${result.fragment}" part="before"></g:getFragment> </td>
                                        <td style="" align="center"> <g:getFragment fragment="${result.fragment}" part="center"></g:getFragment> </td>
                                        <td style="" align="left"> <g:getFragment fragment="${result.fragment}" part="after"></g:getFragment> </td>
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

         <script type="text/javascript">

                $('#filetable').bootstrapTable({                    
                });

        </script>       

    </body>
</html>