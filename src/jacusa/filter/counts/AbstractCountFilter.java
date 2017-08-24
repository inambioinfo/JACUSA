package jacusa.filter.counts;


import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.data.BaseConfig;
import jacusa.data.BaseQualCount;
import jacusa.data.ParallelPileupData;

public abstract class AbstractCountFilter<T extends BaseQualData> {

	private AbstractParameters<T> parameters;

	public AbstractCountFilter(final AbstractParameters<T> parameters) {
		this.parameters	= parameters;
	}

	// ORDER RESULTS [0] SHOULD BE THE VARIANTs TO TEST
	public int[] getVariantBaseIndexs(final ParallelPileupData<T> parallelData) {
		final int[] variantBasesIs = ParallelPileupData.getVariantBaseIndexs(parallelData);
		final int[] allelesIndexs = parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getAlleles();
		final char referenceBase = parallelData.getCombinedPooledData().getReferenceBase();

		/* TODO
		
		// A | G
		// define all non-reference bases as potential variants
		//if (ParallelPileupData.isHoHo(parallelData)) {
		{
			if (referenceBase == 'N') {
				return new int[0];
			}
			final int referenceBaseIndex = BaseConfig.getInstance().getBaseIndex((byte)referenceBase);

			// find non-reference base(s)
			int i = 0;
			final int[] tmp = new int[allelesIndexs.length];
			for (final int baseIndex : allelesIndexs) {
				if (baseIndex != referenceBaseIndex) {
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
		 * 
		 */
		return new int[0];
	}
	
	/**
	 * null if filter did not change anything
	 * @param extendedPileups
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T[] applyFilter(final int variantBaseIndex, final T[] pileupData, final BaseQualData[] baseQualData) {
		// TODO
		final T[] filtered = (T[]) new Object[pileupData.length];

		// indicates if something has been filtered
		boolean processed = false;
		
		for (int pileupIndex = 0; pileupIndex < pileupData.length; ++pileupIndex) {
			filtered[pileupIndex] = (T) pileupData[pileupIndex].copy();
			final BaseQualData bQD = baseQualData[pileupIndex];
			if (bQD != null) { 
				// TODO 
				//filtered[pileupIndex].getBaseQualCount().substract(variantBaseIndex, bQD);
				processed = true;
			}
		}

		return processed ? filtered : null;
	}

	final protected ParallelPileupData<T> applyFilter(final int variantBaseIndex, 
			final ParallelPileupData<T> parallelData, 
			final BaseQualData[][] baseQualData) {
		
		T[][] filteredData = parameters.getMethodFactory().createDataContainer(parallelData.getConditions(), -1);
		int filtered = 0;
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			filteredData[conditionIndex] = 
					applyFilter(variantBaseIndex, parallelData.getData(conditionIndex), baseQualData[conditionIndex]);
			if (filteredData[conditionIndex] == null) {
				filteredData[conditionIndex] = parallelData.getData(conditionIndex);
			} else {
				filtered++;
			}
		}

		if (filtered == 0) {
			// nothing has been filtered
			return null;
		}

		final ParallelPileupData<T> filteredParallelData =
				new ParallelPileupData<T>(parallelData.getCoordinate(), filteredData);

		return filteredParallelData;
	}

	/**
	 * Apply filter on each variant base
	 */
	public boolean filter(final int[] variantBaseIndexs, 
			ParallelPileupData<T> parallelData, 
			BaseQualData[][] baseQualData) {

		for (int variantBaseIndex : variantBaseIndexs) {
			if (filter(variantBaseIndex, parallelData, baseQualData)) {
				return true;
			}
		}

		return false;
	}

	protected abstract boolean filter(final int variantBaseIndex, 
			final ParallelPileupData<T> parallelData, 
			BaseQualData[][] baseQualData);

}