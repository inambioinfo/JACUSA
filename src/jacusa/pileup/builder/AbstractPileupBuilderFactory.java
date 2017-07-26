package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
import jacusa.util.WindowCoordinates;
import net.sf.samtools.SAMFileReader;

public abstract class AbstractPileupBuilderFactory implements hasLibraryType {

	final private LibraryType libraryType;
	
	public AbstractPileupBuilderFactory(final LibraryType libraryType) {
		this.libraryType = libraryType;
	}
	
	public abstract AbstractPileupBuilder newInstance(
			final WindowCoordinates windowCoordinates, 
			final SAMFileReader reader, 
			final SampleParameters sample,
			final AbstractParameters parameters);

	final public boolean isStranded() {
		if (libraryType == LibraryType.UNSTRANDED) {
			return false;
		}
		
		return true;
	}
	
	final public LibraryType getLibraryType() {
		return libraryType;
	}
	
}