package jacusa.filter;

import java.util.List;

import jacusa.data.BaseQualCount;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.WindowCache;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

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
	protected BaseQualCount[] getBaseQualCounts(final Coordinate coordinate, 
			final List<FilterContainer<T>> filterContainers) {
		final int n = filterContainers.size();
		BaseQualCount[] baseQualCount = new BaseQualCount[n];

		// FIXME
		// correct orientation in U,S S,U cases
		boolean invert = false;
		if (coordinate.getStrand() == STRAND.REVERSE && filterContainers.get(0).getStrand() == STRAND.UNKNOWN) {
			invert = true;
		}
		
		for (int replicateIndex = 0; replicateIndex < n; ++replicateIndex) {
			final FilterContainer<T> filterContainer = filterContainers.get(replicateIndex);
			final WindowCache windowCache = getWindowCache(filterContainer);
			final int windowPosition = filterContainer.getWindowCoordinates().convert2WindowPosition(coordinate.getPosition());

			baseQualCount[replicateIndex] = windowCache.getBaseCount(windowPosition);
			if (invert) {
				baseQualCount[replicateIndex].invert();
			}
		}

		return baseQualCount;
	}

}
