package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class FRPairedEnd2PileupBuilder<T extends BaseQualData> 
extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd2PileupBuilder(
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, reader, condition, parameters, LibraryType.FR_SECONDSTRAND);
	}
	
	protected void processRecord(SAMRecord record) {
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				strand = STRAND.REVERSE;
			} else {
				strand = STRAND.FORWARD;
			}
		} else { // single end
			if (record.getReadNegativeStrandFlag()) {
				strand = STRAND.REVERSE;
			} else {
				strand = STRAND.FORWARD;
			}
		}

		switchByStrand();

		super.processRecord(record);
	}

}