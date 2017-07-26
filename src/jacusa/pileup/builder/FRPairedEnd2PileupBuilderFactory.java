package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.builder.inverted.FRPairedEnd2InvertedPileupBuilder;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class FRPairedEnd2PileupBuilderFactory extends AbstractPileupBuilderFactory {
	
	public FRPairedEnd2PileupBuilderFactory() {
		super(LibraryType.FR_SECONDSTRAND);
	}

	@Override
	public AbstractStrandedPileupBuilder newInstance(
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters condition,
			final AbstractParameters parameters) {
		if (condition.isInvertStrand()) {
			return new FRPairedEnd2InvertedPileupBuilder(windowCoordinates, reader, condition, parameters);
		}
		
		return new FRPairedEnd2PileupBuilder(windowCoordinates, reader, condition, parameters);
	}

}