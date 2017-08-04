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

public class HomozygousFilterFactory<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractFilterFactory<T> {

	private int conditionIndex;
	private AbstractParameters<T> parameters;
	private boolean strict;
	
	public HomozygousFilterFactory(AbstractParameters<T> parameters) {
		super('H', "Filter non-homozygous pileup/BAM in condition 1 or 2 (MUST be set to H:1 or H:2). Default: none");
		conditionIndex = 0;
		this.parameters = parameters;
		strict = parameters.collectLowQualityBaseCalls();
	}

	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			throw new IllegalArgumentException("Invalid argument " + line);
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

		for (int i = 1; i < s.length; ++i) {
			switch(i) {
			case 1:
				final int conditionIndex = Integer.parseInt(s[1]);
				if (conditionIndex == 1 || conditionIndex == 2) {
					setConditionIndex(conditionIndex);
				}
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

	@Override
	public DummyFilterFillCache createFilterStorage(final WindowCoordinates windowCoordinates, final ConditionParameters condition) {
		// storage is not needed - done 
		// Low Quality Base Calls are stored in AbstractBuilder 
		return new DummyFilterFillCache(getC());
	}
	
	public final void setConditionIndex(final int conditionIndex) {
		this.conditionIndex = conditionIndex;
	}

	public final int getConditionIndex() {
		return conditionIndex;
	}

	@Override
	public AbstractStorageFilter<T> createStorageFilter() {
		if (strict) {
			return new HomozygousStrictFilter(getC());
		}
		
		return new HomozygousFilter(getC());
	}

	private class HomozygousStrictFilter extends AbstractStorageFilter<T> {

		public HomozygousStrictFilter(final char c) {
			super(c);
		}

		@Override
		public boolean filter(final Result<T> result, final Location location, final WindowIterator<T> windowIterator) {
			int alleles = 0;
			alleles = windowIterator.getAlleleCount(conditionIndex, location);
	
			if (alleles > 1) {
				return true;
			}
	
			return false;
		}

	}
	
	private class HomozygousFilter extends AbstractStorageFilter<T> {

		public HomozygousFilter(final char c) {
			super(c);
		}

		@Override
		public boolean filter(final Result<T> result, final Location location, final WindowIterator<T> windowIterator) {
			int alleles = 0;
			final ParallelData<T> parallelData = result.getParellelData();
	
			alleles = parallelData
				.getPooledData(conditionIndex)
				.getBaseCount()
				.getAlleles().length;
	
			if (alleles > 1) {
				return true;
			}
	
			return false;
		}

	}
	
}