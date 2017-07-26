package jacusa.cli.options.pileupbuilder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class TwoConditionPileupBuilderOption extends AbstractPileupBuilderOption {

	private ConditionParameters condition1;
	private ConditionParameters condition2;
	
	public TwoConditionPileupBuilderOption(AbstractParameters parameters, ConditionParameters condition1, ConditionParameters condition2) {
		super(parameters);
		this.condition1 = condition1;
		this.condition2 = condition2;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
			.withDescription("Choose the library types and how parallel pileups are build for condition1(c1) and condition2(c2).\nFormat: s1,s2. \nPossible values for s1 and s2:\n" + getPossibleValues() + "\ndefault: " + LibraryType.UNSTRANDED + SEP + LibraryType.UNSTRANDED)
			.create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String s = line.getOptionValue(opt);
	    	String[] ss = s.split(Character.toString(SEP));
	    	
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("Format: s1,s2. \n");
	    	sb.append("Possible values for s1 and s2:\n");
	    	sb.append(getPossibleValues());
	    	
	    	if (ss.length != 2) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}
	    	
	    	LibraryType l1 = parse(ss[0]);
	    	LibraryType l2 = parse(ss[1]);
	    	
	    	if (l1 == null || l2 == null) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}
	    	condition1.setPileupBuilderFactory(buildPileupBuilderFactory(l1));
	    	condition2.setPileupBuilderFactory(buildPileupBuilderFactory(l2));
	    }
	}

}