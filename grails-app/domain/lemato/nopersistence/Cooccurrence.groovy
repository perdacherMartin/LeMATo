package lemato.nopersistence

class Cooccurrence {

	Long nA // individual frequency $n_A$
	Long nB // individual frequency $n_B$
	Long nAB // co-occurrence $n_AB$
	Long n // corpus size

	public Cooccurrence(Long nA, Long nB, Long nAB, Long n){
		this.nA = nA
		this.nB = nB
		this.nAB = nAB
		this.n = n
	}

	static enum Measure {
		DiceCoefficient
	}

	static mapWith = "none"

	private Double calculateDiceCoefficient() {
		return (2 * nAB) / (nA + nB)
	}

	Double getCooccurrence(Measure calc){
		switch ( calc ){
			case Measure.DiceCoefficient:
				return calculateDiceCoefficient()
				break;
			default: break;
		}		
	}

    static constraints = {
    	nA min: 0
    	nB min: 0
    	nAB min: 0
    }
}
