package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.storage.DummyFilterFillCache;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.WindowCoordinates;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class MaxAlleleCountFilterFactory<T extends BaseQualData> 
extends AbstractFilterFactory<T> {

	//
	private static final int MAX_ALLELES = 2;
	//
	private int alleles;
	//
	private AbstractParameters<T> parameters;
	//
	private boolean strict;
	
	public MaxAlleleCountFilterFactory(AbstractParameters<T> parameters) {
		super('M', 
				"Max allowed alleles per parallel pileup. Default: "+ MAX_ALLELES);
		alleles = MAX_ALLELES;
		this.parameters = parameters;
		strict = parameters.collectLowQualityBaseCalls();
	}
	
	@Override
	public DummyFilterFillCache createFilterStorage(
			WindowCoordinates windowCoordinates,
			ConditionParameters<T> condition) {
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
				this.alleles = alleleCount;
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
		public boolean filter(final Result<T> result, final WindowIterator<T> windowIterator) {
			final ParallelPileupData<T> parallelData = result.getParellelData();
			return parallelData.getCombinedPooledData()
					.getBaseQualCount().getAlleles().length > alleles;
		}
	}
	
	private class MaxAlleleStrictFilter extends AbstractStorageFilter<T> {
		
		public MaxAlleleStrictFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final Result<T> result, final WindowIterator<T> windowIterator) {
			return windowIterator.getAlleleCount(result.getParellelData().getCoordinate()) > alleles;
		}
	}
	
}