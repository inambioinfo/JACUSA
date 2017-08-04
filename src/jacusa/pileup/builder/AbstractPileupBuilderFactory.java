package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public abstract class AbstractPileupBuilderFactory<T extends Data<T> & hasBaseCount & hasRefBase & hasCoordinate> implements hasLibraryType {

	final private LibraryType libraryType;
	
	public AbstractPileupBuilderFactory(final LibraryType libraryType) {
		this.libraryType = libraryType;
	}
	
	public abstract AbstractPileupBuilder<T> newInstance(
			final T dataContainer,
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters condition,
			final AbstractParameters<T> parameters);

	final public boolean isStranded() {
		if (libraryType == LibraryType.UNSTRANDED) {
			return false;
		}
		
		return true;
	}
	
	public abstract T getDataContainer();
	
	final public LibraryType getLibraryType() {
		return libraryType;
	}
	
}