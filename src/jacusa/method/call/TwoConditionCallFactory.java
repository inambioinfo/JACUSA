package jacusa.method.call;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.options.pileupbuilder.TwoConditionBaseQualDataBuilderOption;
import jacusa.data.BaseQualData;

import org.apache.commons.cli.ParseException;

public class TwoConditionCallFactory extends CallFactory {

	public TwoConditionCallFactory() {
		super(2);
	}

	@Override
	public void initACOptions() {
		super.initACOptions();
		
		for (final AbstractACOption ACOption : getACOptions()) {
			if (ACOption.getOpt().equals("P")) {
				getACOptions().remove(ACOption);
			}
		}

		addACOption(new TwoConditionBaseQualDataBuilderOption<BaseQualData>(
				getParameters().getConditionParameters().get(0),
				getParameters().getConditionParameters().get(1)));
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

}
