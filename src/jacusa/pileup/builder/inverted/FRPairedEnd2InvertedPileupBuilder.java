package jacusa.pileup.builder.inverted;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.AbstractStrandedPileupBuilder;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class FRPairedEnd2InvertedPileupBuilder<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd2InvertedPileupBuilder(
			final T dataContainer,
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters condition,
			final AbstractParameters<T> parameters) {
		super(dataContainer, windowCoordinates, reader, condition, parameters, LibraryType.FR_SECONDSTRAND);
	}

	// invert
	protected void processRecord(SAMRecord record) {
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				//strand = STRAND.REVERSE;
				strand = STRAND.FORWARD;
			} else {
				//strand = STRAND.FORWARD;
				strand = STRAND.REVERSE;
			}
		} else {
			if (record.getReadNegativeStrandFlag()) {
				strand = STRAND.FORWARD;
			} else {
				//strand = STRAND.FORWARD;
				strand = STRAND.REVERSE;
			}
		}
		int i = strand.integer() - 1;

		byte2int = byte2intAr[i]; 
		filterContainer = filterContainers[i];
		windowCache = windowCaches[i];

		super.processRecord(record);
	}

}