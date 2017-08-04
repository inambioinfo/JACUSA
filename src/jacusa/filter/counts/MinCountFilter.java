package jacusa.filter.counts;

import jacusa.pileup.BaseConfig;
import jacusa.pileup.BaseCount;
import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasRefBase;

public class MinCountFilter<T extends Data<T> & hasBaseCount & hasRefBase> extends AbstractCountFilter<T> {

	private double minCount;
	
	public MinCountFilter(final char c, 
			final double minCount, 
			final BaseConfig baseConfig) {
		super(baseConfig);
		this.minCount = minCount;
	}

	@Override
	protected boolean filter(int variantBaseIndex, 
			ParallelData<T> parallelData, 
			BaseCount[] counts1, BaseCount[] counts2) {
		int count = parallelData
				.getCombinedPooledData()
				.getBaseCount()
				.getBaseCount(variantBaseIndex);
		if (count == 0) {
			return false;
		}

		ParallelData<T> filteredParallelData = applyFilter(variantBaseIndex, parallelData, counts1, counts2);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getBaseCount()
				.getBaseCount(variantBaseIndex);

		return count - filteredCount >= minCount;
	}

	public double getMinCount() {
		return minCount;
	}
	
}