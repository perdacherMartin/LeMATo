<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Welcome to Grails</title>
        <style type="text/css" media="screen">
            #status {
                background-color: #eee;
                border: .2em solid #fff;
                margin: 2em 2em 1em;
                padding: 1em;
                width: 12em;
                float: left;
                -moz-box-shadow: 0px 0px 1.25em #ccc;
                -webkit-box-shadow: 0px 0px 1.25em #ccc;
                box-shadow: 0px 0px 1.25em #ccc;
                -moz-border-radius: 0.6em;
                -webkit-border-radius: 0.6em;
                border-radius: 0.6em;
            }

            #status ul {
                font-size: 0.9em;
                list-style-type: none;
                margin-bottom: 0.6em;
                padding: 0;
            }

            #status li {
                line-height: 1.3;
            }

            #status h1 {
                text-transform: uppercase;
                font-size: 1.1em;
                margin: 0 0 0.3em;
            }

            #page-body {
                margin: 2em 1em 1.25em 18em;
            }

            h2 {
                margin-top: 1em;
                margin-bottom: 0.3em;
                font-size: 1em;
            }

            p {
                line-height: 1.5;
                margin: 0.25em 0;
            }

            #controller-list ul {
                list-style-position: inside;
            }

            #controller-list li {
                line-height: 1.3;
                list-style-position: inside;
                margin: 0.25em 0;
            }

            @media screen and (max-width: 480px) {
                #status {
                    display: none;
                }

                #page-body {
                    margin: 0 1em 1em;
                }

                #page-body h1 {
                    margin-top: 0;
                }
            }
        </style>
    </head>
    <body>
        
            <h1>Welcome to LeMATo</h1>
            <h3>LexicoMetric Analysis Tool</h3>
            <p>
                LeMATo, a lexicometric web application enables the analysis of lexical elements in a closed corpus. LeMATo analyses the corpus in a four stage process and each stage is based upon the promising ideas (published in [1]), for lexicometric analysis as a methodological approach for discourse analysis. First, the <em>frequency analysis</em> constructs a list with terms and their frequencies and opposes the frequency development in time for a subset of words. Second, the <em>concordance analysis</em> provides a <i>KWIC</i> analysis, which reveals the context of a queried term (i.e. the keyword). Third, the <em>analysis of characteristics in sub-corpora</em> groups the most frequent lexical elements into families based on annotations from a reference corpus. Fourth, the <em>co-occurrence analysis</em> uses the most frequent terms of the concordance analysis to measure their distance in document, section and sentence to extract the most significant words. At the end of each stage LeMATo shows a meaningful visualisation of the results.                
            </p>
            <br>
            <br>
            <p>
                <font size="1">
                [1] DZUDZEK, I., GLASZE, G., MATTISSEK, A., AND SCHIRMEL, H. Verfahren der lexikometrischen analyse von textkorpora. In Handbuch Diskurs und Raum. Theorien und Methoden für die Humangeographie sowie die sozial- und kulturwissenschaftliche Raum- forschung., vol. 1. Bielefeld: Transcript-Verlag., 2009, pp. 233–260.
                </font>
            </p>
    </body>
</html>
