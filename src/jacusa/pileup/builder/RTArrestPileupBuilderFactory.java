package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasReadInfoCount;
import jacusa.pileup.hasRefBase;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class RTArrestPileupBuilderFactory<T extends Data<T> & hasCoordinate & hasReadInfoCount & hasRefBase & hasBaseCount> extends AbstractPileupBuilderFactory<T> {

	final AbstractPileupBuilderFactory<T> pbf;
	
	public RTArrestPileupBuilderFactory(final AbstractPileupBuilderFactory<T> pbf) {
		super(pbf.getLibraryType());
		this.pbf = pbf;
	}

	@Override
	public AbstractPileupBuilder<T> newInstance(
			final T baseReadPileup, 
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters condition, 
			final AbstractParameters<T> parameters) {

		return new RTArrestPileupBuilder<T>(baseReadPileup, 
				pbf.newInstance(baseReadPileup, windowCoordinates, reader, condition, parameters));
	}

	@Override
	public T getDataContainer() {
		// TODO Auto-generated method stub
		return null;
	}
}