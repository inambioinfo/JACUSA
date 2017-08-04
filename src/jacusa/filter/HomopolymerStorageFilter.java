package jacusa.filter;

import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.MinCountFilter;
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

public class HomopolymerStorageFilter<T extends Data<T> & hasCoordinate & hasBaseCount & hasRefBase> extends AbstractWindowStorageFilter<T> {

	private AbstractCountFilter<T> countFilter;
	
	public HomopolymerStorageFilter(final char c, BaseConfig baseConfig) {
		super(c);

		countFilter = new MinCountFilter<T>(c, 1, baseConfig);
	}

	@Override
	protected boolean filter(final Result<T> result, final Location location, final WindowIterator<T> windowIterator) {
		final ParallelData<T> parallelData = result.getParellelData();
		
		// FIXME
		BaseCount[] counts1 = getCounts(location, windowIterator.getFilterContainers(0, location));
		BaseCount[] counts2 = getCounts(location, windowIterator.getFilterContainers(1, location));

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}
		
		return countFilter.filter(variantBaseIndexs, parallelData, counts1, counts2);
	}
	
}