package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
// import jacusa.filter.factory.MaxDepthFilterFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MaxDepthOption extends AbstractACOption {

	private AbstractParameters parameters;
	
	public MaxDepthOption(final AbstractParameters parameters) {
		opt = "d";
		longOpt = "max-depth";
		this.parameters = parameters;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg()
			.withDescription("max per-BAM depth\ndefault: " + parameters.getConditionParameters(0).getMaxDepth())
			.create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	int maxDepth = Integer.parseInt(line.getOptionValue(opt));
	    	if (maxDepth <= -2 || maxDepth == 0) {
	    		throw new IllegalArgumentException(longOpt.toUpperCase() + " must be > 0 or -1 (coverage restricted by memory)!");
	    	}

	    	for (final ConditionParameters cp : parameters.getConditionParameters()) {
	    		cp.setMaxDepth(maxDepth);
	    	}
	    	
	    	
	    	// FIXME
	    	/*
	    	if (! parameters.getFilterConfig().hasFilter(MaxDepthFilterFactory.C)) {
	    		parameters.getFilterConfig().addFactory(new MaxDepthFilterFactory(parameters));
	    	}
	    	*/
	    }
	}

}