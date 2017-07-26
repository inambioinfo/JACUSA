package jacusa.cli.options.condition.filter;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.options.condition.filter.samtag.SamTagFilter;
import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public abstract class AbstractFilterSamTagOption extends AbstractACOption {

	// private int condition;
	private ConditionParameters parameters;
	private String tag;

	public AbstractFilterSamTagOption(int conditionIndex, ConditionParameters condition, String tag) {
		this.parameters = condition;
		this.tag = tag;
		longOpt = "filter" + tag + "_" + conditionIndex;
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(longOpt)) {
	    	int value = Integer.parseInt(line.getOptionValue(longOpt));
	    	parameters.getSamTagFilters().add(createSamTagFilter(value));
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
				.withArgName(tag + "-VALUE")
				.hasArg(true)
		        .withDescription("Max " + tag + "-VALUE for SAM tag " + tag)
		        .create();
	}

	protected abstract SamTagFilter createSamTagFilter(int value);  

}