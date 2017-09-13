package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LibraryType;
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

	private WindowCoordinates windowCoordinates;
	private AbstractParameters<T> parameters;
	private ConditionParameters<T> condition;
	private LibraryType libraryType;

	private AbstractDataBuilder<T> forward;
	private AbstractDataBuilder<T> reverse;
	
	public AbstractStrandedPileupBuilder(final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters,
			final LibraryType libraryType) {
		this.windowCoordinates 	= windowCoordinates;
		this.parameters 		= parameters;
		this.condition 			= condition;
		this.libraryType		= libraryType;
		
		forward = new UnstrandedPileupBuilder<T>(windowCoordinates, reader, STRAND.FORWARD, condition, parameters);
		reverse = new UnstrandedPileupBuilder<T>(windowCoordinates, reader, STRAND.REVERSE, condition, parameters);
	}

	@Override
	public int processBuffer(int SAMReocordsInBuffer, SAMRecord[] SAMRecordsBuffer) {
		return forward.processBuffer(SAMReocordsInBuffer, SAMRecordsBuffer);
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
		
		dataContainer.getCoordinate().setSequenceName(windowCoordinates.getContig()); 
		dataContainer.getCoordinate().setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.getCoordinate().setStrand(strand);

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
		if (strand == STRAND.FORWARD) {
			return forward.getWindowCache(strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getWindowCache(strand);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean isCovered(int windowPosition, STRAND strand) {
		return getCoverage(windowPosition, strand) >= condition.getMinCoverage();
	}

	@Override
	public LibraryType getLibraryType() {
		return libraryType;
	}
	
	@Override
	public WindowCoordinates getWindowCoordinates() {
		return windowCoordinates;
	}
	
	@Override
	public SAMRecordIterator getIterator(int genomicWindowStart) {
		return forward.getIterator(genomicWindowStart);
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
	public boolean isValid(SAMRecord record) {
		return forward.isValid(record);
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
	
	@Override
	public boolean adjustWindowStart(int genomicWindowStart) {
		return forward.adjustWindowStart(genomicWindowStart);
	}

	protected AbstractDataBuilder<T> getForward() {
		return forward;
	}
	
	protected AbstractDataBuilder<T> getReverse() {
		return reverse;
	}

}