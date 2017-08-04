package jacusa.filter.counts;

import jacusa.pileup.BaseConfig;
import jacusa.pileup.BaseCount;
import jacusa.pileup.Data;
import jacusa.pileup.DefaultParallelData;
import jacusa.pileup.ParallelData;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasRefBase;

public abstract class AbstractCountFilter<T extends Data<T> & hasBaseCount & hasRefBase> {

	protected final BaseConfig baseConfig;

	public AbstractCountFilter(final BaseConfig baseConfig) {
		this.baseConfig 	= baseConfig;
	}

	// ORDER RESULTS [0] SHOULD BE THE VARIANTs TO TEST
	public int[] getVariantBaseIndexs(final ParallelData<T> parallelData) {
		final int[] variantBasesIs = DefaultParallelData.getVariantBaseIndexs(parallelData);
		final int[] allelesIndexs = parallelData
				.getCombinedPooledData()
				.getBaseCount()
				.getAlleles();
		final char refBase = parallelData.getCombinedPooledData().getRefBase();

		// A | G
		// define all non-reference bases as potential variants
		if (DefaultParallelData.isHoHo(parallelData)) {
			if (refBase == 'N') {
				return new int[0];
			}
			final int refBaseIndex = baseConfig.getBaseI((byte)refBase);

			// find non-reference base(s)
			int i = 0;
			final int[] tmp = new int[allelesIndexs.length];
			for (final int baseIndex : allelesIndexs) {
				if (baseIndex != refBaseIndex) {
					tmp[i] = baseIndex;
					++i;
				}
			}
			final int[] ret = new int[i];
			System.arraycopy(tmp, 0, ret, 0, i);
			return ret;
		}

		// A | AG
		if (variantBasesIs.length >= 1) {
			return variantBasesIs;
		}

		// condition1: AG | AG AND condition2: AGC |AGC
		// return allelesIs;
		return new int[0];
	}
	
	/**
	 * null if filter did not change anything
	 * @param extendedPileups
	 * @return
	 */
	protected T[] applyFilter(final int variantBaseIndex, final T[] pileups, final BaseCount[] counts) {
		@SuppressWarnings("unchecked")
		final T[] filtered = (T[]) new Object[pileups.length];

		// indicates if something has been filtered
		boolean processed = false;
		
		for (int pileupIndex = 0; pileupIndex < pileups.length; ++pileupIndex) {
			filtered[pileupIndex] = pileups[pileupIndex].copy();
			final BaseCount count = counts[pileupIndex];
			if (count != null) { 
				filtered[pileupIndex].getBaseCount().substract(variantBaseIndex, count);
				processed = true;
			}
		}

		return processed ? filtered : null;
	}

	final protected ParallelData<T> applyFilter(final int variantBaseI, 
			final ParallelData<T> parallelData, 
			BaseCount[] baseCounts1, BaseCount[] baseCounts2) {
		final T[] filteredParallelData1 = applyFilter(variantBaseI, parallelData.getData(0), baseCounts1);
		final T[] filteredParallelData2 = applyFilter(variantBaseI, parallelData.getData(1), baseCounts2);

		if (filteredParallelData1 == null && filteredParallelData2 == null) {
			// nothing has been filtered
			return null;
		}

		

		
		T[] data1 = null;
		if (filteredParallelData1 == null) {
			data1 = parallelData.getData(0);
		} else {
			data1 = filteredParallelData1;
		}
		
		T[] data2 = null;
		if (filteredParallelData2 == null) {
			data2 = parallelData.getData(1);
		} else {
			data2 = filteredParallelData2;
		}
		
		@SuppressWarnings("unchecked")
		T[][] data = (T[][]) new Object[2][];
		System.arraycopy(data1, 0, data, 0, data1.length);
		System.arraycopy(data2, 0, data, data1.length, data2.length);
		
		final ParallelData<T> filteredParallelData = 
		new DefaultParallelData<T>(parallelData.getContig(),
				parallelData.getStart(),
				parallelData.getEnd(),
				parallelData.getStrand(),
				data);

		return filteredParallelData;
	}

	/**
	 * Apply filter on each variant base
	 */
	public boolean filter(final int[] variantBaseIndexs, 
			ParallelData<T> parallelData, 
			BaseCount[] baseCounts1, BaseCount[] baseCounts2) {
		// final int[] variantBaseIs = getVariantBaseIs(parallelPileup);

		for (int variantBaseI : variantBaseIndexs) {
			if (filter(variantBaseI, parallelData, baseCounts1, baseCounts2)) {
				return true;
			}
		}

		return false;
	}

	protected abstract boolean filter(final int variantBaseIndexs, 
			final ParallelData<T> parallelData, 
			BaseCount[] baseCounts1, BaseCount[] baseCounts2);

}