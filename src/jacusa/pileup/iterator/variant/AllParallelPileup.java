package jacusa.pileup.iterator.variant;

import jacusa.pileup.BasePileup;
import jacusa.pileup.ParallelData;

public class AllParallelPileup implements Variant<BasePileup> {

	@Override
	public boolean isValid(ParallelData<BasePileup> parallelData) {
		return true;
	}

}