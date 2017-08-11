package jacusa.cli.options.condition.filter;

import jacusa.cli.options.condition.filter.samtag.SamTagFilter;
import jacusa.cli.options.condition.filter.samtag.SamTagNHFilter;
import jacusa.cli.parameters.ConditionParameters;

public class FilterNHsamTagOption extends AbstractFilterSamTagOption {

	public FilterNHsamTagOption(final int conditionIndex, final ConditionParameters<?> parameters) {
		super(conditionIndex, parameters, "NH");
	}

	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNHFilter(value);
	}

}