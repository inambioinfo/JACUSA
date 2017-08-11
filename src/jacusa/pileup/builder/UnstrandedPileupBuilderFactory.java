package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;

import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class UnstrandedPileupBuilderFactory<T extends BaseQualData> 
extends AbstractDataBuilderFactory<T> {

	public UnstrandedPileupBuilderFactory() {
		super(LibraryType.UNSTRANDED);
	}

	@Override
	public UnstrandedPileupBuilder<T> newInstance(
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters<T> condition, 
			final AbstractParameters<T> parameters) {
		return new UnstrandedPileupBuilder<T>(windowCoordinates, reader, 
				condition, parameters);
	}

}
