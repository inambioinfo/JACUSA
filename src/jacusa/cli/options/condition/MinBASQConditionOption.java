package jacusa.cli.options.condition;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinBASQConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "q";
	private static final String LONG_OPT = "min-basq";
	
	public MinBASQConditionOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	public MinBASQConditionOption(final List<ConditionParameters<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();
		if (getConditionIndex() >= 0) {
			s = "filter condition " + getConditionIndex();
		} else {
			s = "filter all conditions";
		}
		
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription(s + " with positions with base quality < " + getLongOpt().toUpperCase() + 
	        		" \n default: " + getConditions().get(0).getMinBASQ())
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	byte minBASQ = Byte.parseByte(value);
	    	if(minBASQ < 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minBASQ + " not valid.");
	    	}
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.setMinBASQ(minBASQ);
	    	}
		}
	}

}
