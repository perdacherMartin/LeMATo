<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'corpus.label', default: 'Corpus')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="container">
            <ul class="nav nav-tabs nav-justified">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li class="active"><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-corpus" class="content scaffold-list" role="main">
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
            <div class="container-fluid">
                <div class="bootstrap-table">
                    <div class="fixed-table-container" style="padding-bottom: 0px;">
                        <div class="fixed-table-body">
                            <div class="fixed-table-loading" style="top: 42 px; ">Loading please wait...</div>
                            <table id="corpustable" class="table table-hover" data-show-footer="true">
                                <thead>
                                    <tr>
                                        <th data-field="0" style="">
                                            <div class="th-inner">Name</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="1" style="">
                                            <div class="th-inner">Description</div>
                                            <div class="fht-cell"></div>
                                        </th>
                                        <th data-field="2" style="">
                                            <div class="th-inner">File count</div>
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
                                <% corpusList.each { corpus-> %>
                                    <%='<tr data-index="${++counter}">'%>
                                        <td style=""><g:link action="show" id="${corpus.id}"> <%= corpus.name %> </g:link></td>
                                        <td style=""><%= corpus.description %> </td>
                                        <% Long wordCount = 0 %>
                                        <% Long sentenceCount = 0 %>
                                        <% Long paragraphCount = 0 %>
                                        <% Long docCount = 0 %>
                                        <% Long fileCount = 0 %>
                                        <% corpus.lfiles.each { lfile -> 
                                            fileCount = fileCount + 1
                                            wordCount = wordCount + lfile.stats.wordCount
                                            sentenceCount = sentenceCount + lfile.stats.sentenceCount
                                            paragraphCount = paragraphCount + lfile.stats.paragraphCount
                                            docCount = docCount + lfile.stats.docCount 
                                        }
                                        %>        
                                        <td style="" align="right"><%= fileCount %></td>
                                        <td style="" align="right"><%= wordCount %></td>
                                        <td style="" align="right"><%= sentenceCount %></td>
                                        <td style="" align="right"><%= paragraphCount %></td>
                                        <td style="" align="right"><%= docCount %></td>
                                    </tr>
                                <%}%>
                                </tbody>
                                <tfoot>

                                </tfoot>
                            </table>
                        </div>                   
                    </div>
                </div>
            </div>
        </div>

    </body>
</html>