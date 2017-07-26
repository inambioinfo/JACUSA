package jacusa.cli.options.condition;

import jacusa.cli.options.AbstractACOption;
import jacusa.cli.parameters.ConditionParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class InvertStrandOption extends AbstractACOption {

	private int conditionIndex;
	private ConditionParameters condition;
	
	public InvertStrandOption(final int conditionIndex, final ConditionParameters condition) {
		this.conditionIndex = conditionIndex;
		this.condition = condition;

		opt = "i" + conditionIndex;
		longOpt = "invert-strand" + conditionIndex;
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
			condition.setInvertStrand(true);
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
				.withArgName(longOpt.toUpperCase())
				.hasArg(false)
		        .withDescription("Invert strand of " + conditionIndex + " condition. Default " + Boolean.toString(condition.isInvertStrand()))
		        .create(opt);
	}

}