package jacusa.pileup;

import jacusa.util.Coordinate.STRAND;

public interface ParallelData<T extends Data<T>> {

	// coordinates
	String getContig();
	int getStart();
	int getEnd();

	// FIXME
	STRAND getStrand();
	STRAND getStrand(int conditionIndex);
	
	void setContig(String contig);
	void setStart(int start);
	void setEnd(int end);
	void setStrand(STRAND strand);
	
	T getData(final int conditionIndex, final int replicateIndex);
	T[] getData(final int conditionIndex);
	void setData(int conditionIndex, T[] data);
	
	T[] getCombinedData();

	T getPooledData(int conditionIndex);
	T getCombinedPooledData();
	
	void resetCache();

	int getReplicates(int conditionIndex);
	int getTotalReplicates();

	int getConditions();

	boolean isValid();

	String prettyPrint();

	ParallelData<T> copy();
	
}