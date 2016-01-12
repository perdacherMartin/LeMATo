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
          google.setOnLoadCallback(drawTermFrequencies);
          google.setOnLoadCallback(drawDocumentFrequencies);
          google.setOnLoadCallback(drawParagraphFrequencies);
          google.setOnLoadCallback(drawSentenceFrequencies);

          function drawTermFrequencies() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            data.addColumn('number', 'Term frequency');
            data.addRows([
            <% tfHist.keySet().eachWithIndex{ year, index -> %>
                ['${year}', <%= tfHist.get(year) %>],
            <%}%>
            ]);

            var options = {'title':'Term freqeuncy distribution over years',
                           'width':400,
                           'height':300};

            var chart = new google.visualization.ColumnChart(document.getElementById('termFrequencies'));
            chart.draw(data, options);
          }

          function drawDocumentFrequencies() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            data.addColumn('number', 'Document frequency');
            data.addRows([
            <% dfHist.keySet().eachWithIndex{ year, index -> %>
                ['${year}', <%= dfHist.get(year) %>],
            <%}%>
            ]);

            var options = {'title':'Document freqeuncy distribution over years',
                           'width':400,
                           'height':300};

            var chart = new google.visualization.ColumnChart(document.getElementById('docFrequencies'));
            chart.draw(data, options);
          }

          function drawParagraphFrequencies() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            data.addColumn('number', 'Paragraph frequency');
            data.addRows([
            <% pfHist.keySet().eachWithIndex{ year, index -> %>
                ['${year}', <%= pfHist.get(year) %>],
            <%}%>
            ]);

            var options = {'title':'Paragraph freqeuncy distribution over years',
                           'width':400,
                           'height':300};

            var chart = new google.visualization.ColumnChart(document.getElementById('parFrequencies'));
            chart.draw(data, options);
          }

          function drawSentenceFrequencies() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            data.addColumn('number', 'Sentence frequency');
            data.addRows([
            <% sfHist.keySet().eachWithIndex{ year, index -> %>
                ['${year}', <%= sfHist.get(year) %>],
            <%}%>
            ]);

            var options = {'title':'Sentence freqeuncy distribution over years',
                           'width':400,
                           'height':300};

            var chart = new google.visualization.ColumnChart(document.getElementById('senFrequencies'));
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
            <g:if test="${params.query}" >
                <div class="alert alert-success alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  <g:message code="default.filtering.byquery.label" args="[params.query]"/>
                </div>
            </g:if>               
            <div class="container">
                <div class="row">
                  <div class="col-xs-6">
                    <h3><g:message code="default.termfrequency.label"/></h3>
                    <div id="termFrequencies"></div>
                  </div>
                  <div class="col-xs-6">
                    <h3><g:message code="default.docfrequency.label"/></h3>
                    <div id="docFrequencies"></div>  
                  </div>
                </div>
                <div class="row">
                  <div class="col-xs-6">
                    <h3><g:message code="default.paragraphfrequency.label"/></h3>
                    <div id="parFrequencies"></div>
                  </div>
                  <div class="col-xs-6">
                    <h3><g:message code="default.sentencefrequency.label"/></h3>
                    <div id="senFrequencies"></div>  
                  </div>
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
                    <g:form action="detail">
                        <g:hiddenField id="corpusId" name="corpusId" value="${corpusId}" />
                        <g:hiddenField id="keys" name="keys" value="" />
                        <g:hiddenField id="query" name="query" value="${luceneQuery}" />
                        <button id="viewDetail" class="btn btn-primary" >
                            <i class="glyphicon glyphicon-check"></i>Show detail for selected
                        </button>
                    </g:form>
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
                                        <th data-field="rank" data-sortable="true" style="" >
                                            <div class="th-inner">Rank</div>
                                            <div class="fht-cell"></div>
                                        </th>                                    
                                        <th data-field="name" data-sortable="true" style="">
                                            <div class="th-inner">Keyword</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="docFreq" data-sortable="true" style="text-align:right">
                                            <div class="th-inner">Document Freqeuncy</div>
                                            <div class="fht-cell"></div>
                                        </th>   
                                        <th data-field="termFreq" data-sortable="true" style="text-align:right">
                                            <div class="th-inner">Term Freqeuncy</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="relFreq" data-sortable="true" style="text-align:right">
                                            <div class="th-inner">Relative freqeuncy <br> (rel. to complete corpus)</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="kendall" data-sortable="true" style="text-align:right">
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
                                        <td style="" align="right"> <%= frequency.termFrequency / totalFrequencies %> </td>
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
                    pageList: [10, 25, 50, 100, 'All'],
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

                        // window.location.href = url + "?keys=" + keys + "&corpusId=${corpusId}&query=${luceneQuery}";
                        
                        $('#keys').val(keys)
                        
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