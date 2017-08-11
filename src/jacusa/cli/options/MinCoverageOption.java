package jacusa.cli.options;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinCoverageOption<T extends AbstractData> extends AbstractACOption {

	final private List<ConditionParameters<T>> conditions;
	
	public MinCoverageOption(final List<ConditionParameters<T>> conditions) {
		opt = "c";
		longOpt = "min-coverage";
		
		this.conditions = conditions;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
					.withArgName(longOpt.toUpperCase())
					.hasArg(true)
			        .withDescription("filter positions with coverage < " + longOpt.toUpperCase() + " \n default: " + conditions.get(0).getMinCoverage())
			        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
	    if (line.hasOption(opt)) {
	    	int minCoverage = Integer.parseInt(line.getOptionValue(opt));
	    	if (minCoverage < 1) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " must be > 0!");
	    	}

	    	for (ConditionParameters<?> condition : conditions) {
	    		condition.setMinCoverage(minCoverage);
	    	}
	    }
	}

}