package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.inverted.FRPairedEnd2InvertedPileupBuilder;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class FRPairedEnd2PileupBuilderFactory<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractPileupBuilderFactory<T> {
	
	public FRPairedEnd2PileupBuilderFactory() {
		super(LibraryType.FR_SECONDSTRAND);
	}

	@Override
	public AbstractStrandedPileupBuilder<T> newInstance(
			final T dataContainer,
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters condition,
			final AbstractParameters<T> parameters) {
		if (condition.isInvertStrand()) {
			return new FRPairedEnd2InvertedPileupBuilder<T>(dataContainer, windowCoordinates, reader, condition, parameters);
		}
		
		return new FRPairedEnd2PileupBuilder<T>(dataContainer, windowCoordinates, reader, condition, parameters);
	}

	@Override
	public T getDataContainer() {
		// TODO Auto-generated method stub
		return null;
	}
}