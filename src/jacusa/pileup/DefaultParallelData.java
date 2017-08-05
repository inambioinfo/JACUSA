package jacusa.pileup;

import jacusa.util.Coordinate.STRAND;

public class DefaultParallelData<T extends Data<T>> implements ParallelData<T> {

	private String contig;
	private int start;
	private int end;
	private STRAND strand;

	private Object[][] data;
	private Object[] cachedCombinedData;
	
	private T[] cachedPooledData;
	private T cachedCombinedPooledData;

	private int cachedTotalReplicates;
	
	public DefaultParallelData() {
		contig 	= new String();
		start 	= -1;
		end 	= -1;
		strand	= STRAND.UNKNOWN;
		
		cachedTotalReplicates = -1;
	}

	public DefaultParallelData(final String contig,
			final int start,
			final int end,
			final STRAND strand,
			final T[][] data) {
		this.contig = contig;
		this.start 	= start;
		this.end 	= end;
		this.strand = strand;

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
	 * @param parallelData
	 */
	public DefaultParallelData(final DefaultParallelData<T> parallelData) {
		contig 	= parallelData.getContig();
		start 	= parallelData.getStart();
		end 	= parallelData.getEnd();
		strand 	= parallelData.getStrand();

		int conditions = parallelData.data.length;
		// copy data
		data = new Object[conditions][];
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			for (int replicateIndex = 0; replicateIndex < conditions; replicateIndex++) {
				data[conditionIndex][replicateIndex] = parallelData.getData(conditionIndex, replicateIndex).copy();
			}
		}

		cachedTotalReplicates = parallelData.cachedTotalReplicates;
	}
	
	@Override
	public String getContig() {
		return contig;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public STRAND getStrand() {
		return strand;
	}

	@Override
	public void setContig(String contig) {
		this.contig = contig;
	}

	@Override
	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public void setStrand(STRAND strand) {
		this.strand = strand;
	}

	// make this faster remove data and add new
	@Override
	public void setData(int conditionIndex, T[] data) {
		System.arraycopy(data, 0, this.data[conditionIndex], 0, data.length);

		cachedCombinedData[conditionIndex] = null;
		cachedCombinedPooledData = null;
		cachedPooledData[conditionIndex] = null;
		cachedTotalReplicates = -1;
	}

	@Override
	public void resetCache() {
		cachedCombinedData 			= null;
		cachedCombinedPooledData 	= null;
		cachedPooledData 			= null;
		cachedTotalReplicates		= -1;
	}

	@Override
	public int getReplicates(int conditionIndex) {
		return data[conditionIndex].length;
	}

	@Override
	public int getTotalReplicates() {
		if (cachedTotalReplicates == -1) {
			cachedTotalReplicates = 0;
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				cachedTotalReplicates += data[conditionIndex].length;
			}
		}

		return cachedTotalReplicates;
	}

	@Override
	public boolean isValid() {
		for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
			if (data[conditionIndex].length <= 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	public T getPooledData(int conditionIndex) {
		if (cachedPooledData[conditionIndex] == null && getReplicates(conditionIndex) > 0) {
			
			cachedPooledData[conditionIndex] = (T) getData(conditionIndex, 0).copy();
			for (int replicateIndex = 1; replicateIndex < getReplicates(conditionIndex); replicateIndex++) {
				cachedPooledData[conditionIndex].add(getData(conditionIndex, replicateIndex));
			}
		}

		return cachedPooledData[conditionIndex];
	}

	@Override
	public T getCombinedPooledData() {
		if (cachedCombinedPooledData == null && getPooledData(0) != null) {

			cachedCombinedPooledData = (T) getPooledData(0).copy();
			for (int conditionIndex = 1; conditionIndex < getConditions(); conditionIndex++) {
				cachedCombinedPooledData.add(getPooledData(conditionIndex));
			}
		}

		return cachedCombinedPooledData;
	}

	@Override
	public String prettyPrint() {
		final StringBuilder sb = new StringBuilder();

		for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
			add(sb, Integer.toString(conditionIndex + 1), getPooledData(conditionIndex));
			for (int replicateIndex = 0; replicateIndex < getReplicates(conditionIndex); ++replicateIndex) {
				add(sb, Integer.toString(conditionIndex + 1) + replicateIndex, getData(conditionIndex, replicateIndex));
			}
		}

		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T[] getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = new Object[getTotalReplicates()];

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
		
		return (T[]) cachedCombinedData;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getData(int conditionIndex, int replicateIndex) {
		return (T) data[conditionIndex][replicateIndex];
	}

	@Override
	public int getConditions() {
		return data.length;
	}
	
	protected void add(StringBuilder sb, String condition, T data) {
		sb.append(condition);
		sb.append('\t');
		// sb.append(data.dataToString());
		sb.append('\n');
	}

	@Override
	public STRAND getStrand(int conditionIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T[] getData(int conditionIndex) {
		return (T[]) data[conditionIndex];
	}

	@Override
	public ParallelData<T> copy() {
		return new DefaultParallelData<T>(this);
	}

	public static <V extends Data<V> & hasBaseCount> int[] getVariantBaseIndexs(ParallelData<V> v) {
		// TODO check old ParallelPileup
		return null;
	}
	
	public static <V extends Data<V> & hasBaseCount> boolean isHoHo(ParallelData<V> v) {
		// TODO check old ParallelPileup
		return false;
	}
	
	public static <V extends Data<V> & hasBaseCount> V[] flat(V[] pileups, int[] variantBaseIs, int commonBaseI) {
		@SuppressWarnings("unchecked")
		V[] ret = (V[]) new Object[pileups.length];
		for (int i = 0; i < pileups.length; ++i) {
			ret[i] = pileups[i].copy();

			for (int variantBaseI : variantBaseIs) {
				ret[i].getBaseCount().add(commonBaseI, variantBaseI, pileups[i].getBaseCount());
				ret[i].getBaseCount().substract(variantBaseI, variantBaseI, pileups[i].getBaseCount());
			}
			
		}
		return ret;
	}
	
}
