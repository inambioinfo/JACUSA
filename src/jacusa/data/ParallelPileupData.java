package jacusa.data;

import jacusa.util.Coordinate;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ParallelPileupData<T extends AbstractData> 
implements hasCoordinate {
	
	private Coordinate coordinate;

	private T[][] data;
	private T[] cachedCombinedData;
	
	private T[] cachedPooledData;
	private T cachedCombinedPooledData;

	private int cachedTotalReplicates;
	
	public ParallelPileupData() {
		coordinate 				= new Coordinate();		
		cachedTotalReplicates 	= -1;
	}

	public ParallelPileupData(final Coordinate coordinate,
			final T[][] data) {
		this.coordinate = new Coordinate(coordinate);
		
		int conditions = data.length;
		// copy data
		this.data = data;
		cachedTotalReplicates = 0;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			cachedTotalReplicates += data[conditionIndex].length;
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param parallelPileupData
	 */
	@SuppressWarnings("unchecked")
	public ParallelPileupData(final ParallelPileupData<T> parallelPileupData, T[][] data) {
		this.coordinate = new Coordinate(parallelPileupData.getCoordinate());

		int conditions = parallelPileupData.data.length;

		// copy data
		this.data = data;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			for (int replicateIndex = 0; replicateIndex < conditions; replicateIndex++) {
				data[conditionIndex][replicateIndex] = (T) parallelPileupData.getData(conditionIndex, replicateIndex).copy();
			}
		}

		cachedTotalReplicates = parallelPileupData.cachedTotalReplicates;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	// make this faster remove data and add new
	public void setData(int conditionIndex, T[] data) {
		System.arraycopy(data, 0, this.data[conditionIndex], 0, data.length);

		cachedCombinedData[conditionIndex] = null;
		cachedCombinedPooledData = null;
		cachedPooledData[conditionIndex] = null;
		cachedTotalReplicates = -1;
	}

	public void reset() {
		coordinate 	= new Coordinate();
		data 		= null;
		resetCache();
	}
	
	protected void resetCache() {
		cachedCombinedData 			= null;
		cachedCombinedPooledData 	= null;
		cachedPooledData 			= null;
		cachedTotalReplicates		= -1;
	}

	public int getReplicates(int conditionIndex) {
		return data[conditionIndex].length;
	}

	public int getTotalReplicates() {
		if (cachedTotalReplicates == -1) {
			cachedTotalReplicates = 0;
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				cachedTotalReplicates += data[conditionIndex].length;
			}
		}

		return cachedTotalReplicates;
	}

	public boolean isValid() {
		for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
			if (data[conditionIndex].length <= 0) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public T getPooledData(int conditionIndex) {
		if (cachedPooledData[conditionIndex] == null && getReplicates(conditionIndex) > 0) {
			
			cachedPooledData[conditionIndex] = (T) getData(conditionIndex, 0).copy();
			for (int replicateIndex = 1; replicateIndex < getReplicates(conditionIndex); replicateIndex++) {
				cachedPooledData[conditionIndex].add(getData(conditionIndex, replicateIndex));
			}
		}

		return cachedPooledData[conditionIndex];
	}

	@SuppressWarnings("unchecked")
	public T getCombinedPooledData() {
		if (cachedCombinedPooledData == null && getPooledData(0) != null) {

			cachedCombinedPooledData = (T) getPooledData(0).copy();
			for (int conditionIndex = 1; conditionIndex < getConditions(); conditionIndex++) {
				cachedCombinedPooledData.add(getPooledData(conditionIndex));
			}
		}

		return cachedCombinedPooledData;
	}
	
	public T[] getCombinedData(T[] container) {
		if (cachedCombinedData == null) {
			cachedCombinedData = container;

			int dest = 0;;
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				System.arraycopy(
						data[conditionIndex], 
						0, 
						cachedCombinedData[conditionIndex], 
						dest, 
						getReplicates(conditionIndex));
				dest += getReplicates(conditionIndex);
			}
		}
		
		return cachedCombinedData;
	}

	public T getData(int conditionIndex, int replicateIndex) {
		return data[conditionIndex][replicateIndex];
	}

	public int getConditions() {
		return data.length;
	}

	public T[] getData(int conditionIndex) {
		return data[conditionIndex];
	}

	public ParallelPileupData<T> copy(T[][] data) {
		return new ParallelPileupData<T>(this, data);
	}

	public static <S extends BaseQualData> int[] getNonReferenceBaseIndexs(ParallelPileupData<S> parallelData) {
		final char referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
		if (referenceBase == 'N') {
			return new int[0];
		}
	
		final int[] allelesIndexs = parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
				.getAlleles();
		
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

	// suffices that one replicate contains replicate
	public static <S extends BaseQualData> int[] getVariantBaseIndexs(ParallelPileupData<S> parallelData) {
		int n = 0;
		int[] alleles = parallelData.getCombinedPooledData().getBaseQualCount().getAlleles();
		
		for (int baseIndex : alleles) {
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
				if (parallelData.getPooledData(conditionIndex).getBaseQualCount().getBaseCount(baseIndex) > 0) {
					alleles[baseIndex]++;
				}
			}
			if (alleles[baseIndex] > 0 && alleles[baseIndex] < parallelData.getConditions()) {
				++n;
			}
		}

		int[] variantBaseIs = new int[n];
		int j = 0;
		for (int baseIndex : alleles) {
			if (alleles[baseIndex] > 0 && alleles[baseIndex] < parallelData.getConditions()) {
				variantBaseIs[j] = baseIndex;
				++j;
			}
		}

		return variantBaseIs;
	}
	
	public static <S extends BaseQualData> S[] flat(final S[] data, 
			final S[]ret, 
			final int[] variantBaseIndexs, final int commonBaseIndex) {
		for (int i = 0; i < data.length; ++i) {
			ret[i] = data[i];

			for (int variantBaseIndex : variantBaseIndexs) {
				ret[i].getBaseQualCount().add(commonBaseIndex, variantBaseIndex, data[i].getBaseQualCount());
				ret[i].getBaseQualCount().substract(variantBaseIndex, variantBaseIndex, data[i].getBaseQualCount());
			}
			
		}
		return ret;
	}
	
	
}
