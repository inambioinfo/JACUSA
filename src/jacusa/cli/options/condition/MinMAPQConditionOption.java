package jacusa.cli.options.condition;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinMAPQConditionOption extends AbstractACOption {

	private int conditionIndex;
	private ConditionParameters<?> condition;
	
	public MinMAPQConditionOption(final int conditionIndex, final ConditionParameters<?> condition) {
		this.conditionIndex = conditionIndex;
		this.condition = condition;

		opt = "m" + conditionIndex;
		longOpt = "min-mapq" + conditionIndex;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
				.withArgName(longOpt.toUpperCase())
				.hasArg(true)
		        .withDescription("filter " + conditionIndex + " positions with MAPQ < " + longOpt.toUpperCase() + "\n default: " + condition.getMinMAPQ())
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
	    	condition.setMinMAPQ(minMapq);
	    }
	}

}
