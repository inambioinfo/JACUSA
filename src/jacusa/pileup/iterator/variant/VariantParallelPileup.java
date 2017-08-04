package jacusa.pileup.iterator.variant;

import jacusa.pileup.BasePileup;
import jacusa.pileup.ParallelData;

public class VariantParallelPileup implements Variant<BasePileup> {
	
	@Override
	public boolean isValid(ParallelData<BasePileup> parallelData) {
		return parallelData
				.getCombinedPooledData()
				.getBaseCount()
				.getAlleles().length > 1;
	}

}