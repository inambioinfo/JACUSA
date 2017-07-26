package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class RTArrestPileupBuilderFactory extends AbstractPileupBuilderFactory {

	final AbstractPileupBuilderFactory pbf;
	
	public RTArrestPileupBuilderFactory(final AbstractPileupBuilderFactory pbf) {
		super(pbf.getLibraryType());
		this.pbf = pbf;
	}

	@Override
	public AbstractPileupBuilder newInstance(
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final SampleParameters sample, 
			final AbstractParameters parameters) {

		return new RTArrestPileupBuilder(pbf.newInstance(windowCoordinates, reader, sample, parameters));
	}

}