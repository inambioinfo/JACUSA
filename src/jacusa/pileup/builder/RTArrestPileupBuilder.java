package jacusa.pileup.builder;

import java.util.Arrays;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualReadInfoData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

/**
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilder<T extends BaseQualReadInfoData>
implements DataBuilder<T> {

	private ConditionParameters<T> condition;
	private final DataBuilder<T> dataBuilder;
	
	private final int[] readStartCount;
	private final int[] readEndCount;
	
	public RTArrestPileupBuilder(final ConditionParameters<T> condition,
			final DataBuilder<T> dataBuilder) {
		this.condition = condition;
		this.dataBuilder = dataBuilder;
		
		final int windowSize = dataBuilder.getWindowCoordinates().getWindowSize();
		readStartCount	= new int[windowSize];
		readEndCount	= new int[windowSize];
	}
	
	@Override
	public T getData(final int windowPosition, final STRAND strand) {
		T dataContainer = dataBuilder.getData(windowPosition, strand);
		
		dataContainer.getReadInfoCount().setStart(readStartCount[windowPosition]);
		dataContainer.getReadInfoCount().setEnd(readEndCount[windowPosition]);
		dataContainer.getReadInfoCount().setInner(getCoverage(windowPosition, strand) - 
				readStartCount[windowPosition] - 
				readEndCount[windowPosition]);

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
	public int getCoverage(final int windowPosition, final STRAND strand) {
		return dataBuilder.getCoverage(windowPosition, strand);
	}

	@Override
	public WindowCache getWindowCache(final STRAND strand) {
		return dataBuilder.getWindowCache(strand);
	}

	@Override
	public FilterContainer<T> getFilterContainer(final int windowPosition, final STRAND strand) {
		return dataBuilder.getFilterContainer(windowPosition, strand);
	}

	@Override
	public SAMRecord getNextValidRecord(final int targetPosition) {
		return dataBuilder.getNextValidRecord(targetPosition);
	}

	@Override
	public SAMRecordIterator getIterator(int genomicWindowStart) {
		return dataBuilder.getIterator(genomicWindowStart);
	}
	
	@Override
	public int getSAMRecords() {
		return dataBuilder.getSAMRecords();
	}
	
	@Override
	public SAMRecord[] getSAMRecordsBuffer() {
		return dataBuilder.getSAMRecordsBuffer();
	}
	
	@Override
	public int processIterator(SAMRecordIterator iterator) {
		int SAMReocordsInBuffer = 0;
		while (iterator.hasNext()) {
			SAMRecord record = iterator.next();

			if(condition.isValid(record)) {
				getSAMRecordsBuffer()[SAMReocordsInBuffer++] = record;
				dataBuilder.incrementSAMRecords();
			} else {
				dataBuilder.incrementFilteredSAMRecords();
			}

			// process buffer
			if (SAMReocordsInBuffer >= dataBuilder.getSAMRecordsBuffer().length) {
				SAMReocordsInBuffer = processBuffer(SAMReocordsInBuffer, dataBuilder.getSAMRecordsBuffer());
			}
		}
		iterator.close();
		
		return SAMReocordsInBuffer;
	}
	
	@Override
	public void incrementFilteredSAMRecords() {
		dataBuilder.incrementFilteredSAMRecords();
	}
	
	@Override
	public void incrementSAMRecords() {
		dataBuilder.incrementSAMRecords();
	}
	
	@Override
	public int processBuffer(int SAMReocordsInBuffer, SAMRecord[] SAMRecordsBuffer) {
		for (int i = 0; i < SAMReocordsInBuffer; ++i) {
			try {
				processRecord(SAMRecordsBuffer[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return 0;
	}
	
	// bad code duplication
	public boolean adjustWindowStart(int genomicWindowStart) {
		clearCache();

		// get iterator to fill the window
		SAMRecordIterator iterator = getIterator(genomicWindowStart);
		final int SAMReocordsInBuffer = processIterator(iterator);

		if (SAMReocordsInBuffer > 0) {
			// process any left SAMrecords in the buffer
			processBuffer(SAMReocordsInBuffer, getSAMRecordsBuffer());
		}
		
		if (getSAMRecords() == 0) {
			// no reads found
			return false;
		}
		
		return true;
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
	public LIBRARY_TYPE getLibraryType() {
		return dataBuilder.getLibraryType();
	}

	@Override
	public void advance() {
		dataBuilder.advance();
	}

	@Override
	public Coordinate getCoordinate() {
		return dataBuilder.getCoordinate();
	}

	@Override
	public void adjustPosition(int position, STRAND strand) {
		dataBuilder.adjustPosition(position, strand);
	}

	@Override
	public int getNextPosition() {
		return dataBuilder.getNextPosition();
	}

	@Override
	public DataBuilder.CACHE_STATUS getCacheStatus() {
		return dataBuilder.getCacheStatus();
	}
}