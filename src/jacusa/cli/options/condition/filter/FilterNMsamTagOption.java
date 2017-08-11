package jacusa.cli.options.condition.filter;

import jacusa.cli.options.condition.filter.samtag.SamTagFilter;
import jacusa.cli.options.condition.filter.samtag.SamTagNMFilter;
import jacusa.cli.parameters.ConditionParameters;

public class FilterNMsamTagOption extends AbstractFilterSamTagOption {
	
	public FilterNMsamTagOption(final int conditionIndex, final ConditionParameters<?> paramters) {
		super(conditionIndex, paramters, "NM");
	}

	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNMFilter(value);
	}

}
