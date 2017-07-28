package jacusa.pileup.builder;

public interface hasLibraryType {

	public LibraryType getLibraryType();
	
	public enum LibraryType {
		FR_FIRSTSTRAND, 
		FR_SECONDSTRAND,
		UNSTRANDED
	}

}
