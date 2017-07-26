package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.builder.inverted.FRPairedEnd1InvertedPileupBuilder;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class FRPairedEnd1PileupBuilderFactory extends AbstractPileupBuilderFactory {

	public FRPairedEnd1PileupBuilderFactory() {
		super(LibraryType.FR_FIRSTSTRAND);
	}

	@Override
	public AbstractPileupBuilder newInstance(
			WindowCoordinates windowCoordinates, 
			SAMFileReader reader, 
			ConditionParameters condition, 
			AbstractParameters parameters) {
		if (condition.isInvertStrand()) {
			return new FRPairedEnd1InvertedPileupBuilder(windowCoordinates, reader, condition, parameters);
		}
		return new FRPairedEnd1PileupBuilder(windowCoordinates, reader, condition, parameters);
	}

}