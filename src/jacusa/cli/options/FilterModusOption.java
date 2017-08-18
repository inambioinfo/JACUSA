package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class FilterModusOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public FilterModusOption(final AbstractParameters<?> parameters) {
		this.parameters = parameters;

		opt = "s";
		longOpt = "separate";
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
			parameters.setSeparate(true);
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
				.withArgName(longOpt.toUpperCase())
				.hasArg(false)
		        .withDescription("Put feature-filtered results in to a separate file (= RESULT-FILE.filtered)")
		        .create(opt);
	}

}