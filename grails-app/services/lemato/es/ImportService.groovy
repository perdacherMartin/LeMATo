package lemato.es

import grails.transaction.Transactional

import java.util.Date
import java.util.Locale
import java.text.*
import groovy.time.*

import lemato.Lfile

@Transactional
class ImportService {

    @Transactional
    private def saveSentences(ParagraphRdb par){
        BreakIterator bi = BreakIterator.getSentenceInstance();
        bi.setText(par.textBody);
        int index = 0;
        while (bi.next() != BreakIterator.DONE) {
            String sentence = par.textBody.substring(index, bi.current())
            SentenceRdb s = new SentenceRdb(
                textBody: sentence, 
                paragraph: par, 
                tags: par.tags, 
                publishDate: par.publishDate,                
            ).save(failOnError: true) 
            par.addToSentences(s)
            s.save(failOnError:true)
            index = bi.current();
        }
    }

    @Transactional
	def importFileToElasticsearch(Lfile lfile, File file, String tags){    
		Integer docCount = 0
		String paragraphStr = ""
        Boolean inRegularText = false
        DocumentRdb doc = new DocumentRdb(                                    
                                    textBody: "", 
                                    tags: tags, 
                                    lfile: lfile, 
                                    publishDate: new Date() 
                                )
        List<ParagraphRdb> pars = []
        
        def timeStart = new Date()
    	
        new FileInputStream(file).eachLine{ line ->                
    		
    		switch ( line ){ // iterating over paragraphs instead of lines
    			// pattern of a line between paragraphs
    			case "" :
    				if ( paragraphStr != "" ){ // react to each starting pattern of the paragraph
                        inRegularText = false
    					switch ( paragraphStr.trim() ){
    						// start of the document
    						case ~/^Dokument\s*\d*\s*von\s*\d*/:
                                // println "new document (# " + docCount++ 
                                print "\b" * 40 + "${paragraphStr.trim()}"
                                paragraphStr = ""
    							doc = new DocumentRdb(                                    
                                    textBody: "", 
                                    tags: tags, 
                                    lfile: lfile, 
                                    publishDate: new Date() 
                                )
    							break;
    						// end of the document
    						case ~/^Copyright\s*\d*.*/:
                                lfile.addToDocuments(doc)                                
    							doc.save(failOnError:true)
                                pars.each{ par ->
                                    par.publishDate = doc.publishDate
                                    doc.addToParagraphs(par)
                                    par.save(failOnError:true)
                                    saveSentences(par)
                                    par.save(failOnError:true)
                                }                
                                doc.save(failOnError:true)                
                                pars = []
                                // importBulk.executeBulk()
    							break;

    						// filtering the following elements:
                            case ~/^HIGHLIGHT:.*/ : 
                            case ~/^UPDATE:.*/ : 
                            case ~/^SPRACHE:.*/ : 
                            case ~/^GRAPHICS:.*/ : 
                            case ~/^LANGUAGE:.*/ : 
                            case ~/^LOAD-DATE:.*/ : 
                            case ~/^PUB-TYPE:.*/ : 
                            case ~/^BYLINE:.*/ : 
                            case ~/^PUBLICATION-TYPE:.*/ : 
                            case ~/^GRAFIK:.*/ : 
                            case ~/^AUTOR:.*/ : 
                            case ~/^BIBLIOGRAPHIE:.*/ : 
                            case ~/^GRAPHIK:.*/ : 
                            case ~/^LÄNGE:.*/ :
                            case ~/^LENGTH:.*/ :
                            case ~/^RUBRIK:.*/ : 
                            case ~/^SECTION:.*/ : 
                            case ~/^ZEITUNGS-CODE:.*/ :   
								break;
                            
                            case ~/^\d{1,2}\W*(Januar|Jaenner|Jänner|Februar|Feber|März|Maerz|April|May|Mai|Juni|Juli|August|September|Oktober|November|Dezember)\W*\d{4}/:
                                try{
                                    paragraphStr = paragraphStr.trim().replaceAll("ae", "ä")
                                    paragraphStr = paragraphStr.replaceAll("Jänner", "Januar")
                                    paragraphStr = paragraphStr.replaceAll("May", "Mai")
                                    doc.publishDate = new SimpleDateFormat("dd. MMMM yyyy", new Locale("DE")).parse(paragraphStr)
                                }catch(ParseException e){
                                    // occurs in text, is no date format specifier for the document                                    
                                    inRegularText = true
                                }

                                if ( !inRegularText )
                                    break;

							default:
                                // if none of the patterns above applies, then it is a regular paragraph
                                
                                if ( paragraphStr ){
                                    if ( doc.textBody ){
                                        doc.textBody =  "" + doc.textBody + "\n\n" + paragraphStr
                                    }else{
                                        doc.textBody = paragraphStr
                                    }

                                    pars << new ParagraphRdb(
                                        document: doc,                                    
                                        tags: tags,
                                        publishDate: doc.publishDate,
                                        textBody: paragraphStr)
                                }
								break;
    					}

    					paragraphStr = ""
                        // p = new Paragraph(textBody: "")
                        // p.textBody = ""
    				}
    				break

                // pattern of a regular line within a paragraph
                case ~/^.?.*/ : 
                    if ( line ){
                        paragraphStr = paragraphStr + " " + line
                    }                    
                    break                  
    		}

    	}

        
		lfile.addToDocuments(doc)
		doc.save(failOnError:true)
        pars.each{ par ->            
            par.publishDate = doc.publishDate
            doc.addToParagraphs(par)
            par.save(failOnError:true)
            saveSentences(par)
            par.save(failOnError:true)
        }
        doc.save(failOnError:true)
        pars = []

        println ""
        println "Elapased time for this file:" + TimeCategory.minus(new Date(), timeStart) + " ; " + file.length()
	}	
}
