package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractDataBuilderFactory<T extends AbstractData>
implements hasLibraryType {

	final private LibraryType libraryType;
	
	public AbstractDataBuilderFactory(final LibraryType libraryType) {
		this.libraryType = libraryType;
	}
	
	/**
	 * 
	 * @param windowCoordinates
	 * @param reader
	 * @param condition
	 * @param parameters
	 * @return
	 */
	public abstract DataBuilder<T> newInstance(
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters);

	/**
	 * 
	 * @return
	 */
	final public boolean isStranded() {
		return libraryType == LibraryType.UNSTRANDED;
	}

	@Override
	final public LibraryType getLibraryType() {
		return libraryType;
	}
	
}
