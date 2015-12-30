<!doctype html>
<html lang="en" class="no-js">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title><g:layoutTitle default="LeMATo"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <asset:stylesheet src="application.css"/>
        <asset:javascript src="application.js"/>
        <g:layoutHead/>
    </head>
    <body>
        <div id="wrapper">

            <!-- Sidebar -->
            <div id="sidebar-wrapper">
                <ul class="sidebar-nav">
                    <li>
                        <a href="#menu-toggle" class="glyphicon glyphicon-menu-hamburger" id="in-menu-toggle"></a>
                    </li>                
                    <li class="sidebar-brand">
                        LeMATo
                    </li>
                    <li>
                        <g:link controller="corpus" action="index"> 
                            <g:message code="menu.corpus.label"/> 
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="lfile" action="index"> 
                            <g:message code="menu.lfile.label"/> 
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="frequency" action="index"> 
                            <g:message code="menu.frequency.label"/> 
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="concordance" action="index"> 
                            <g:message code="menu.concordance.label"/> 
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="characteristics" action="index"> 
                            <g:message code="menu.characteristics.label"/> 
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="cooccurrence" action="index"> 
                            <g:message code="menu.cooccurrence.label"/> 
                        </g:link>
                    </li>
                </ul>
            </div>
            <!-- /#sidebar-wrapper -->

            <!-- Page Content -->
            <div id="page-content-wrapper">
                <div class="container">
                    <div class="row">
                        <div class="col-lg-1">
                            <a href="#menu-toggle" class="glyphicon glyphicon-menu-hamburger" id="menu-toggle"></a>
                        </div>
                        <div class="col-md-10">
                            
                                <a href="/"><asset:image src="LeMATo_Logo.jpg" alt="LeMATo"/></a>
                            
                        </div>
                    </div>
                </div>
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-lg-12">
                            <g:layoutBody/>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /#page-content-wrapper -->

        </div>    

        <script>
        $("#menu-toggle").click(function(e) {
            e.preventDefault();
            $("#wrapper").toggleClass("toggled");
        });
        $("#in-menu-toggle").click(function(e) {
            e.preventDefault();
            $("#wrapper").toggleClass("toggled");
        });        
        </script>        
    </body>
</html>
