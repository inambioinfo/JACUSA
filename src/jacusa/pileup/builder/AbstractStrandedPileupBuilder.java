package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.filter.FilterContainer;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;

/**
 * @author Michael Piechotta
 *
 */
public class AbstractStrandedPileupBuilder<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractPileupBuilder<T> {

	protected WindowCache[] windowCaches;

	protected FilterContainer[] filterContainers;
	protected int[][] byte2intAr;
	
	public AbstractStrandedPileupBuilder(
			final T dataContainer,
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters condition,
			final AbstractParameters<T> parameters,
			final LibraryType libraryType) {
		super(dataContainer, windowCoordinates, STRAND.FORWARD, reader, condition, parameters, libraryType);

		/* Ar[0, 1]
		 * 0 -> reversed
		 * 1 -> forward
		 * 
		 * Depending on the observer strand we switch 
		 * between 0, 1
		 */
		
		windowCaches	= new WindowCache[2];
		windowCaches[0] = new WindowCache(windowCoordinates, baseConfig.getBaseLength());
		windowCaches[1] = windowCache;
		
		filterContainers = new FilterContainer[2];
		filterContainers[0] = parameters.getFilterConfig().createFilterContainer(windowCoordinates, STRAND.REVERSE, condition);
		filterContainers[1] = filterContainer;

		final BaseConfig baseConfig = parameters.getBaseConfig();
		byte2intAr = new int[2][baseConfig.getByte2Int().length];
		byte2intAr[0] = baseConfig.getComplementByte2Int();
		byte2intAr[1] = byte2int;
	}

	@Override
	public void clearCache() {
		super.clearCache();

		for (WindowCache windowCache : windowCaches) {
			windowCache.clear();
		}
		
		for (FilterContainer filterContainer : filterContainers) {
			filterContainer.clear();
		}
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		int i = strand.integer() - 1;
		return windowCaches[i].getCoverage(windowPosition);
	}

	@Override
	public FilterContainer getFilterContainer(int windowPosition, STRAND strand) {
		int i = strand.integer() - 1;
		return filterContainers[i];
	}

	@Override
	public T getData(int windowPosition, STRAND strand) {
		dataContainer = dataContainer.copy();
		// FIXME baseConfig.getBaseLength()
		dataContainer.setContig(windowCoordinates.getContig()); 
		dataContainer.setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.setStrand(strand);

		int i = strand.integer() - 1;
		WindowCache windowCache = windowCaches[i];

		// copy base and qual info from cache
		dataContainer.setBaseCount(windowCache.getBaseCount(windowPosition));

		byte refBaseByte = windowCache.getReferenceBase(windowPosition);
		if (refBaseByte != (byte)'N') {
			dataContainer.setRefBase((char)refBaseByte);
		}
		
		// for "Stranded"PileupBuilder the basesCounts in the pileup are already inverted (when on the reverse strand) 
		return dataContainer;
	}

	@Override
	public WindowCache getWindowCache(STRAND strand) {
		int i = strand.integer() - 1;
		return windowCaches[i];
	}
	
	@Override
	public boolean isCovered(int windowPosition, STRAND strand) {
		return getCoverage(windowPosition, strand) >= condition.getMinCoverage();
	}

	@Override
	protected void addHighQualityBaseCall(int windowPosition, int baseI, int qual, STRAND strand) {
		int i = strand.integer() - 1;
		windowCaches[i].addHighQualityBaseCall(windowPosition, baseI, qual);
	}

	@Override
	protected void addLowQualityBaseCall(int windowPosition, int baseI, int qual, STRAND strand) {
		int i = strand.integer() - 1;
		windowCaches[i].addLowQualityBaseCall(windowPosition, baseI, qual);
	}
	
}