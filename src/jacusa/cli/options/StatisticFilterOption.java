package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class StatisticFilterOption  extends AbstractACOption {

	private StatisticParameters<?> parameters;

	public StatisticFilterOption(StatisticParameters<?> parameters) {
		this.parameters = parameters;
		opt = "T";
		longOpt = "threshold";
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("Filter positions based on test-statistic " + longOpt.toUpperCase() + "\n default: DO NOT FILTER")
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
		    String value = line.getOptionValue(opt);
	    	double stat = Double.parseDouble(value);
	    	if (stat < 0) {
	    		throw new Exception("Invalid value for " + longOpt.toUpperCase() + ". Allowed values are 0 <= " + longOpt.toUpperCase());
	    	}
	    	parameters.setThreshold(stat);
		}
	}

}