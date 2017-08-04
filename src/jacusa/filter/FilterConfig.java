package jacusa.filter;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.storage.AbstractFilterStorage;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterConfig<T extends Data<T> & hasCoordinate & hasBaseCount & hasRefBase> implements Cloneable {

	private final Map<Character, AbstractFilterFactory<?>> c2Factory;
	private final List<AbstractFilterFactory<T>> i2Factory;
	private final Map<Character, Integer> c2i;
	
	public FilterConfig() {
		int initialCapacity = 6;

		c2Factory = new HashMap<Character, AbstractFilterFactory<?>>(initialCapacity);
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
	public FilterContainer<T> createFilterContainer(final WindowCoordinates windowCoordinates, final STRAND strand, final ConditionParameters condition) {
		AbstractFilterStorage[] filters = new AbstractFilterStorage[i2Factory.size()];
		for (int filterI = 0; filterI < i2Factory.size(); ++filterI) {
			filters[filterI] = i2Factory.get(filterI).createFilterStorage(windowCoordinates, condition);
			
		}
		FilterContainer<T> filterContainer = new FilterContainer<T>(this, filters, windowCoordinates, strand);

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