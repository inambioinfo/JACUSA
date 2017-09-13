package jacusa.pileup.builder;

import jacusa.data.AbstractData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LibraryType;
import jacusa.util.WindowCoordinates;
import jacusa.util.Coordinate.STRAND;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

public interface DataBuilder<T extends AbstractData> {

	SAMRecord getNextValidRecord(final int targetPosition);

	boolean adjustWindowStart(final int genomicWindowStart);
	SAMRecordIterator getIterator(final int genomicWindowStart);
	int processIterator(final SAMRecordIterator iterator);
	
	SAMRecord[] getSAMRecordsBuffer();
	int processBuffer(final int SAMReocordsInBuffer, final SAMRecord[] SAMRecordsBuffer);
	void processRecord(final SAMRecord record);
	
	boolean isValid(SAMRecord record);
	
	int getFilteredSAMRecords();
	int getSAMRecords();
	void incrementFilteredSAMRecords();
	void incrementSAMRecords();

	WindowCoordinates getWindowCoordinates();

	void clearCache();
	
	// strand dependent methods
	boolean isCovered(final int windowPosition, final STRAND strand);
	int getCoverage(final int windowPosition, final STRAND strand);

	T getData(final int windowPosition, final STRAND strand);
	WindowCache getWindowCache(final STRAND strand);

	FilterContainer<T> getFilterContainer(final int windowPosition, final STRAND strand); 

	LibraryType getLibraryType();

}
