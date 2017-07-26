package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class UnstrandedPileupBuilderFactory extends AbstractPileupBuilderFactory {

	public UnstrandedPileupBuilderFactory() {
		super(LibraryType.UNSTRANDED);
	}

	@Override
	public AbstractPileupBuilder newInstance(
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final SampleParameters sample, 
			final AbstractParameters parameters) {
		return new UnstrandedPileupBuilder(windowCoordinates, reader, sample, parameters);
	}
	
}