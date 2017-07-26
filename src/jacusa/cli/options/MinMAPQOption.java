package jacusa.cli.options;

import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinMAPQOption extends AbstractACOption {

	private ConditionParameters[] conditions;
	
	public MinMAPQOption(final ConditionParameters[] conditions) {
		this.conditions = conditions;

		opt = "m";
		longOpt = "min-mapq";
	}


	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("filter positions with MAPQ < " + longOpt.toUpperCase() + "\n default: " + conditions[0].getMinMAPQ())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	int minMapq = Integer.parseInt(value);
	    	if(minMapq < 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " = " + minMapq + " not valid.");
	    	}
	    	for (ConditionParameters condition : conditions) {
		    	condition.setMinMAPQ(minMapq);
	    	}
	    }
	}

}