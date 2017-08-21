package jacusa.filter;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualCount;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.RatioCountFilter;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;

public class DistanceStorageFilter<T extends BaseQualData> 
extends AbstractWindowStorageFilter<T> {

	private AbstractCountFilter<T> countFilter;

	public DistanceStorageFilter(final char c, final double minRatio, 
			final int minCount, final AbstractParameters<T> parameters) {
		super(c);

		countFilter = new RatioCountFilter<T>(minRatio, parameters);
	}

	@Override
	protected boolean filter(final Result<T> result, final WindowIterator<T> windowIterator) {
		final ParallelPileupData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		final Coordinate coordinate = parallelData.getCoordinate();
		final BaseQualCount[][] baseCounts = new BaseQualCount[parallelData.getConditions()][];
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			baseCounts[conditionIndex] = getBaseQualData(coordinate, windowIterator.getFilterContainers(conditionIndex, coordinate));
		}
		
		return countFilter.filter(variantBaseIndexs, parallelData, baseCounts);
	}

}
