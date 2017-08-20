package jacusa.cli.options.condition.filter;

import java.util.List;

import jacusa.cli.options.condition.AbstractConditionACOption;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class FilterFlagConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "F";
	private static final String LONG_OPT = "filter-flags";
	
	public FilterFlagConditionOption(final List<ConditionParameters<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}

	public FilterFlagConditionOption(final int conditionIndex, ConditionParameters<T> conditions) {
		super(OPT, LONG_OPT, conditionIndex, conditions);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();
		if (getConditionIndex() >= 0) {
			s = "filter reads with flags " + getLongOpt().toUpperCase() + " for condition " + getConditionIndex();
		} else {
			s = "filter reads with flags " + getLongOpt().toUpperCase() + " for all conditions";
		}		
		
		return OptionBuilder.withLongOpt(getLongOpt())
				.withArgName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .withDescription(s + " \ndefault: " + getConditions().get(0).getFilterFlags())
		        .create(getOpt());
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	int filterFlags = Integer.parseInt(value);
	    	if (filterFlags <= 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + filterFlags + " not valid.");
	    	}
	    	
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.setFilterFlags(filterFlags);
	    	}
	    }
	}

}