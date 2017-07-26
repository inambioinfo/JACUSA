package jacusa.cli.options.pileupbuilder;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.method.rtarrest.RTArrestFactory;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.AbstractPileupBuilderFactory;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

public abstract class AbstractPileupBuilderOption extends AbstractACOption {
	
	protected static final char SEP = ',';
	private final AbstractParameters parameters;
	
	public AbstractPileupBuilderOption(final AbstractParameters parameters) {
		opt = "P";
		longOpt = "build-pileup";
		this.parameters = parameters;
	}

	protected AbstractPileupBuilderFactory buildPileupBuilderFactory(LibraryType libraryType) {
		AbstractPileupBuilderFactory pbf;
		
		switch(libraryType) {
		case UNSTRANDED:
			pbf = new UnstrandedPileupBuilderFactory();
		
		case FR_FIRSTSTRAND:
			pbf = new FRPairedEnd1PileupBuilderFactory();
		
		case FR_SECONDSTRAND:
			pbf =  new FRPairedEnd2PileupBuilderFactory();
			
		default:
			pbf = null;
		}
		
		if (parameters.getMethodFactory().getName().equals(RTArrestFactory.NAME)) {
			return new RTArrestPileupBuilderFactory(pbf);				
		}
		return pbf;
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
	
}