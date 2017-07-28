package jacusa.cli.options.pileupbuilder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class OneConditionPileupBuilderOption extends AbstractPileupBuilderOption {

	private ConditionParameters condition;
	
	public OneConditionPileupBuilderOption(AbstractParameters parameters, ConditionParameters condition) {
		super(parameters);
		
		this.condition = condition;
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

}