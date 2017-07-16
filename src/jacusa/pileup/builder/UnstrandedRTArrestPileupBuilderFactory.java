package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class UnstrandedRTArrestPileupBuilderFactory implements PileupBuilderFactory {

	public UnstrandedRTArrestPileupBuilderFactory() {
		// nothing to be done
	}

	@Override
	public AbstractPileupBuilder newInstance(
			final Coordinate coordinate,
			final SAMFileReader reader, 
			final SampleParameters sample, 
			final AbstractParameters parameters) {
		return new UnstrandedRTArrestPileupBuilder(coordinate, reader, sample, parameters);
	}

	@Override
	public boolean isStranded() {
		return false;
	}

}