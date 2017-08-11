package jacusa.cli.options.condition;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinBASQConditionOption extends AbstractACOption {

	private int conditionIndex;
	private final ConditionParameters<?> condition;
	
	public MinBASQConditionOption(final int conditionIndex, final ConditionParameters<?> condition) {
		this.conditionIndex = conditionIndex;
		this.condition = condition;
		
		opt = "q" + conditionIndex;
		longOpt = "min-basq" + conditionIndex;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("filter " + conditionIndex + " positions with base quality < " + longOpt.toUpperCase() + " \n default: " + condition.getMinBASQ())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	byte minBASQ = Byte.parseByte(value);
	    	if(minBASQ < 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " = " + minBASQ + " not valid.");
	    	}
	    	condition.setMinBASQ(minBASQ);
	    }
	}

}