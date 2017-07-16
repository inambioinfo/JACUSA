package jacusa.filter;

import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.MinCountFilter;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.Counts;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Result;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.util.Location;

public class HomopolymerStorageFilter extends AbstractWindowStorageFilter {

	private AbstractCountFilter countFilter;
	
	public HomopolymerStorageFilter(final char c, BaseConfig baseConfig) {
		super(c);

		countFilter = new MinCountFilter(c, 1, baseConfig);
	}

	@Override
	protected boolean filter(final Result result, final Location location, final AbstractWindowIterator windowIterator) {
		final ParallelPileup parallelPileup = result.getParellelPileup();
		Counts[] counts1 = getCounts(location, windowIterator.getFilterContainers4Replicates1(location));
		Counts[] counts2 = getCounts(location, windowIterator.getFilterContainers4Replicates2(location));

		final int[] variantBaseIs = countFilter.getVariantBaseIs(parallelPileup);
		if (variantBaseIs.length == 0) {
			return false;
		}
		
		return countFilter.filter(variantBaseIs, parallelPileup, counts1, counts2);
	}
	
}