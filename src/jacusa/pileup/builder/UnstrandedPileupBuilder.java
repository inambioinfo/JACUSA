/**
 * 
 */
package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.filter.FilterContainer;
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
public class UnstrandedPileupBuilder<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractPileupBuilder<T> {
	
	public UnstrandedPileupBuilder(final T dataContainer,
			final WindowCoordinates windowCoordinates,
			final SAMFileReader SAMFileReader,
			final ConditionParameters condition,
			final AbstractParameters<T> parameters) {
		super(dataContainer, 
				windowCoordinates, 
				STRAND.UNKNOWN, 
				SAMFileReader, 
				condition, 
				parameters, 
				LibraryType.UNSTRANDED);
	}

	public FilterContainer getFilterContainer(int windowPosition, STRAND strand) {
		return filterContainer;
	}
	
	@Override
	public T getData(int windowPosition, STRAND strand) {
		dataContainer = dataContainer.copy();

		dataContainer.setContig(windowCoordinates.getContig()); 
		dataContainer.setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.setStrand(strand);

		// copy base and qual info from cache
		dataContainer.setBaseCount(windowCache.getBaseCount(windowPosition));

		byte refBaseByte = windowCache.getReferenceBase(windowPosition);
		if (refBaseByte != (byte)'N') {
			dataContainer.setRefBase((char)refBaseByte);
		}
		
		// and complement if needed
		if (strand == STRAND.REVERSE) {
			dataContainer.getBaseCount().invert();
		}

		return dataContainer;
	}

	@Override
	protected void addHighQualityBaseCall(int windowPosition, int baseI, int qualI, STRAND strand) {
		windowCache.addHighQualityBaseCall(windowPosition, baseI, qualI);
	}
	
	@Override
	protected void addLowQualityBaseCall(int windowPosition, int baseI, int qualI, STRAND strand) {
		windowCache.addLowQualityBaseCall(windowPosition, baseI, qualI);
	}

	@Override
	public WindowCache getWindowCache(STRAND strand) {
		return windowCache;
	}

	/**
	 * 
	 * @param windowPosition
	 * @return
	 */
	@Override
	public boolean isCovered(int windowPosition, STRAND strand) {
		// for unstrandedPileup we ignore strand
		return getCoverage(windowPosition, STRAND.UNKNOWN) >= condition.getMinCoverage();
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		// for unstrandedPileup we ignore strand
		return windowCache.getCoverage(windowPosition);
	}

}