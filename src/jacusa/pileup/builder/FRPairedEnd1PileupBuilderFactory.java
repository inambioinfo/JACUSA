package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.inverted.FRPairedEnd1InvertedPileupBuilder;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public class FRPairedEnd1PileupBuilderFactory<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractPileupBuilderFactory<T> {

	public FRPairedEnd1PileupBuilderFactory() {
		super(LibraryType.FR_FIRSTSTRAND);
	}

	@Override
	public AbstractPileupBuilder<T> newInstance(
			final T dataContainer,
			WindowCoordinates windowCoordinates, 
			SAMFileReader reader, 
			ConditionParameters condition, 
			AbstractParameters<T> parameters) {
		if (condition.isInvertStrand()) {
			return new FRPairedEnd1InvertedPileupBuilder<T>(dataContainer, windowCoordinates, reader, condition, parameters);
		}
		return new FRPairedEnd1PileupBuilder<T>(dataContainer, windowCoordinates, reader, condition, parameters);
	}

	@Override
	public T getDataContainer() {
		// TODO Auto-generated method stub
		return null;
	}
	
}