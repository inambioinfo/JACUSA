package jacusa.cli.options.pileupbuilder;

import jacusa.cli.options.AbstractACOption;
import jacusa.data.AbstractData;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

public abstract class AbstractDataBuilderOption<T extends AbstractData>
extends AbstractACOption {
	
	protected static final char SEP = ',';

	public AbstractDataBuilderOption() {
		opt = "P";
		longOpt = "build-pileup";
	}

	protected abstract AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final LibraryType libraryType);

	public LibraryType parse(String s) {
		s = s.toUpperCase();
		s = s.replace("-", "_");
		
		switch(LibraryType.valueOf(s)) {

		case UNSTRANDED:
			return LibraryType.UNSTRANDED;
			
		case FR_FIRSTSTRAND:
			return LibraryType.FR_FIRSTSTRAND;
		
		case FR_SECONDSTRAND:
			return LibraryType.FR_SECONDSTRAND;
		}

		return null;
	}
	
	public String getPossibleValues() {
		final StringBuilder sb = new StringBuilder();
		
		for (final LibraryType l : LibraryType.values()) {
			String option = l.toString();
			option = option.replace("_", "-");
			String desc = "";

			switch (l) {
			case FR_FIRSTSTRAND:
				desc = "STRANDED library - first strand sequenced";
				break;
				
			case FR_SECONDSTRAND:
				desc = "STRANDED library - second strand sequenced";
				break;

			case UNSTRANDED:
				desc = "UNSTRANDED library";
				break;

			}

			sb.append(option);
			sb.append("\t\t");
			sb.append(desc);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
}