package jacusa.filter;

import jacusa.pileup.BaseCount;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.WindowCache;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.Location;

public abstract class AbstractWindowStorageFilter<T extends Data<T> & hasBaseCount & hasRefBase & hasCoordinate> extends AbstractStorageFilter<T> {

	public AbstractWindowStorageFilter(final char c) {
		super(c);
	}

	protected BaseCount[] getCounts(final Location location, 
			FilterContainer[] replicateFilterContainer) {
		final int n = replicateFilterContainer.length;
		BaseCount[] baseCount = new BaseCount[n];

		// correct orientation in U,S S,U cases
		boolean invert = false;
		if (location.strand == STRAND.REVERSE && replicateFilterContainer[0].getStrand() == STRAND.UNKNOWN) {
			invert = true;
		}
		
		for (int i = 0; i < n; ++i) {
			final FilterContainer filterContainer = replicateFilterContainer[i];
			final WindowCache windowCache = getData(filterContainer);
			final int windowPosition = filterContainer.getWindowCoordinates().convert2WindowPosition(location.genomicPosition);

			baseCount[i] = windowCache.getBaseCount(windowPosition);
			if (invert) {
				baseCount[i].invert();
			}
			
		}

		return baseCount;
	}

}