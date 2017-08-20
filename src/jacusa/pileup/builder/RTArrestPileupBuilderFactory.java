package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualReadInfoData;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilderFactory<T extends BaseQualReadInfoData> 
extends AbstractDataBuilderFactory<T> {

	final AbstractDataBuilderFactory<T> pbf;
	
	public RTArrestPileupBuilderFactory(final AbstractDataBuilderFactory<T> pbf) {
		super(pbf.getLibraryType());
		this.pbf = pbf;
	}

	@Override
	public DataBuilder<T> newInstance(
			final WindowCoordinates windowCoordinates,
			final SAMFileReader reader, 
			final ConditionParameters<T> condition, 
			final AbstractParameters<T> parameters) {
		return new RTArrestPileupBuilder<T>(pbf.newInstance(windowCoordinates, reader, condition, parameters));
	}

}
