package jacusa.pileup.builder;

import java.util.Arrays;

import jacusa.data.BaseQualReadInfoData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LibraryType;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilder<T extends BaseQualReadInfoData>
implements DataBuilder<T> {
	
	private final int[] readStartCount;
	private final int[] readEndCount;
	private final DataBuilder<T> dataBuilder;
	
	public RTArrestPileupBuilder(final DataBuilder<T> dataBuilder) {
		this.dataBuilder = dataBuilder;
		
		final int windowSize = dataBuilder.getWindowCoordinates().getWindowSize();
		readStartCount	= new int[windowSize];
		readEndCount	= new int[windowSize];
	}
	
	@Override
	public T getData(int windowPosition, STRAND strand) {
		T dataContainer = dataBuilder.getData(windowPosition, strand);

		dataContainer.getReadInfoCount().setStart(readStartCount[windowPosition]);
		dataContainer.getReadInfoCount().setEnd(readEndCount[windowPosition]);

		int arrest = 0;
		int through = 0;

		switch (getLibraryType()) {
		
		case UNSTRANDED:
			arrest 	+= dataContainer.getReadInfoCount().getStart();
			arrest 	+= dataContainer.getReadInfoCount().getEnd();
			through += dataContainer.getReadInfoCount().getInner();
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= dataContainer.getReadInfoCount().getEnd();
			through += dataContainer.getReadInfoCount().getInner();
			break;

		case FR_SECONDSTRAND:
			arrest 	+= dataContainer.getReadInfoCount().getStart();
			through += dataContainer.getReadInfoCount().getInner();
			break;				
		}

		dataContainer.getReadInfoCount().setArrest(arrest);
		dataContainer.getReadInfoCount().setThrough(through);
		
		return dataContainer;
	}

	public void processRecord(SAMRecord record) {
		dataBuilder.processRecord(record);
		
		int genomicPosition = record.getAlignmentStart();
		int windowPosition  = getWindowCoordinates().convert2WindowPosition(genomicPosition);
		
		if (windowPosition >= 0) {
			readStartCount[windowPosition] += 1;
		}
		int windowPositionReadEnd = getWindowCoordinates().convert2WindowPosition(record.getAlignmentEnd());
		if (windowPositionReadEnd >= 0) {
			readEndCount[windowPositionReadEnd] += 1;
		}
	}
	
	@Override
	public void clearCache() {
		dataBuilder.clearCache();

		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);		
	}

	@Override
	public boolean isCovered(int windowPosition, STRAND strand) {
		return dataBuilder.isCovered(windowPosition, strand);
	}

	@Override
	public int getCoverage(final int windowPosition, final STRAND strand) {
		return getCoverage(windowPosition, strand);
	}

	@Override
	public WindowCache getWindowCache(final STRAND strand) {
		return getWindowCache(strand);
	}

	@Override
	public FilterContainer<T> getFilterContainer(final int windowPosition, final STRAND strand) {
		return getFilterContainer(windowPosition, strand);
	}

	@Override
	public SAMRecord getNextValidRecord(final int targetPosition) {
		return dataBuilder.getNextValidRecord(targetPosition);
	}

	@Override
	public boolean adjustWindowStart(final int genomicWindowStart) {
		return dataBuilder.adjustWindowStart(genomicWindowStart);
	}

	@Override
	public int getFilteredSAMRecords() {
		return dataBuilder.getFilteredSAMRecords();
	}

	@Override
	public WindowCoordinates getWindowCoordinates() {
		return dataBuilder.getWindowCoordinates();
	}

	@Override
	public LibraryType getLibraryType() {
		return dataBuilder.getLibraryType();
	}

}