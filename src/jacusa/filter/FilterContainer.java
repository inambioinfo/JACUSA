package jacusa.filter;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.samtools.CigarOperator;

import jacusa.data.AbstractData;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.storage.AbstractFilterStorage;
import jacusa.util.WindowCoordinates;
import jacusa.util.Coordinate.STRAND;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterContainer<T extends AbstractData> {

	private FilterConfig<T> filterConfig;
	private AbstractFilterStorage[] filterStorage;
	private List<AbstractFilterStorage> cigarFilters;
	private List<AbstractFilterStorage> processRecordFilters;

	private WindowCoordinates windowCoordinates;

	private Map<CigarOperator, Set<AbstractFilterStorage>> cigar2cFilter;
	
	private STRAND strand;
	
	public FilterContainer(
			final FilterConfig<T> filterConfig, 
			final AbstractFilterStorage[] filters,
			final STRAND strand,
			final WindowCoordinates windowCoordinates) {
		this.filterConfig = filterConfig;
		this.strand	= strand;
		this.windowCoordinates = windowCoordinates;
		this.filterStorage = filters;
		
		cigarFilters = new ArrayList<AbstractFilterStorage>(filters.length);
		processRecordFilters = new ArrayList<AbstractFilterStorage>(filters.length);
		cigar2cFilter = new HashMap<CigarOperator, Set<AbstractFilterStorage>>();

		for (final AbstractFilterStorage filter : filters) {
			// get filter factory
			final char c = filter.getC();
			final int i = filterConfig.c2i(c);
			final AbstractFilterFactory<T> filterFactory = filterConfig.getFactories().get(i);

			if (filterFactory.hasFilterByRecord()) {
				processRecordFilters.add(filter);
			}
			
			if (filterFactory.hasFilterByCigar()) {
				cigarFilters.add(filter);
				for (final CigarOperator cigarOperator : filterFactory.getCigarOperators()) {
					if (! cigar2cFilter.containsKey(cigarOperator)) {
						cigar2cFilter.put(cigarOperator, new HashSet<AbstractFilterStorage>());
					}
					cigar2cFilter.get(cigarOperator).add(filter);
				}
			}
		}
	}

	public void clear() {
		for (AbstractFilterStorage filter : cigarFilters) {
			filter.clearContainer();
		}
	}

	public AbstractFilterStorage get(int filterIndex) {
		return filterStorage[filterIndex];
	}
	
	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}

	public WindowCoordinates getWindowCoordinates() {
		return windowCoordinates;
	}

	public List<AbstractFilterStorage> getPR() {
		return processRecordFilters;
	}

	public STRAND getStrand() {
		return strand;
	}
	
	public Set<AbstractFilterStorage> get(CigarOperator cigarOperator) {
		if (! cigar2cFilter.containsKey(cigarOperator)) {
			return new HashSet<AbstractFilterStorage>();
		}

		return cigar2cFilter.get(cigarOperator);
		
	}

}