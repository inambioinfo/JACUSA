package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.data.BaseConfig;
import jacusa.filter.FilterContainer;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;

// TODO strand 

/**
 * @author Michael Piechotta
 *
 */
public abstract class AbstractStrandedPileupBuilder<T extends BaseQualData> 
extends AbstractPileupBuilder<T> {

	protected WindowCache[] windowCaches;

	protected FilterContainer<T> filterContainersReverse;
	protected FilterContainer<T> filterContainersForward;
	
	protected int[][] byte2intAr;
	
	public AbstractStrandedPileupBuilder(final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters,
			final LibraryType libraryType) {
		super(windowCoordinates, STRAND.FORWARD, reader, condition, parameters, libraryType);

		/* Ar[0, 1]
		 * 0 -> reversed
		 * 1 -> forward
		 * 
		 * Depending on the observer strand we switch 
		 * between 0, 1
		 */

		windowCaches	= new WindowCache[2];
		windowCaches[0] = new WindowCache(windowCoordinates, baseConfig.getBases().length);
		windowCaches[1] = windowCache;

		filterContainersReverse = parameters.getFilterConfig().createFilterContainer(windowCoordinates, STRAND.REVERSE, condition);
		filterContainersForward = filterContainer;

		final BaseConfig baseConfig = parameters.getBaseConfig();
		byte2intAr = new int[2][baseConfig.getbyte2int().length];
		byte2intAr[0] = baseConfig.getComplementbyte2int();
		byte2intAr[1] = byte2int;
	}

	@Override
	public void clearCache() {
		super.clearCache();

		for (WindowCache windowCache : windowCaches) {
			windowCache.clear();
		}
		
		filterContainersReverse.clear();
		filterContainersForward.clear();
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		int i = strand.integer() - 1;
		return windowCaches[i].getCoverage(windowPosition);
	}

	@Override
	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		int i = strand.integer() - 1;
		return i == 0 ? filterContainersReverse : filterContainersForward;
	}

	@Override
	public T getData(int windowPosition, STRAND strand) {
		T dataContainer = parameters.getMethodFactory().createDataContainer();
		
		dataContainer.setContig(windowCoordinates.getContig()); 
		dataContainer.setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.setStrand(strand);

		WindowCache windowCache = getWindowCache(strand);

		// copy base and qual info from cache
		dataContainer.setBaseQualCount(windowCache.getBaseCount(windowPosition));

		byte refBaseByte = windowCache.getReferenceBase(windowPosition);
		if (refBaseByte != (byte)'N') {
			dataContainer.setReferenceBase((char)refBaseByte);
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
	
	protected void switchByStrand() {
		int i = strand.integer() - 1;
		// makes sure that for reads on the reverse strand the complement is stored in pileup and filters
		byte2int = byte2intAr[i]; 
		filterContainer = i == 0 ? filterContainersReverse : filterContainersForward;
		windowCache = windowCaches[i];
	}
	
}