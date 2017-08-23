package jacusa.filter.counts;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualCount;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;

public class CombinedCountFilter<T extends BaseQualData> 
extends AbstractCountFilter<T> {

	private RatioCountFilter<T> minRatioFilter;
	private MinCountFilter<T> minCountFilter;
	
	public CombinedCountFilter(
			final double minRatio, final double minCount,
			final AbstractParameters<T> parameters) {
		super(parameters);
		
		minRatioFilter = new RatioCountFilter<T>(minRatio, parameters);
		minCountFilter = new MinCountFilter<T>(minRatio, parameters);
	}

	@Override
	protected boolean filter(final int variantBaseIndex, 
			final ParallelPileupData<T> parallelData, 
			final BaseQualCount[][] counts) {
		return minCountFilter.filter(variantBaseIndex, parallelData, counts) &&
				minRatioFilter.filter(variantBaseIndex, parallelData, counts);
	}

	
}