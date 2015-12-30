<%@ page import="java.net.URLEncoder" %>

<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />        
        <title><g:message code="default.frequency.label" /></title>
        <r:require modules="jquery-ui, blueprint"/>
        <r:require module="single-module"/>       
        <asset:javascript src="/webjars/DataTables/1.10.8/media/js/jquery.dataTables.min.js" />
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>

        <g:javascript>    
          // Load the Visualization API and the piechart package.
          google.load('visualization', '1.0', {'packages':['corechart']});

          // Set a callback to run when the Google Visualization API is loaded.
          google.setOnLoadCallback(drawTermFrequencyChart);
          google.setOnLoadCallback(drawDocumentFrequencyChart);
          google.setOnLoadCallback(drawParagraphFrequencyChart);
          google.setOnLoadCallback(drawSentenceFrequencyChart);
          
          var keywords = new Array()

          function drawTermFrequencyChart() {
            // Create the data table.
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            var title = "Term freqeuncy distribution for "
            
            <% HashMap<Integer, Long> years = [:]
            details.each{ detail ->
              years = years << detail.termFrequencies %>
              keywords.push( "${detail.result.keyword}" )
              data.addColumn('number', '${detail.result.keyword}');
              title = title.concat(" '${detail.result.keyword}' ")
            <% } %>
                        
            <% years.keySet().eachWithIndex{ year, index -> %>                
                var row = ['${year}']
                <% details.each{ detail -> %> 
                  <% if ( detail.termFrequencies.get(year) ) { %> 
                    row.push(<%= detail.termFrequencies.get(year) %>)
                  <% }else{ %>
                    row.push(0)
                  <% } %>
                <% } %>
                data.addRows([row]);
            <%}%>            

            var options = {'title':title,
                           'width':400,
                           'height':300};

            // Instantiate and draw our chart, passing in some options.
            var chart = new google.visualization.ColumnChart(document.getElementById('termfrequency_chart'));

            chart.draw(data, options);
          }

          function drawDocumentFrequencyChart() {
            // Create the data table.
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            var title = "Document freqeuncy distribution for "
            <% details.each{ detail -> %>
              data.addColumn('number', '${detail.result.keyword}');
              title = title.concat(" '${detail.result.keyword}' ")
            <% } %>
                        
            <% years.keySet().eachWithIndex{ year, index -> %>                
                var row = ['${year}']
                <% details.each{ detail -> %> 
                  <% if ( detail.docFrequencies.get(year) ) { %> 
                    row.push(<%= detail.docFrequencies.get(year) %>)
                  <% }else{ %>
                    row.push(0)
                  <% } %>
                <% } %>
                data.addRows([row]);
            <%}%>            

            var options = {'title':title,
                           'width':400,
                           'height':300};

            // Instantiate and draw our chart, passing in some options.
            var chartDoc = new google.visualization.ColumnChart(document.getElementById('documentfrequency_chart'));
            
            function selectHandlerDocFreq() {
              var selectedItem = chartDoc.getSelection()[0];
              if (selectedItem) {
                var value = data.getValue(selectedItem.row, selectedItem.column);
                var selectedYear = data.getValue(selectedItem.row, 0)  
                window.location.href = "/frequency/filterDocuments?year=".concat(
                  data.getValue(selectedItem.row, 0), "&",
                  "corpusId=", <%= params.corpusId %>, "&",
                  "key=", keywords[selectedItem.column - 1]);
              }
            }

            google.visualization.events.addListener(chartDoc, 'select', selectHandlerDocFreq); 

            chartDoc.draw(data, options);
          }          

          function drawParagraphFrequencyChart() {
            // Create the data table.
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            var title = "Paragraph freqeuncy distribution for "
            <% details.each{ detail -> %>
              data.addColumn('number', '${detail.result.keyword}');
              title = title.concat(" '${detail.result.keyword}' ")
            <% } %>
                        
            <% years.keySet().eachWithIndex{ year, index -> %>                
                var row = ['${year}']
                <% details.each{ detail -> %> 
                  <% if ( detail.paragraphFrequencies.get(year) ) { %> 
                    row.push(<%= detail.paragraphFrequencies.get(year) %>)
                  <% }else{ %>
                    row.push(0)
                  <% } %>
                <% } %>
                data.addRows([row]);
            <%}%>            

            var options = {'title':title,
                           'width':400,
                           'height':300};

            // Instantiate and draw our chart, passing in some options.
            var chartPar = new google.visualization.ColumnChart(document.getElementById('paragraphfrequency_chart'));

            function selectHandlerParFreq() {
              var selectedItem = chartPar.getSelection()[0];
              if (selectedItem) {
                var value = data.getValue(selectedItem.row, selectedItem.column);
                var selectedYear = data.getValue(selectedItem.row, 0)  
                window.location.href = "/frequency/filterParagraphs?year=".concat(
                  data.getValue(selectedItem.row, 0), "&",
                  "corpusId=", <%= params.corpusId %>, "&",
                  "key=", keywords[selectedItem.column - 1])                
              }
            }

            google.visualization.events.addListener(chartPar, 'select', selectHandlerParFreq);
            chartPar.draw(data, options);
          }

          function drawSentenceFrequencyChart() {
            // Create the data table.
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Years');
            var title = "Sentence freqeuncy distribution for "
            <% details.each{ detail -> %>
              data.addColumn('number', '${detail.result.keyword}');
              title = title.concat(" '${detail.result.keyword}' ")
            <% } %>
                        
            <% years.keySet().eachWithIndex{ year, index -> %>                
                var row = ['${year}']
                <% details.each{ detail -> %> 
                  <% if ( detail.sentenceFrequencies.get(year) ) { %> 
                    row.push(<%= detail.sentenceFrequencies.get(year) %>)
                  <% }else{ %>
                    row.push(0)
                  <% } %>
                <% } %>
                data.addRows([row]);
            <%}%>            

            var options = {'title':title,
                           'width':400,
                           'height':300};

            // Instantiate and draw our chart, passing in some options.
            var chartSen = new google.visualization.ColumnChart(document.getElementById('sentencefrequency_chart'));

            function selectHandlerSentenceFreq() {
              var selectedItem = chartSen.getSelection()[0];
              if (selectedItem) {
                var value = data.getValue(selectedItem.row, selectedItem.column);
                var selectedYear = data.getValue(selectedItem.row, 0)  
                window.location.href = "/frequency/filterSentences?year=".concat(
                  data.getValue(selectedItem.row, 0), "&",
                  "corpusId=", <%= params.corpusId %>, "&",
                  "key=", keywords[selectedItem.column - 1])                
              }
            }

            google.visualization.events.addListener(chartSen, 'select', selectHandlerSentenceFreq);            
            chartSen.draw(data, options);
          }                     
        </g:javascript>

    </head>
    <body>
      <div class="container">
        <ul class="nav nav-tabs nav-justified">
          <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
          <li><g:link class="index" action="index"><g:message code="default.frequency.label" /></g:link></li>
          <li class="active"> <a href="${request.getRequestURL}"> <g:message code="default.frequency.details.label"/></a></li>
        </ul>
        </div>    
      <div class="container"> 
      <div class="row">
        <div class="col-md-6">
          <div id="termfrequency_chart">
          </div>
        </div>
        <div class="col-md-6">
          <div id="documentfrequency_chart">
          </div>
        </div>
      </div>
      <div class="container">
        <div class="col-md-6">
          <div id="paragraphfrequency_chart">
          </div>
        </div>
        <div class="col-md-6">
          <div id="sentencefrequency_chart">
          </div>        
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
                  data-show-footer="true" 
                  data-pagination="true" 
                  data-search="true"
                  data-click-to-select="true" 
                  data-select-item-name="selectItemName" >
                  <thead>
                    <tr>
                      <th data-field="state" style="" >
                        <div class="th-inner">Keyword</div>
                        <div class="fht-cell"></div>
                      </th>
                      <th data-field="state" style="" >
                        <div class="th-inner">Term frequency</div>
                        <div class="fht-cell"></div>
                      </th>
                      <th data-field="state" style="" >
                        <div class="th-inner">Kendall's &Tau; <br> (on term frequncies over years)</div>
                        <div class="fht-cell"></div>
                      </th>                      
                      <th data-field="state" style="" >
                        <div class="th-inner">Document frequency</div>
                        <div class="fht-cell"></div>
                      </th>
                      <th data-field="state" style="" >
                        <div class="th-inner">Paragraph frequency</div>
                        <div class="fht-cell"></div>
                      </th>
                      <th data-field="state" style="" >
                        <div class="th-inner">Sentence frequency</div>
                        <div class="fht-cell"></div>
                      </th>
                      </thead>                            
                      <tbody> 
                          <% details.eachWithIndex{ detail, i -> %> 
                          <tr>                                        
                              <td style="" > <%= detail.result.keyword %> </td>
                              <td style="" > <%= detail.termFrequencies.values().sum().intValue() %> </td>
                              <td style="" > <%= detail.getKendallTau().round(2) %> </td>
                              <td style="" > <%= detail.docFrequencies.values().sum() %> </td>
                              <td style="" > <%= detail.paragraphFrequencies.values().sum() %> </td>
                              <td style="" > <%= detail.sentenceFrequencies.values().sum() %> </td>
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
    </body>
</html>