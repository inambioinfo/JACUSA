package jacusa.cli.options;

import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinBASQOption extends AbstractACOption {

	private ConditionParameters[] conditions;
	
	public MinBASQOption(ConditionParameters[] conditions) {
		this.conditions = conditions;

		opt = "q";
		longOpt = "min-basq";
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("filter positions with base quality < " + longOpt.toUpperCase() + " \n default: " + conditions[0].getMinBASQ())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	byte minBASQ = Byte.parseByte(value);
	    	if(minBASQ < 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " = " + minBASQ + " not valid.");
	    	}
	    	for (ConditionParameters condition : conditions) {
	    		condition.setMinBASQ(minBASQ);
	    	}
	    }
	}

}