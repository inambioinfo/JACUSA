package jacusa.cli.options.condition;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinCoverageConditionOption extends AbstractACOption {

	private int conditionIndex;
	private ConditionParameters condition;
	
	public MinCoverageConditionOption(final int conditionIndex, final ConditionParameters condition) {
		this.conditionIndex = conditionIndex;
		this.condition = condition;
		
		opt = "c" + conditionIndex;
		longOpt = "min-coverage" + conditionIndex;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
					.withArgName(longOpt.toUpperCase())
					.hasArg(true)
			        .withDescription("filter " + conditionIndex + " positions with coverage < " + longOpt.toUpperCase() + " \n default: " + condition.getMinCoverage())
			        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
	    if(line.hasOption(opt)) {
	    	int minCoverage = Integer.parseInt(line.getOptionValue(opt));
	    	if(minCoverage < 1) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " must be > 0!");
	    	}
	    	condition.setMinCoverage(minCoverage);
	    }
	}

}
