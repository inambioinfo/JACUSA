package jacusa.cli.options;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinBASQOption<T extends AbstractData> extends AbstractACOption {

	private final List<ConditionParameters<T>> conditions;

	public MinBASQOption(final List<ConditionParameters<T>> conditions) {
		opt = "q";
		longOpt = "min-basq";
		
		this.conditions = conditions;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("filter positions with base quality < " + longOpt.toUpperCase() + " \n default: " + conditions.get(0).getMinBASQ())
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
	    	
	    	for (final ConditionParameters<?> condition : conditions) {
	    		condition.setMinBASQ(minBASQ);
	    	}
	    }
	}

}
