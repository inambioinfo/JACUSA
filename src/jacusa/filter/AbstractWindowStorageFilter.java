package jacusa.filter;

import java.util.List;

import jacusa.data.BaseQualCount;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.WindowCache;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.Location;

/**
 * 
 * @author Michael Piechotta
 *
 * @param <T>
 */
public abstract class AbstractWindowStorageFilter<T extends BaseQualData>
extends AbstractStorageFilter<T> {

	public AbstractWindowStorageFilter(final char c) {
		super(c);
	}

	// TODO check
	protected BaseQualCount[] getCounts(final Location location, 
			List<FilterContainer<T>> replicateFilterContainer) {
		final int n = replicateFilterContainer.size();
		BaseQualCount[] baseCount = new BaseQualCount[n];

		// FIXME
		// correct orientation in U,S S,U cases
		boolean invert = false;
		if (location.strand == STRAND.REVERSE && replicateFilterContainer.get(0).getStrand() == STRAND.UNKNOWN) {
			invert = true;
		}
		
		for (int i = 0; i < n; ++i) {
			final FilterContainer<T> filterContainer = replicateFilterContainer.get(i);
			final WindowCache windowCache = getWindowCache(filterContainer);
			final int windowPosition = filterContainer.getWindowCoordinates().convert2WindowPosition(location.genomicPosition);

			baseCount[i] = windowCache.getBaseCount(windowPosition);
			if (invert) {
				baseCount[i].invert();
			}
			
		}

		return baseCount;
	}

}
