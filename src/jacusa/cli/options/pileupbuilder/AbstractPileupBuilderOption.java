package jacusa.cli.options.pileupbuilder;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.method.rtarrest.RTArrestFactory;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.PileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedRTArrestPileupBuilderFactory;

public abstract class AbstractPileupBuilderOption extends AbstractACOption {
	
	protected static final char SEP = ',';
	private final AbstractParameters parameters;
	
	public AbstractPileupBuilderOption(final AbstractParameters parameters) {
		opt = "P";
		longOpt = "build-pileup";
		this.parameters = parameters;
	}

	protected PileupBuilderFactory buildPileupBuilderFactory(LibraryType libraryType) {
		switch(libraryType) {
		case UNSTRANDED:
			if (parameters.getMethodFactory().getName().equals(RTArrestFactory.NAME)) {
				return new UnstrandedRTArrestPileupBuilderFactory();				
			}
			return new UnstrandedPileupBuilderFactory();
		
		case FR_FIRSTSTRAND:
			return new FRPairedEnd1PileupBuilderFactory();
		
		case FR_SECONDSTRAND:
			return new FRPairedEnd2PileupBuilderFactory();
			
		default:
			return null;
		}
	}

	public LibraryType parse(String s) {
		s = s.toUpperCase();

		// for compatibility with older versions 
		/* SE stranded now - flips the strand
		if (s.length() == 1) {
			switch(s.charAt(0)) {
			case 'S':
				return LibraryType.SE_STRANDED;
				
			case 'U':
				return LibraryType.UNSTRANDED;
			}	
		}
		*/

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
		StringBuilder sb = new StringBuilder();
		
		for (LibraryType l : LibraryType.values()) {
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

	protected enum LibraryType {
		FR_FIRSTSTRAND, 
		FR_SECONDSTRAND,
		UNSTRANDED
	}
	
}