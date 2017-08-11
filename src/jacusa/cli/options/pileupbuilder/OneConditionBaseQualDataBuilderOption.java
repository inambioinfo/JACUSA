package jacusa.cli.options.pileupbuilder;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class OneConditionBaseQualDataBuilderOption<T extends BaseQualData>
extends AbstractDataBuilderOption<T> {

	private final ConditionParameters<T> condition;

	public OneConditionBaseQualDataBuilderOption(
			final ConditionParameters<T> condition) {
		super();
		
		this.condition 	= condition;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("Choose the library type and how parallel pileups are build:\n" + getPossibleValues()+ "\n default: " + LibraryType.UNSTRANDED)
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String s = line.getOptionValue(opt);
	    	LibraryType l = parse(s);
	    	if (l == null) {
	    		throw new IllegalArgumentException("Possible values for " + longOpt.toUpperCase() + ":\n" + getPossibleValues());
	    	}
	    	condition.setPileupBuilderFactory(buildPileupBuilderFactory(l));
	    }
	}

	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(final LibraryType libraryType) {
		
		switch(libraryType) {
		
		case UNSTRANDED:
			return new UnstrandedPileupBuilderFactory<T>();
		
		case FR_FIRSTSTRAND:
			return new FRPairedEnd1PileupBuilderFactory<T>();
		
		case FR_SECONDSTRAND:
			return new FRPairedEnd2PileupBuilderFactory<T>();
		}

		return null;
	}
	
}