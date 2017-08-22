package jacusa.filter;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import jacusa.filter.factory.AbstractFilterFactory;
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

	private final Map<Character, AbstractFilterFactory<T>> c2factory;
	
	public FilterConfig() {
		c2factory = new HashMap<Character, AbstractFilterFactory<T>>(6);
	}

	/**
	 * 
	 * @param filterFactory
	 * @throws Exception
	 */
	public void addFactory(final AbstractFilterFactory<T> filterFactory) throws Exception {
		final char c = filterFactory.getC();

		if (c2factory.containsKey(c)) {
			throw new Exception("Duplicate value: " + c);
		} else {
			c2factory.put(c, filterFactory);	
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
		List<AbstractFilter<T>> filters = new ArrayList<AbstractFilter<T>>(c2factory.size());
		for (final AbstractFilterFactory<T> filterFactory : c2factory.values()) {
			filters.add(filterFactory.createFilter());
		}
		return new FilterContainer<T>(this, filters, strand, windowCoordinates, condition);
	}

	public boolean hasFiters() {
		return c2factory.size() > 0;
	}

	public boolean hasFilter(final char c) {
		return c2factory.containsKey(c);
	}
	
	public List<AbstractFilterFactory<T>> getFactories() {
		return new ArrayList<AbstractFilterFactory<T>>(c2factory.values());
	}
	
}
