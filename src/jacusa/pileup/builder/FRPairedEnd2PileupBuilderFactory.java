package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
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
			final SampleParameters sample,
			final AbstractParameters parameters) {
		if (sample.isInvertStrand()) {
			return new FRPairedEnd2InvertedPileupBuilder(windowCoordinates, reader, sample, parameters);
		}
		
		return new FRPairedEnd2PileupBuilder(windowCoordinates, reader, sample, parameters);
	}

}