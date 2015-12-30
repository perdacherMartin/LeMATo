package lemato.nopersistence

class FrequencyResult {
	
	static mapWith = "none"

	String keyword
	Integer docCount
	Integer termFrequency
	Double kendallTauOnDocCount // kendall $\tau$ on document count over years
}