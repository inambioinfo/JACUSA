package jacusa.filter;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.storage.AbstractFilterStorage;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterConfig<T extends AbstractData> implements Cloneable {

	private final Map<Character, AbstractFilterFactory<T>> c2Factory;
	private final List<AbstractFilterFactory<T>> i2Factory;
	private final Map<Character, Integer> c2i;
	
	public FilterConfig() {
		final int initialCapacity = 6;

		c2Factory = new HashMap<Character, AbstractFilterFactory<T>>(initialCapacity);
		i2Factory = new ArrayList<AbstractFilterFactory<T>>(initialCapacity);
		c2i = new HashMap<Character, Integer>(initialCapacity);
	}

	/**
	 * 
	 * @param filterFactory
	 * @throws Exception
	 */
	public void addFactory(final AbstractFilterFactory<T> filterFactory) throws Exception {
		final char c = filterFactory.getC();

		if (c2Factory.containsKey(c)) {
			throw new Exception("Duplicate value: " + c);
		} else {
			c2i.put(c, i2Factory.size());
			i2Factory.add(filterFactory);
			c2Factory.put(c, filterFactory);	
		}
	}

	/**
	 * Create CountFilterCache for each available filter.
	 * Info: some filters might not need the cache
	 * 
	 * @return
	 */
	public FilterContainer<T> createFilterContainer(final WindowCoordinates windowCoordinates, 
			final STRAND strand, final ConditionParameters<T> condition) {
		AbstractFilterStorage[] filterStorage = new AbstractFilterStorage[i2Factory.size()];
		for (int filterIndex = 0; filterIndex < i2Factory.size(); ++filterIndex) {
			filterStorage[filterIndex] = i2Factory.get(filterIndex).createFilterStorage(windowCoordinates, condition);
		}
		FilterContainer<T> filterContainer = new FilterContainer<T>(this, filterStorage, strand, windowCoordinates);

		return filterContainer;
	}

	public List<AbstractFilterFactory<T>> getFactories() {
		return i2Factory;
	}

	public boolean hasFiters() {
		return c2Factory.size() > 0;
	}

	public boolean hasFilter(final char c) {
		return c2Factory.containsKey(c);
	}

	public int c2i(char c) {
		if (! hasFilter(c)) {
			return -1;
		}
		return c2i.get(c);
	}
	
}