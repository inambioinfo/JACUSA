package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
import jacusa.filter.FilterContainer;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.DefaultPileup;
import jacusa.pileup.Pileup;
import jacusa.pileup.DefaultPileup.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;

/**
 * @author Michael Piechotta
 *
 */
public class AbstractStrandedPileupBuilder extends AbstractPileupBuilder {

	protected WindowCache[] windowCaches;

	protected FilterContainer[] filterContainers;
	protected int[][] byte2intAr;
	
	public AbstractStrandedPileupBuilder(
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final SampleParameters sample,
			final AbstractParameters parameters,
			final LibraryType libraryType) {
		super(windowCoordinates, STRAND.FORWARD, reader, sample, parameters, libraryType);

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
		filterContainers[0] = parameters.getFilterConfig().createFilterContainer(windowCoordinates, STRAND.REVERSE, sampleParameters);
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
	public Pileup getPileup(int windowPosition, STRAND strand) {
		final Pileup pileup = new DefaultPileup(
				windowCoordinates.getContig(), 
				windowCoordinates.getGenomicPosition(windowPosition), 
				strand, 
				baseConfig.getBaseLength());

		int i = strand.integer() - 1;
		WindowCache windowCache = windowCaches[i];

		// copy base and qual info from cache
		pileup.setCounts(windowCache.getCounts(windowPosition));

		byte refBaseByte = windowCache.getReferenceBase(windowPosition);
		if (refBaseByte != (byte)'N') {
			pileup.setRefBase((char)refBaseByte);
		}
		
		// for "Stranded"PileupBuilder the basesCounts in the pileup are already inverted (when on the reverse strand) 
		return pileup;
	}

	@Override
	public WindowCache getWindowCache(STRAND strand) {
		int i = strand.integer() - 1;
		return windowCaches[i];
	}
	
	@Override
	public boolean isCovered(int windowPosition, STRAND strand) {
		return getCoverage(windowPosition, strand) >= sampleParameters.getMinCoverage();
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