package lemato

import lemato.es.*

class ElasticObjectsTagLib {
    
    ElasticsearchService elasticsearchService 

    static defaultEncodeAs = [taglib:'text']
    
    /**
     * Renders the childname for the current object
     *
     * @attr esObject REQUIRED is the current elasticsearch object.
     */

    def getChildrenName = { attrs, body ->
    	
    	final ElasticObject esObject = attrs.get('esObject')

    	if ( esObject instanceof ParagraphRdb ) {
    		out << g.message(code: "default.sentence.plural")
    	}

    	if ( esObject instanceof DocumentRdb ){
    		out << g.message(code: "default.paragraph.plural")
    	}
    }


    /**
     * Renders <li> children </li> for the view textUnit in the ViewController
     *
     * @attr esObject REQUIRED is the current elasticsearch object.
     */
    def getChildrenLi = { attrs, body ->

        final ElasticObject esObject = attrs.get('esObject')
    	def childrens = []

        if (esObject instanceof ParagraphRdb ){
            childrens = esObject.sentences
        }

        if ( esObject instanceof DocumentRdb ){
            childrens = esObject.paragraphs
        }

        childrens = childrens.sort { it.id }

        childrens.each{ child ->
            
            child = elasticsearchService.load(child, [])

            String childTitle = g.getTitle( textBody: child.textBody )

            out << "<li>"
            if ( params.highlight ){
                out << g.link(action:"text", controller: "view", params: [ corpusId: params.corpusId, id: child.getId(), highlight: params.highlight] , childTitle  )                 
            }else{
                out << g.link(action:"text", controller: "view", params: [ corpusId: params.corpusId, id: child.getId()] , childTitle )
            }
            out << "</li>"
        }
    }


    /**
     * Renders a title for a given elasticsearch object. The title contains the first n characters of the textBody 
     *
     * @attr textBody REQUIRED is the elasticsearch object.
     * @attr n value the count of characters, default 10
     */
    String getTitle = { attrs, body -> 
        int lowerbound = 0 
        String esTitle = ""
        int n = 10 

        if ( attrs.n )
            n = Integer.parseInt(attrs.get('n'))

        final String textBody = attrs.get('textBody').trim()

        if ( textBody != null ) 
            lowerbound = Math.min( n, textBody.size() - 1 )        

        if ( lowerbound != 0 ){
            out << textBody[0..lowerbound]
            // return textBody[0..lowerbound]
        }else{
            out << "[empty]"
        }
    }

    /**
     * Renders the link to the parent object link for the textUnit view in the ViewController.
     *
     * @attr esObject REQUIRED is the current elasticsearch object.
     */

    def getParrentLink = { attrs, body ->	
    	final ElasticObject esObject = attrs.get('esObject')
    	String parentId = ""
    	String parentName = ""

    	if ( esObject instanceof SentenceRdb ) {  
    		parentId = esObject.paragraph.getId()
    		parentName = g.message(code: "default.paragraph.label")
    	}

    	if ( esObject instanceof ParagraphRdb ) {   
    		parentId = esObject.document.getId()
    		parentName = g.message(code: "default.document.label")
    	}

    	if ( esObject instanceof DocumentRdb ){
    		parentName = g.message(code: "lfile.label")
    		out << g.link(controller:"lfile", action: "show", id: esObject.lfile.getId(), parentName )
    	}else{
		    if ( params.highlight ){
				out << g.link(action:"text", controller: "view", params: [ corpusId: params.corpusId, id: parentId, highlight: params.highlight] , parentName)					
		    }else{
				out << g.link(action:"text", controller: "view", params: [ corpusId: params.corpusId, id: parentId] , parentName)
		    }
    	}
    	
    }

    /**
     * Renders the text output for esObject.textBody
     *
     * @attr textBody REQUIRED is the textBody to render.
     */

    def formatTextOutput = { attrs, body ->
    	final String text = attrs.get('textBody')

		out << text.replaceAll("\n\n", "<br><br>")
	}

    /**
     * Formats the output of the kwic fragment in "/concordance/kwic"
     *
     * @attr fragment 
     * @attr part is one of the following: 
     * 
     * [ "before",
     *   "center",
     *   "after"  ]
     */

    def getFragment = { attrs, body ->
        final String fragment = attrs.get('fragment')
        final String part = attrs.get('part')

        if ( part == "before" )
            out << fragment.find("(.*?)(?=<mark>)")


        if ( part == "center" )
            out << fragment.find("(?<=<mark>)([^\\s]*)(?=</mark>)")

        if ( part == "after" )
            out << fragment.find("(?<=</mark>)(.*)")
    }

    /**
     * Formats the date output
     *
     * @attr format the date format. Example: dd-MM-yyyy
     * @attrs date
     */

    def dateFormat = { attrs, body ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.date)
    }    


    def linkEsObject = { attrs, body ->
        final ElasticObject esObject = attrs.get('esObject')
        final highlight = g.getFragment(fragment:attrs.get('fragment'), part: "center")
        
        out << "<a href=\"/view/text/${esObject.id}?highlight=${highlight}\">"
        out << body()
        out << "</a>"
    }
}
