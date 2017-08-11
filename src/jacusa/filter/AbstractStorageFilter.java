package jacusa.filter;

import jacusa.data.AbstractData;
import jacusa.data.Result;
import jacusa.pileup.builder.WindowCache;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Location;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractStorageFilter<T extends AbstractData> {

	private final char c;

	public AbstractStorageFilter(final char c) {
		this.c = c;
	}

	/**
	 * 
	 * @return
	 */
	public final char getC() {
		return c;
	}
	
	/**
	 * 
	 * @param filterContainer
	 * @return
	 */
	protected WindowCache getWindowCache(final FilterContainer<T> filterContainer) {
		int filterIndex = filterContainer.getFilterConfig().c2i(c);
		WindowCache windowCache = filterContainer.get(filterIndex).getContainer();
		return windowCache;
	}

	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	protected abstract boolean filter(final Result<T> result, 
			final Location location, 
			final WindowIterator<T> windowIterator);
	
	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	public boolean applyFilter(final Result<T> result, 
			final Location location, 
			final WindowIterator<T> windowIterator) {
		if (filter(result, location, windowIterator)) {
			addFilterInfo(result);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param result
	 */
	public void addFilterInfo(Result<T> result) {
		result.getFilterInfo().add(Character.toString(getC()));
	}

}
