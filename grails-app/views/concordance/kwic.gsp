<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.concordance.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>       
        <script type="text/javascript" src="https://www.google.com/jsapi"></script> 
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" /> 

        <script type="text/javascript">

      google.load("visualization", "1.1", {packages:["wordtree"]});
      google.setOnLoadCallback(drawSimpleNodeChart);
      function drawSimpleNodeChart() {
        var text = "";

        <% results.eachWithIndex{ result, i -> %> 
            text = text.concat('${result.fragment.decodeHTML().replaceAll("(<mark>([^\\s]*)</mark>)|(\n)", "$result.stemmedCenter")}', ". ");
        <% } %>
        
        text = text.replace(/&quot;/g, '\"')

        var data = google.visualization.arrayToDataTable(            
          [ 
            ['Phrases'],
            [ text ]
          ]
        );

        <% if ( ! results.isEmpty() ){ %>

            var options = {
              wordtree: {
                format: 'implicit',
                type: 'double',
                word: $('#keyword').val()
              }
            };

            var wordtree = new google.visualization.WordTree(document.getElementById('wordtree_double'));
            wordtree.draw(data, options);
        <% } %>
      }

        </script>
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li class="active"><g:link class="index" action="index"><g:message code="default.frequency.label" /></g:link></li>                
            </ul>
        </div>
        <div id="list-corpus" class="content scaffold-list" role="main">
            <h1><g:message code="default.concordance.label" /></h1>
            <g:if test="${params.query}" >
                <div class="alert alert-success alert-dismissible" role="alert">
                  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                  <g:message code="default.filtering.byquery.label" args="[params.query]"/>
                </div>                 
            </g:if>             
            <div class="container">
                <div class="input-group">
                  <span class="input-group-addon" id="basic-addon1">center word:</span>
                  <input id="keyword" type="text" class="form-control" placeholder="centered keyword" aria-describedby="basic-addon1" value="${params.keyword}">
                </div>
                <div id="wordtree_double" style="width: 900px; height: 500px;"></div>
            </div>
            <div class="container"> 
                <div id="chart_div">
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
                            <table id="filetable" 
                                class="table table-hover" 
                                data-pagination="true" 
                                data-search="true">
                                <thead>
                                    <tr>
                                        <th data-field="0" style="" >
                                            <div class="th-inner">Date</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="1">
                                            <div class="th-inner">Tag</div>
                                            <div class="fht-cell"></div>                                       
                                        </th>                                        
                                        <th data-field="2"  >
                                            <div class="th-inner"></div>
                                            <div class="fht-cell"></div>
                                        </th>                                    
                                        <th data-field="3" class="text-center">
                                            <div class="th-inner">Fragment</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="4" >
                                            <div class="th-inner"></div>
                                            <div class="fht-cell"></div>
                                        </th>   
                                    </tr>
                                </thead>                            
                                <tbody> 
                                    <% results.eachWithIndex{ result, i -> %> 
                                    <tr>                                        
                                        <td>
                                            <g:dateFormat date="${result.textUnit.publishDate}" format="dd. MMMM YYYY"/>		                                	
                                        </td>
                                        <td style=""> <input id="tags" type="text" value="${result.textUnit.tags}" name="tags" class="form-control" data-role="tagsinput" readonly disabled ></td>
                                        <td class="text-right"><g:getFragment fragment="${result.fragment}" 
                                            part="before"></g:getFragment> </td>
                                        <td class="text-center" > 
                                            <g:linkEsObject esObject="${result.textUnit}" fragment="${result.fragment}">  
                                                <g:getFragment fragment="${result.fragment}" 
                                                    part="center">
                                                </g:getFragment> 
                                            </g:linkEsObject>
                                        </td>
                                        <td class="text-left"> <g:getFragment fragment="${result.fragment}" 
                                            part="after"></g:getFragment> </td>
                                        
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
                    }).on('page-change.bs.table', function (e, size, number) {
                        jQuery("[id=tags]").tagsinput({});
                })                

                $('#keyword').keypress(function (e) {
                    if (e.which == 13) {
                        drawSimpleNodeChart()
                    }
                });

                $('#filetable').bootstrapTable({                    
                });

                $('#tags').tagsinput({});

        </script>       

    </body>
</html>