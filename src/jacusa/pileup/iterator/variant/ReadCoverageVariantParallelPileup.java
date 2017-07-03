package jacusa.pileup.iterator.variant;

import jacusa.pileup.ParallelPileup;

public class ReadCoverageVariantParallelPileup implements Variant {
	
	@Override
	public boolean isValid(ParallelPileup parallelPileup) {
		return parallelPileup.getPooledPileup().getReadEndCount() > 0 && parallelPileup.getPooledPileup().getReadInnerCount() > 0; 
	}

}