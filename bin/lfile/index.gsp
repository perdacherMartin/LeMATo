<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'lfile.label', default: 'Lfile')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />
        <!-- <script src="https://cdn.datatables.net/1.10.8/js/jquery.dataTables.min.js"></script> -->
    </head>
    <body>
        <a href="#list-lfile" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li class="active"><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-lfile" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
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
            %{-- <f:table collection="${lfileList}" /> --}%
            
            <div class="container-fluid">
                <div class="bootstrap-table">
                    <div class="fixed-table-container" style="padding-bottom: 0px;">
                        <div class="fixed-table-header" style="display: none;">
                            <table></table>
                        </div>
                        <div class="fixed-table-body">
                            <div class="fixed-table-loading" style="top: 42 px; ">Loading please wait...</div>
                            <table id="filetable" class="table table-hover" data-show-footer="true">
                                <thead>
                                    <tr>
                                        <th data-field="0" style="">
                                            <div class="th-inner">Filename</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="1" style="">
                                            <div class="th-inner">Corpus</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="2" style="">
                                            <div class="th-inner">Tags</div>
                                            <div class="fht-cell"></div>                                        
                                        </th>                                    
                                        <th data-field="3" style="text-align:right">
                                            <div class="th-inner">Word count</div>
                                            <div class="fht-cell"></div>                                        
                                        </th>
                                        <th data-field="4" style="text-align:right">
                                            <div class="th-inner">Sentence count</div>
                                            <div class="fht-cell"></div>                                        
                                        </th>
                                        <th data-field="5" style="text-align:right">
                                            <div class="th-inner">Paragraph count</div>
                                            <div class="fht-cell"></div>                                        
                                        </th>
                                        <th data-field="6" style="text-align:right">
                                            <div class="th-inner">Document count</div>
                                            <div class="fht-cell"></div>                                        
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                <% Integer counter = 0 %>
                                <% lfileList.eachWithIndex { lfile, i -> %>
                                    <tr data-index="${i}">
                                        <td style=""><g:link action="show" id="${lfile.id}"> <%= lfile.name %> </g:link></td>
                                        <td style=""><g:link controller="corpus" action="show" id="${lfile.corpus.id}"><%= lfile.corpus.name %> </g:link> </td>
                                        <td style=""><input id="lfilename" type="text" value="${lfile.tags}" name="lfilename" class="form-control" data-role="tagsinput" readonly disabled ></td>
                                        <td style="" align="right"><%= lfile.stats.wordCount %></td>
                                        <td style="" align="right"><%= lfile.stats.sentenceCount %></td>
                                        <td style="" align="right"><%= lfile.stats.paragraphCount %></td>
                                        <td style="" align="right"><%= lfile.stats.docCount %></td>
                                    </tr>
                                <%}%>
                                </tbody>
                                <tfoot>
                                    <th colspan="3" style="text-align:right">Total (current page): <br/> Total: </th>
                                    <th style="text-align:right"></th>
                                    <th style="text-align:right"></th>
                                    <th style="text-align:right"></th>
                                    <th style="text-align:right"></th>
                                </tfoot>
                            </table>
                        </div>
                        <form action="reloadStats">
                            <p>
                              <button type="submit" class="btn btn-default">Update Statistics</button>
                            </p>
                        </form>      
                    </div>
                </div>
            </div>            
        </div>


        <g:javascript>
                $(document).ready(function() {                    
                    $('#filetable').DataTable( {
                        "footerCallback": function ( row, data, start, end, display ) {
                            var api = this.api(), data;
 
                            // Remove the formatting to get integer data for summation
                            var intVal = function ( i ) {
                                return typeof i === 'string' ?
                                    i*1 :
                                    typeof i === 'number' ?
                                        i : 0;
                            };

                            var currentPageColumnSum = function(colnum) {
                                return api
                                    .column( colnum, { page: 'current'} )
                                    .data()
                                    .reduce( function (a, b) {
                                        return intVal(a) + intVal(b);
                                    }, 0 );
                            }

                            // does not work:
                            var totalSum = function(colnum){
                                return api
                                    .column( colnum, { page: 'all'} )
                                    .data()
                                    .reduce( function (a, b) {
                                        return intVal(a) + intVal(b);
                                    } );
                            }

                            // Update footer: Word count
                            $( api.column( 3 ).footer() ).html(                                
                                currentPageColumnSum(3) + '<br />' + totalSum(3)
                            );

                            // Update footer: Sentence count
                            $( api.column( 4 ).footer() ).html(                                
                                currentPageColumnSum(4) + '<br />' + totalSum(4)

                            );

                            // Update footer: Paragraph count
                            $( api.column( 5 ).footer() ).html(                            
                                currentPageColumnSum(5) + '<br />' + totalSum(5)

                            );                                                        

                            // Update footer: Document count
                            $( api.column( 6 ).footer() ).html(                                
                                currentPageColumnSum(6) + '<br />' + totalSum(6)
                            );
                        }
                    } );
                } );
        </g:javascript>
    </body>
</html>