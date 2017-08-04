package jacusa.filter;

import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.RatioCountFilter;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.BaseCount;
import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Location;

public class DistanceStorageFilter<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractWindowStorageFilter<T> {

	private AbstractCountFilter<T> countFilter;

	public DistanceStorageFilter(final char c, final double minRatio, final int minCount, final BaseConfig baseConfig) {
		super(c);

		countFilter = new RatioCountFilter<T>(minRatio, baseConfig);
	}

	@Override
	protected boolean filter(final Result<T> result, final Location location, final WindowIterator<T> windowIterator) {
		final ParallelData<T> parallelData = result.getParellelData();

		// FIXME make n
		BaseCount[] baseCounts1 = getCounts(location, windowIterator.getFilterContainers(0, location));
		BaseCount[] baseCounts2 = getCounts(location, windowIterator.getFilterContainers(1, location));

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}
		
		return countFilter.filter(variantBaseIndexs, parallelData, baseCounts1, baseCounts2);
	}

}