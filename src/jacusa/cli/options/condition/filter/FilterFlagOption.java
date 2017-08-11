package jacusa.cli.options.condition.filter;

import java.util.List;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class FilterFlagOption<T extends AbstractData> extends AbstractACOption {

	private final List<ConditionParameters<T>> conditions;
	
	public FilterFlagOption(final List<ConditionParameters<T>> conditions) {
		opt = "F";
		longOpt = "filter-flags";
		this.conditions = conditions;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
				.withArgName(longOpt.toUpperCase())
				.hasArg(true)
		        .withDescription("filter reads with flags " + longOpt.toUpperCase() + " \n default: " + conditions.get(0).getFilterFlags())
		        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	int filterFlags = Integer.parseInt(value);
	    	if (filterFlags <= 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " = " + filterFlags + " not valid.");
	    	}
	    	for (ConditionParameters<?> condition : conditions) {
	    		condition.setFilterFlags(filterFlags);
	    	}
	    }
	}

}