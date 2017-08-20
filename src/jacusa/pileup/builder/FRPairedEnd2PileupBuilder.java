package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.hasLibraryType.LibraryType;
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
	
	public void processRecord(SAMRecord record) {
		AbstractDataBuilder<T> dataBuilder = null;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				dataBuilder = getReverse();
			} else {
				dataBuilder = getForward();
			}
		} else { // single end
			if (record.getReadNegativeStrandFlag()) {
				dataBuilder = getReverse();
			} else {
				dataBuilder = getForward();
			}
		}

		dataBuilder.processRecord(record);
	}

}