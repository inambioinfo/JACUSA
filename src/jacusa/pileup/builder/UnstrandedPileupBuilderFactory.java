package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class UnstrandedPileupBuilderFactory<T extends Data<T> & hasBaseCount & hasRefBase & hasCoordinate> extends AbstractPileupBuilderFactory<T> {

	public UnstrandedPileupBuilderFactory() {
		super(LibraryType.UNSTRANDED);
	}

	@Override
	public AbstractPileupBuilder<T> newInstance(
			final T dataContainer,
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters condition, 
			final AbstractParameters<T> parameters) {
		
		return new UnstrandedPileupBuilder<T>(dataContainer, windowCoordinates, reader, condition, parameters);
	}
	
	@Override
	public T getDataContainer() {
		// TODO Auto-generated method stub
		return null;
	}
	
}