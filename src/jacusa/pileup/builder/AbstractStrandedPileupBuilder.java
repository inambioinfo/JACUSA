package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

/**
 * @author Michael Piechotta
 *
 */
public abstract class AbstractStrandedPileupBuilder<T extends BaseQualData> 
implements DataBuilder<T> {

	private CoordinateAdvancer advancer;

	private WindowCoordinates windowCoordinates;
	private AbstractParameters<T> parameters;
	private LIBRARY_TYPE libraryType;

	private AbstractDataBuilder<T> forward;
	private AbstractDataBuilder<T> reverse;
	
	public AbstractStrandedPileupBuilder(final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters,
			final LIBRARY_TYPE libraryType) {
		this.windowCoordinates 	= windowCoordinates;
		this.parameters 		= parameters;
		this.libraryType		= libraryType;
		
		forward = new UnstrandedPileupBuilder<T>(windowCoordinates, reader, STRAND.FORWARD, condition, parameters);
		reverse = new UnstrandedPileupBuilder<T>(windowCoordinates, reader, STRAND.REVERSE, condition, parameters);
	}

	@Override
	public int processBuffer(final int SAMReocordsInBuffer, final SAMRecord[] SAMRecordsBuffer) {
		for (int i = 0; i < SAMReocordsInBuffer; ++i) {
			try {
				processRecord(SAMRecordsBuffer[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return 0;
	}
	
	@Override
	public void clearCache() {
		forward.clearCache();
		reverse.clearCache();
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getCoverage(windowPosition, strand);
		} else if(strand == STRAND.REVERSE) {
			return reverse.getCoverage(windowPosition, strand);
		} else {
			return forward.getCoverage(windowPosition, strand) + reverse.getCoverage(windowPosition, strand);
		}
	}

	@Override
	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getFilterContainer(windowPosition, strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getFilterContainer(windowPosition, strand);
		} else {
			return null;
		}
	}

	@Override
	public T getData(int windowPosition, STRAND strand) {
		T dataContainer = parameters.getMethodFactory().createData();

		dataContainer.getCoordinate().setContig(windowCoordinates.getContig()); 
		dataContainer.getCoordinate().setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.getCoordinate().setStrand(strand);

		WindowCache windowCache = getWindowCache(strand);

		// copy base and qual info from cache
		dataContainer.setBaseQualCount(windowCache.getBaseCount(windowPosition));

		byte refBaseByte = windowCache.getReferenceBase(windowPosition);
		if (refBaseByte != (byte)'N') {
			dataContainer.setReferenceBase((char)refBaseByte);
		}

		// and complement if needed
		if (strand == STRAND.REVERSE) {
			dataContainer.getBaseQualCount().invert();
		}
		
		// for "Stranded"PileupBuilder the basesCounts in the pileup are already inverted (when on the reverse strand) 
		return dataContainer;
	}

	@Override
	public WindowCache getWindowCache(STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getWindowCache(strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getWindowCache(strand);
		} else {
			return null;
		}
	}

	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	@Override
	public WindowCoordinates getWindowCoordinates() {
		return windowCoordinates;
	}
	
	@Override
	public SAMRecordIterator getIterator(int genomicPosition) {
		return forward.getIterator(genomicPosition);
	}
	
	protected boolean fillWindow(final int genomicPosition) {
		clearCache();

		// get iterator to fill the window
		SAMRecordIterator iterator = getIterator(genomicPosition);
		final int SAMReocordsInBuffer = processIterator(iterator);

		if (SAMReocordsInBuffer > 0) {
			// process any left SAMrecords in the buffer
			processBuffer(SAMReocordsInBuffer, getSAMRecordsBuffer());
		}

		return getSAMRecords() > 0;
	}
	
	public void adjustPosition(int genomicPosition, STRAND strand) {
		if (! getWindowCoordinates().isContainedInWindow(genomicPosition)) {
			fillWindow(genomicPosition);
		}

		advancer.adjustPosition(genomicPosition, strand);
	}

	@Override
	public int getNextPosition() {
		return advancer.getNextPosition();
	}
	
	@Override
	public SAMRecord[] getSAMRecordsBuffer() {
		return forward.getSAMRecordsBuffer();
	}
	
	@Override
	public int processIterator(SAMRecordIterator iterator) {
		return forward.processIterator(iterator);
	}
	
	@Override
	public void incrementFilteredSAMRecords() {
		forward.incrementFilteredSAMRecords();
	}
	
	@Override
	public void incrementSAMRecords() {
		forward.incrementSAMRecords();
	}
	
	@Override
	public int getSAMRecords() {
		return forward.getSAMRecords() + reverse.getSAMRecords();
	}
	
	@Override
	public int getFilteredSAMRecords() {
		return forward.getFilteredSAMRecords() + reverse.getFilteredSAMRecords();
	}
	
	@Override
	public SAMRecord getNextValidRecord(int targetPosition) {
		// ignore reverse; reference object shared between forward and reverse
		return forward.getNextValidRecord(targetPosition);
	}

	protected AbstractDataBuilder<T> getForward() {
		return forward;
	}
	
	protected AbstractDataBuilder<T> getReverse() {
		return reverse;
	}

	public Coordinate getCoordinate() {
		return advancer.getCoordinate();
	}
	
	@Override
	public void advance() {
		final int position = advancer.getNextPosition();
		if (! windowCoordinates.isContainedInWindow(position)) {
			fillWindow(position);
		} 
		advancer.advance();
	}
	
	@Override
	public DataBuilder.CACHE_STATUS getCacheStatus() {
		return forward.getCacheStatus();
	}
	
}