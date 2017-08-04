package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.storage.DummyFilterFillCache;
import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Location;
import jacusa.util.WindowCoordinates;

public class MaxAlleleCountFilterFactors<T extends Data<T> & hasCoordinate & hasBaseCount & hasRefBase> extends AbstractFilterFactory<T> {

	private static int ALLELE_COUNT = 2;
	private int alleleCount;
	private AbstractParameters<T> parameters;
	private boolean strict;
	
	public MaxAlleleCountFilterFactors(AbstractParameters<T> parameters) {
		super(
				'M', 
				"Max allowed alleles per parallel pileup. Default: "+ ALLELE_COUNT);
		alleleCount = ALLELE_COUNT;
		this.parameters = parameters;
		strict = parameters.collectLowQualityBaseCalls();
	}
	
	@Override
	public DummyFilterFillCache createFilterStorage(
			WindowCoordinates windowCoordinates,
			ConditionParameters condition) {
		return new DummyFilterFillCache(getC());
	}

	@Override
	public AbstractStorageFilter<T> createStorageFilter() {
		if (strict) {
			return new MaxAlleleStrictFilter(getC());
		}
		return new MaxAlleleFilter(getC());
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

		for (int i = 1; i < s.length; ++i) {
			switch(i) {
			case 1:
				final int alleleCount = Integer.valueOf(s[i]);
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count " + line);
				}
				this.alleleCount = alleleCount;
				break;
		
			case 2:
				if (! s[i].equals("strict")) {
					throw new IllegalArgumentException("Did you mean strict? " + line);
				}
				parameters.collectLowQualityBaseCalls(true);
				strict = true;
				break;
			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}
	
	private class MaxAlleleFilter extends AbstractStorageFilter<T> {
		public MaxAlleleFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final Result<T> result, final Location location, final WindowIterator<T> windowIterator) {
			final ParallelData<T> parallelData = result.getParellelData();
			return parallelData.getCombinedPooledData()
					.getBaseCount().getAlleles().length > alleleCount;
		}
	}
	
	private class MaxAlleleStrictFilter extends AbstractStorageFilter<T> {
		
		public MaxAlleleStrictFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final Result<T> result, final Location location, final WindowIterator<T> windowIterator) {
			return windowIterator.getAlleleCount(location) > alleleCount;
		}
	}
	
}
