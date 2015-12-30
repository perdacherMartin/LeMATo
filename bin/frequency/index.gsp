<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.frequency.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>       
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />

        <g:javascript>
          google.load('visualization', '1.0', {'packages':['corechart']});
          google.setOnLoadCallback(drawChart);

          function drawChart() {

            // Create the data table.
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            data.addColumn('number', 'Term frequency');
            data.addRows([
            <%chartYears.eachWithIndex{ cYear, index -> %>
                ['${cYear}', <%=chartCounts[index]%>],
            <%}%>
            ]);

            // Set chart options
            var options = {'title':'Term freqeuncy distribution over years',
                           'width':400,
                           'height':300};

            // Instantiate and draw our chart, passing in some options.
            var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
            chart.draw(data, options);
          }
        </g:javascript>
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
                                            <div class="th-inner">Selection</div>
                                            <div class="fht-cell"></div>
                                        </th>                                        
                                        <th data-field="rank" style="" >
                                            <div class="th-inner">Rank</div>
                                            <div class="fht-cell"></div>
                                        </th>                                    
                                        <th data-field="name" style="">
                                            <div class="th-inner">Keyword</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="docFreq" style="text-align:right">
                                            <div class="th-inner">Document Freqeuncy</div>
                                            <div class="fht-cell"></div>
                                        </th>   
                                        <th data-field="termFreq" style="text-align:right">
                                            <div class="th-inner">Term Freqeuncy</div>
                                            <div class="fht-cell"></div>                                       
                                        </th>
                                        <th data-field="kendall" style="text-align:right">
                                            <div class="th-inner">Kendall's &Tau; <br> (on document counts over years)</div>
                                            <div class="fht-cell"></div>            
                                        </th>
                                    </tr>
                                </thead>                            
                                <tbody> 
                                    <% frequencies.eachWithIndex{ frequency, i -> %> 
                                    <tr>                                        
                                        <td class="bs-checkbox">
                                            <input data-index="${frequency.keyword}" name="btSelectItem" data-index="${i}" value="${frequency.keyword}" type="checkbox">
                                        </td>
                                        <td style="" > <%=i + 1 %> </td>
                                        <td style="" > <%=frequency.keyword %> </td>
                                        <td style="" align="right"> <%=frequency.docCount %> </td>
                                        <td style="" align="right"> <%=frequency.termFrequency %> </td>
                                        <td style="" align="right"> <%=String.format("%.2f", frequency.kendallTauOnDocCount) %> </td>
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

                document.getElementById('viewDetail').onclick = function() {
                    var url = "${createLink(controller:'frequency', action:'detail')}";
                    if ( $('table').bootstrapTable('getSelections').length <= 0 ) {
                        alert("No item selected!")
                    }else{                        
                        var keys = []   

                        for (index = 0; index < $('#filetable').bootstrapTable('getSelections').length ; index++) {
                            if ( $('#filetable').bootstrapTable('getSelections')[index].state == true ) {
                                keys.push($('#filetable').bootstrapTable('getSelections')[index].name.trim())
                            }                            
                        } 

                        window.location.href = url + "?keys=" + keys + "&corpusId=${corpusId}";
                        
                        // problem with ajax request is, that it does not redirect to the view, so the view does not get rendered: 
                        // $.ajax({
                        //         type: "POST",
                        //         dataType: "JSON",
                        //         url : url,
                        //         data : 'data=' + JSON.stringify(keys) ,
                        //         success: function(response){                                    
                        //             // console.log(response);
                        //             window.location.href = response.redirect;
                        //         }
                        //  })
                    }                    
                    
                }                
        </script>       

    </body>
</html>