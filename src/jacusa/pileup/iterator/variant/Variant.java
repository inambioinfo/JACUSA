package jacusa.pileup.iterator.variant;

import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;

public interface Variant<T extends Data<T>> {
	
	boolean isValid(final ParallelData<T> parallelData);
	
}