package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

/**
 * @author Michael Piechotta
 *
 */
public class UnstrandedPileupBuilder<T extends BaseQualData> 
extends AbstractDataBuilder<T> {
	
	public UnstrandedPileupBuilder(final WindowCoordinates windowCoordinates,
			final SAMFileReader SAMFileReader,
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, 
				SAMFileReader, 
				condition, 
				parameters, 
				LibraryType.UNSTRANDED);
	}

	@Override
	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		return filterContainer;
	}
	
	@Override
	public T getData(int windowPosition, STRAND strand) {
		T dataContainer = parameters.getMethodFactory().createDataContainer();

		dataContainer.getCoordinate().setSequenceName(windowCoordinates.getContig()); 
		dataContainer.getCoordinate().setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.getCoordinate().setStrand(strand);

		// copy base and qual info from cache
		dataContainer.setBaseQualCount(windowCache.getBaseCount(windowPosition));

		byte referenceBaseByte = windowCache.getReferenceBase(windowPosition);
		if (referenceBaseByte != (byte)'N') {
			dataContainer.setReferenceBase((char)referenceBaseByte);
		}
		
		// and complement if needed
		if (strand == STRAND.REVERSE) {
			dataContainer.getBaseQualCount().invert();
		}

		return dataContainer;
	}

	@Override
	protected void addHighQualityBaseCall(int windowPosition, int baseIndex, 
			int qualIndex, STRAND strand) {
		windowCache.addHighQualityBaseCall(windowPosition, baseIndex, qualIndex);
	}
	
	@Override
	protected void addLowQualityBaseCall(int windowPosition, int baseIndex, 
			int qualIndex, STRAND strand) {
		windowCache.addLowQualityBaseCall(windowPosition, baseIndex, qualIndex);
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