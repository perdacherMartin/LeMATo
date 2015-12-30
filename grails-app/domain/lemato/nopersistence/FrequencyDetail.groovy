package lemato.nopersistence

import org.apache.commons.math3.stat.correlation.KendallsCorrelation 

class FrequencyDetail {
	
	static mapWith = "none"

	static belongsTo = [result: FrequencyResult]
	HashMap<Integer, Long> termFrequencies
	HashMap<Integer, Long> docFrequencies
	HashMap<Integer, Long> paragraphFrequencies
	HashMap<Integer, Long> sentenceFrequencies

	Double getKendallTau() {

		if ( termFrequencies.size() > 0 ){
			double[] frequencies = termFrequencies.keySet().collect{ it.doubleValue() }.toArray()
			double[] years = termFrequencies.values().collect{ it.doubleValue() }.toArray()

	        return new KendallsCorrelation().correlation(
	                    years,
	                    frequencies                    
	            )
		}else{
			return 0.0
		}
	}

}