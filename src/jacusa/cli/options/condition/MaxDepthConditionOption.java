package jacusa.cli.options.condition;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.filter.factory.MaxDepthFilterFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MaxDepthConditionOption extends AbstractACOption {

	private int conditionIndex;
	private ConditionParameters condition;
	private AbstractParameters parameters;
	
	public MaxDepthConditionOption(
			final int conditionIndex, 
			final ConditionParameters condition,
			final AbstractParameters parameters) {
		this.conditionIndex = conditionIndex;
		this.condition = condition;
		this.parameters = parameters;

		opt = "d" + conditionIndex;
		longOpt = "max-depth" + conditionIndex;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg()
			.withDescription("max per-condition " + conditionIndex + " depth\ndefault: " + condition.getMaxDepth())
			.create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
	    	int maxDepth = Integer.parseInt(line.getOptionValue(opt));
	    	if(maxDepth < 2 || maxDepth == 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " must be > 0 or -1 (limited by memory)!");
	    	}
	    	condition.setMaxDepth(maxDepth);

	    	if (! parameters.getFilterConfig().hasFilter(MaxDepthFilterFactory.C)) {
	    		parameters.getFilterConfig().getFactories().add(new MaxDepthFilterFactory(parameters));
	    	}

	    }
	}

}