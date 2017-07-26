package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
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
			SampleParameters sample, 
			AbstractParameters parameters) {
		if (sample.isInvertStrand()) {
			return new FRPairedEnd1InvertedPileupBuilder(windowCoordinates, reader, sample, parameters);
		}
		return new FRPairedEnd1PileupBuilder(windowCoordinates, reader, sample, parameters);
	}

}