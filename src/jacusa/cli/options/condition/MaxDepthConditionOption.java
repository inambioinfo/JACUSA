package jacusa.cli.options.condition;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MaxDepthConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "d";
	private static final String LONG_OPT = "max-depth";
	
	public MaxDepthConditionOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	public MaxDepthConditionOption(final List<ConditionParameters<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();
		if (getConditionIndex() >= 0) {
			s = "max depth for condition " + getConditionIndex();
		} else {
			s = "max depth for all conditions";
		}
		
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg()
			.withDescription(s + "\nDefault: " + getConditions().get(0).getMaxDepth())
			.create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	int maxDepth = Integer.parseInt(line.getOptionValue(getOpt()));
	    	if(maxDepth < 2 || maxDepth == 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0 or -1 (limited by memory)!");
	    	}
	    	
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.setMaxDepth(maxDepth);
	    	}
	    }
	}

}