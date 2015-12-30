package lemato.es

public enum TextUnit{
	document(DocumentRdb.typeName), paragraph(ParagraphRdb.typeName), sentence(SentenceRdb.typeName)

	final String value

	TextUnit(String value){
		this.value = value
	}

	String toString() { value } 
    String getKey() { name() }

}