package jacusa.cli.options.pileupbuilder;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.method.rtarrest.RTArrestFactory;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasReadInfoCount;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.AbstractPileupBuilderFactory;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

public abstract class AbstractPileupBuilderOption<T extends Data<T> & hasReadInfoCount & hasBaseCount & hasCoordinate & hasRefBase> extends AbstractACOption {
	
	protected static final char SEP = ',';
	private final AbstractParameters<?> parameters;
	
	public AbstractPileupBuilderOption(final AbstractParameters<T> parameters) {
		opt = "P";
		longOpt = "build-pileup";
		this.parameters = parameters;
	}

	protected AbstractPileupBuilderFactory<T> buildPileupBuilderFactory(LibraryType libraryType) {
		AbstractPileupBuilderFactory<T> pbf;
		
		switch(libraryType) {
		case UNSTRANDED:
			pbf = new UnstrandedPileupBuilderFactory<T>();
		
		case FR_FIRSTSTRAND:
			pbf = new FRPairedEnd1PileupBuilderFactory<T>();
		
		case FR_SECONDSTRAND:
			pbf =  new FRPairedEnd2PileupBuilderFactory<T>();
			
		default:
			pbf = null;
		}
		
		if (parameters.getMethodFactory().getName().equals(RTArrestFactory.NAME)) {
			return new RTArrestPileupBuilderFactory<T>(pbf);				
		}
		return pbf;
	}

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