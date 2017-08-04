package jacusa.filter;

import jacusa.pileup.Data;
import jacusa.pileup.Result;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.WindowCache;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Location;

public abstract class AbstractStorageFilter<T extends Data<T> & hasCoordinate & hasBaseCount & hasRefBase> {

	private final char c;

	public AbstractStorageFilter(final char c) {
		this.c = c;
	}
	
	public final char getC() {
		return c;
	}
	
	protected WindowCache getData(FilterContainer filterContainer) {
		int filterI = filterContainer.getFilterConfig().c2i(c);

		WindowCache data = filterContainer.get(filterI).getContainer();

		return data;
	}

	protected abstract boolean filter(
			final Result<T> result, 
			final Location location, 
			final WindowIterator<T> windowIterator);
	
	public boolean applyFilter(
			final Result<T> result, 
			final Location location, 
			final WindowIterator<T> windowIterator) {
		if (filter(result, location, windowIterator)) {
			addFilterInfo(result);
			return true;
		}
		
		return false;
	}

	public void addFilterInfo(Result<T> result) {
		result.getFilterInfo().add(Character.toString(getC()));
	}

}