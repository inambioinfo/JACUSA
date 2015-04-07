package jacusa.pileup.sample;

import jacusa.pileup.ParallelPileup;

public interface ParallelPileupSampling {

	/**
	 * 
	 * @param parallelPileup
	 * @return a permutated version of parallelPileup
	 */
	void sample(ParallelPileup parallelPileup);

}