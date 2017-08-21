package jacusa.pileup.builder;

import jacusa.cli.options.condition.filter.samtag.SamTagFilter;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.data.BaseConfig;
import jacusa.filter.FilterContainer;
import jacusa.filter.storage.AbstractFilterStorage;
import jacusa.util.WindowCoordinates;
import jacusa.util.Coordinate.STRAND;

import java.util.Arrays;
import java.util.List;

import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMValidationError;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractDataBuilder<T extends AbstractData>
implements DataBuilder<T>, hasLibraryType {

	// in genomic coordinates
	protected WindowCoordinates windowCoordinates;

	protected SAMRecord[] SAMRecordsBuffer;
	protected SAMFileReader reader;

	protected int filteredSAMRecords;

	protected BaseConfig baseConfig;
	protected ConditionParameters<T> condition;
	protected AbstractParameters<T> parameters;

	protected WindowCache windowCache;
	
	protected FilterContainer<T> filterContainer;
	protected int[] byte2int;
	protected STRAND strand;

	protected int distance;
	
	protected LibraryType libraryType;
	
	public AbstractDataBuilder (
			final WindowCoordinates windowCoordinates,
			final SAMFileReader SAMFileReader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters,
			final STRAND strand,
			final LibraryType libraryType) {
		this.windowCoordinates	= windowCoordinates;
		
		SAMRecordsBuffer		= new SAMRecord[20000];
		reader					= SAMFileReader;

		filteredSAMRecords		= 0;

		baseConfig				= parameters.getBaseConfig();
		this.condition			= condition;
		this.parameters			= parameters;

		windowCache				= new WindowCache(windowCoordinates, baseConfig.getBases().length);
		filterContainer			= parameters.getFilterConfig().createFilterContainer(windowCoordinates, strand, condition);
		byte2int 				= parameters.getBaseConfig().getbyte2int();

		this.libraryType		= libraryType;
		
		// get max overhang
		for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.M)) {
			distance = Math.max(filter.getDistance(), distance);
		}
	}

	/**
	 * 
	 * @param targetPosition
	 * @return
	 */
	@Override
	public SAMRecord getNextValidRecord(int targetPosition) {
		SAMRecordIterator iterator = reader.query(
				windowCoordinates.getContig(), 
				targetPosition, 
				windowCoordinates.getMaxGenomicPosition(), 
				false);
		
		while (iterator.hasNext() ) {
			SAMRecord record = iterator.next();

			if (isValid(record)) {
				iterator.close();
				iterator = null;
				return record;
			}
		}
		iterator.close();

		// if no more reads are found 
		return null;
	}
	
	/**
	 * Tries to adjust to target position
	 * Return true if at least one valid SAMRecord could be found.
	 * WARNING: currentGenomicPosition != targetPosition is possible after method call 
	 * @param genomicWindowStart
	 * @return
	 */
	@Override
	public boolean adjustWindowStart(int genomicWindowStart) {
		clearCache();
		windowCoordinates.setGenomicWindowStart(genomicWindowStart);
		
		// get iterator to fill the window
		SAMRecordIterator iterator = reader.query(
				windowCoordinates.getContig(), 
				windowCoordinates.getGenomicWindowStart(), 
				windowCoordinates.getGenomicWindowEnd(), 
				false);

		// true if a valid read is found within genomicWindowStart and genomicWindowStart + windowSize
		boolean windowHit = false;
		int SAMReocordsInBuffer = 0;

		while (iterator.hasNext()) {
			SAMRecord record = iterator.next();

			if(isValid(record)) {
				SAMRecordsBuffer[SAMReocordsInBuffer++] = record;
			} else {
				filteredSAMRecords++;
			}

			// process buffer
			if (SAMReocordsInBuffer >= SAMRecordsBuffer.length) {
				for (SAMRecord bufferedRecord : SAMRecordsBuffer) {
					try {
						processRecord(bufferedRecord);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// reset counter
				SAMReocordsInBuffer = 0;
				// we found at least a valid SAMRecord
				windowHit = true;
			}
		}
		iterator.close();

		if (! windowHit && SAMReocordsInBuffer == 0) {
			// no reads found
			return false;
		} else { // process any left SAMrecords in the buffer
			for (int i = 0; i < SAMReocordsInBuffer; ++i) {
				try {
					processRecord(SAMRecordsBuffer[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}
	}

	/**
	 * Checks if a record fulfills user defined criteria
	 * @param samRecord
	 * @return
	 */
	protected boolean isValid(SAMRecord samRecord) {
		int mapq = samRecord.getMappingQuality();
		List<SAMValidationError> errors = samRecord.isValid();

		if (! samRecord.getReadUnmappedFlag()
				&& ! samRecord.getNotPrimaryAlignmentFlag() // ignore non-primary alignments CHECK
				&& (mapq < 0 || mapq >= condition.getMinMAPQ()) // filter by mapping quality
				&& (condition.getFilterFlags() == 0 || (condition.getFilterFlags() > 0 && ((samRecord.getFlags() & condition.getFilterFlags()) == 0)))
				&& (condition.getRetainFlags() == 0 || (condition.getRetainFlags() > 0 && ((samRecord.getFlags() & condition.getRetainFlags()) > 0)))
				&& errors == null // isValid is expensive
				) { // only store valid records that contain mapped reads
			// custom filter 
			for (SamTagFilter samTagFilter : condition.getSamTagFilters()) {
				if (samTagFilter.filter(samRecord)) {
					return false;
				}
			}

			// no errors found
			return true;
		}

		// print error messages
		if (errors != null) {
			for (SAMValidationError error : errors) {
				 System.err.println(error.toString());
			}
		}

		// something went wrong
		return false;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public int getFilteredSAMRecords() {
		return filteredSAMRecords;
	}

	@Override
	public WindowCoordinates getWindowCoordinates() {
		return windowCoordinates;
	}

	// abstract methods

	// Reset all caches in windows
	@Override
	public void clearCache() {
		windowCache.clear();
		filterContainer.clear();
	}
	
	protected abstract void addHighQualityBaseCall(final int windowPosition, 
			final int baseIndex, final int qualIndex, final STRAND strand);
	protected abstract void addLowQualityBaseCall(final int windowPosition, 
			final int baseIndex, final int qualIndex, final STRAND strand);

	/*
	 * process CIGAR string methods
	 */
	
	protected void processHardClipping(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			final CigarElement cigarElement, 
			final SAMRecord record) {
		// System.err.println("Hard Clipping not handled yet!");
	}
	
	protected void processSoftClipping(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			final CigarElement cigarElement, 
			final SAMRecord record) {
		// override if needed
	}

	protected void processPadding(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		System.err.println("Padding not handled yet!");
	}

	protected byte[] parseMDField(final SAMRecord record) {
		String tag = "MD";
		Object o = record.getAttribute(tag);
		if (o == null) {
			return new byte[0]; // no MD field :-(
		}

		// init container size with read length
		final byte[] referenceBases = new byte[record.getReadLength()];
		int destPos = 0;
		// copy read sequence to reference container / concatenate mapped segements ignor DELs
		for (int i = 0; i < record.getAlignmentBlocks().size(); i++) {
			if (referenceBases != null) {
				final int srcPos = record.getAlignmentBlocks().get(i).getReadStart() - 1;
				final int length = record.getAlignmentBlocks().get(i).getLength();
				System.arraycopy(
						record.getReadBases(), 
						srcPos, 
						referenceBases, 
						destPos, 
						length);
				destPos += length;
			}
		}

		// get MD string
		String MD = (String)o;
		// add potential missing number(s)
		MD = "0" + MD.toUpperCase();

		int position = 0;
		boolean nextInteger = true;
		// change to reference base based on MD string
//		int j = 0;
		for (String e : MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))")) {
			if (nextInteger) { // match
				// use read sequence
				int matchLength = Integer.parseInt(e);
				position += matchLength;
				nextInteger = false;	
			} else if (e.charAt(0) == '^') {
				// ignore deletions from reference
				nextInteger = true;
			} else { // mismatch
//				try {
				referenceBases[position] = (byte)e.toCharArray()[0];
//				} catch (ArrayIndexOutOfBoundsException e2) {
//					String[] tmp = MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))");
//					System.out.println(e2.toString());
//				}

				position += 1;
				nextInteger = true;
			}
//			++j;
		}
		// resize container if MD < read length
		if (position < referenceBases.length) {
			Arrays.copyOf(referenceBases, position);
		}

		return referenceBases;
	}
	
	public void processRecord(SAMRecord record) {
		// init	
		int readPosition 	= 0;
		int genomicPosition = record.getAlignmentStart();
		int windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
		int alignmentBlockI = 0;

		int MDPosition = 0;
		byte[] referenceBases = null;

		// collect alignment length of blocks
		int alignmentBlockLength[] = new int[record.getAlignmentBlocks().size() + 2];
		alignmentBlockLength[0] = 0;
		
		for (int i = 0; i < record.getAlignmentBlocks().size(); i++) {
			alignmentBlockLength[i + 1] = record.getAlignmentBlocks().get(i).getLength();
		}
		alignmentBlockLength[record.getAlignmentBlocks().size() + 1] = 0;

		// process record specific filters
		for (AbstractFilterStorage filter : filterContainer.getPR()) {
			filter.processRecord(windowCoordinates.getGenomicWindowStart(), record);
		}
		
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			switch(cigarElement.getOperator()) {

			/*
			 * handle insertion
			 */
			case I:
				processInsertion(
						windowPosition, 
						readPosition, 
						genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1], 
						cigarElement, 
						record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * handle alignment/sequence match and mismatch
			 */
			case M:
			case EQ:
			case X:
				processAlignmentMatch(windowPosition, readPosition, genomicPosition, cigarElement, record, MDPosition, referenceBases);
				readPosition += cigarElement.getLength();
				genomicPosition += cigarElement.getLength();
				MDPosition += cigarElement.getLength();
				windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
				alignmentBlockI++;
				break;

			/*
			 * handle hard clipping 
			 */
			case H:
				processHardClipping(windowPosition, readPosition, genomicPosition, cigarElement, record);
				break;

			/*
			 * handle deletion from the reference and introns
			 */
			case D:
				processDeletion(
						windowPosition, 
						readPosition, 
						genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				genomicPosition += cigarElement.getLength();
				windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
				break;

			case N:
				processSkipped(
						windowPosition, 
						readPosition, 
						genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				genomicPosition += cigarElement.getLength();
				windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
				break;

			/*
			 * soft clipping
			 */
			case S:
				processSoftClipping(windowPosition, readPosition, genomicPosition, cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * silent deletion from padded sequence
			 */
			case P:
				processPadding(
						windowPosition, 
						readPosition, 
						genomicPosition,
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, 
						record);
				break;

			default:
				throw new RuntimeException("Unsupported Cigar Operator: " + cigarElement.getOperator().toString());
			}
		}
	}

	protected void processAlignmentMatch(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			final CigarElement cigarElement, 
			final SAMRecord record,
			final int MDPosition, 
			byte[] referenceBases) {
		// process alignmentBlock specific filters

		for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.M)) {
			filter.processAlignmentBlock(windowPosition, readPosition, genomicPosition, cigarElement, record);
		}
		
		for (int offset = 0; offset < cigarElement.getLength(); ++offset) {
			final int baseIndex = byte2int[record.getReadBases()[readPosition + offset]];
			int qualIndex = record.getBaseQualities()[readPosition + offset];

			if (baseIndex == -1) {
				windowPosition = windowCoordinates.convert2WindowPosition(genomicPosition + offset);
				int orientation = windowCoordinates.getOrientation(genomicPosition + offset);
				
				// process MD on demand
				if (record.getAttribute("MD") != null && orientation == 0 && windowCache.getReferenceBase(windowPosition) == (byte)'N') {
					if (referenceBases == null) {
						referenceBases = parseMDField(record);
					}
					if (referenceBases.length > 0) {
						windowCache.addReferenceBase(windowPosition, referenceBases[MDPosition + offset]);
					}
				}

				continue;
			}

			// speedup: if orientation == 1 the remaining part of the read will be outside of the windowCache
			// ignore the overhanging part of the read until it overlaps with the window cache
			windowPosition = windowCoordinates.convert2WindowPosition(genomicPosition + offset);
			int orientation = windowCoordinates.getOrientation(genomicPosition + offset);
			
			switch (orientation) {
			case 1:
				if ((genomicPosition + offset) - windowCoordinates.getGenomicWindowEnd() <= distance) {
					if (qualIndex >= condition.getMinBASQ()) {
						for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.M)) {
							filter.processAlignmentMatch(windowPosition, readPosition + offset, genomicPosition + offset, cigarElement, record, baseIndex, qualIndex);
						}
					}
				} else {
					return;
				}
				break;
			case -1: // speedup jump to covered position
				if (windowCoordinates.getGenomicWindowStart() - (genomicPosition + offset) > distance) {
					offset += windowCoordinates.getGenomicWindowStart() - (genomicPosition + offset) - distance - 1;
				} else {
					if (qualIndex >= condition.getMinBASQ()) {
						for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.M)) {
							filter.processAlignmentMatch(windowPosition, readPosition + offset, genomicPosition + offset, cigarElement, record, baseIndex, qualIndex);
						}
					}
				}
				break;
			case 0:
				if (windowPosition >= 0) {
					if (qualIndex >= condition.getMinBASQ()) {
						addHighQualityBaseCall(windowPosition, baseIndex, qualIndex, strand);

						// process any alignmentMatch specific filters
						for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.M)) {
							filter.processAlignmentMatch(windowPosition, readPosition + offset, genomicPosition + offset, cigarElement, record, baseIndex, qualIndex);
						}
					} else if (parameters.collectLowQualityBaseCalls()) { 
						addLowQualityBaseCall(windowPosition, baseIndex, qualIndex, strand);
					}
					// process MD on demand
					if (record.getAttribute("MD") != null && windowCache.getReferenceBase(windowPosition) == (byte)'N') {
						if (referenceBases == null) {
							referenceBases = parseMDField(record);
						}
						if (referenceBases.length > 0) {
							windowCache.addReferenceBase(windowPosition, referenceBases[MDPosition + offset]);
						}
					}
				}
				break;
			}
		}
	}

	protected void processInsertion(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.I)) {
			filter.processInsertion(
					windowPosition, 
					readPosition, 
					genomicPosition, 
					upstreamMatch, 
					downstreamMatch, 
					cigarElement, 
					record);
		}
	}

	protected void processDeletion(
			int windowPosition, 
			int readPosition, 
			int genomicPosition, 
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.D)) {
			filter.processDeletion(
					windowPosition, 
					readPosition, 
					genomicPosition, 
					upstreamMatch,
					downstreamMatch,
					cigarElement, 
					record);
		}
	}

	protected void processSkipped(
			int windowPosition, 
			int readPosition, 
			int genomicPosition,
			int upstreamMatch,
			int downstreamMatch,
			final CigarElement cigarElement, 
			final SAMRecord record) {
		for (AbstractFilterStorage filter : filterContainer.get(CigarOperator.N)) {
			filter.processSkipped(
					windowPosition, 
					readPosition, 
					genomicPosition,
					upstreamMatch,
					downstreamMatch,
					cigarElement, 
					record);
		}
	}

	@Override
	public LibraryType getLibraryType() {
		return libraryType;
	}
	
}