package jacusa.filter.storage;

import jacusa.pileup.builder.WindowCache;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;

public abstract class AbstractFilterStorage {

	// corresponds to CLI option 
	private char c; 
	private WindowCache container;

	public AbstractFilterStorage(final char c) {
		this.c = c;
	}
	
	protected void setContainer(WindowCache container) {
		this.container = container;
	}

	public WindowCache getContainer() {
		return container;
	}

	public final char getC() {
		return c;
	}

	public void processRecord(
			int genomicWindowStart, 
			SAMRecord record) {
		// override if needed	
	}

	public int getDistance() {
		return 0;
	}

	public void processAlignmentBlock(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			CigarElement cigarElement, 
			SAMRecord record) {
		// override if needed
	}

	public void processInsertion(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		// override if needed
	}

	public void processAlignmentMatch(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			final CigarElement cigarElement,
			final SAMRecord record,
			final int baseI,
			final int qual) {
		// override if needed
	}

	/*
	public void processHardClipping(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			final CigarElement cigarElement, 
			final SAMRecord record) {
		System.err.println("Hard Clipping not handled yet!");
	}
	*/

	public void processDeletion(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		// override if needed
	}
	
	public void processSkipped(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		// override if needed
	}
	
	/*
	public void processSoftClipping(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			final CigarElement cigarElement, 
			final SAMRecord record) {
		// override if needed
	}
	*/

	/*
	public void processPadding(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		System.err.println("Padding not handled yet!");
	}
	*/

	public void clearContainer() {
		// override if needed
	}

}