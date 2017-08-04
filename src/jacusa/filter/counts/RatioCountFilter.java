package jacusa.filter.counts;

import jacusa.pileup.BaseConfig;
import jacusa.pileup.BaseCount;
import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasRefBase;

public class RatioCountFilter<T extends Data<T> & hasBaseCount & hasRefBase> extends AbstractCountFilter<T> {

	private double minRatio;

	public RatioCountFilter(final double minRatio, final BaseConfig baseConfig) {
		super(baseConfig);
		this.minRatio = minRatio;
	}

	@Override
	protected boolean filter(int variantBaseIndex, ParallelData<T> parallelData, 
			BaseCount[] baseCounts1, BaseCount[] baseCounts2) {
		int count = parallelData
				.getCombinedPooledData()
				.getBaseCount()
				.getBaseCount(variantBaseIndex);
		ParallelData<T> filteredParallelData = applyFilter(variantBaseIndex, 
				parallelData, baseCounts1, baseCounts2);
		int filteredCount = filteredParallelData
				.getCombinedPooledData()
				.getBaseCount()
				.getBaseCount(variantBaseIndex);

		return (double)filteredCount / (double)count <= minRatio;
	}
	
	public double getMinRatio() {
		return minRatio;
	}

}