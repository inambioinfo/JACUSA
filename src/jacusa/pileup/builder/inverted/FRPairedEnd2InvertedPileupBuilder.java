package jacusa.pileup.builder.inverted;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.AbstractStrandedPileupBuilder;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class FRPairedEnd2InvertedPileupBuilder<T extends BaseQualData> 
extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd2InvertedPileupBuilder(
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, reader, condition, parameters, LibraryType.FR_SECONDSTRAND);
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
		switchByStrand();
		super.processRecord(record);
	}

}