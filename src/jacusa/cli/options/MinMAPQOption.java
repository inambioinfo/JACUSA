package jacusa.cli.options;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinMAPQOption<T extends AbstractData> extends AbstractACOption {

	private final List<ConditionParameters<T>> conditions;
	
	public MinMAPQOption(final List<ConditionParameters<T>> conditions) {
		opt = "m";
		longOpt = "min-mapq";

		this.conditions = conditions;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("filter positions with MAPQ < " + longOpt.toUpperCase() + "\n default: " + conditions.get(0).getMinMAPQ())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	int minMapq = Integer.parseInt(value);
	    	if(minMapq < 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " = " + minMapq + " not valid.");
	    	}
	    	
	    	for (final ConditionParameters<?> condition : conditions) {
		    	condition.setMinMAPQ(minMapq);
	    	}
	    }
	}

}