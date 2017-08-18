package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class WindowSizeOption extends AbstractACOption {

	final private AbstractParameters<?> parameters; 
	
	public WindowSizeOption(AbstractParameters<?> parameters) {
		this.parameters = parameters;

		opt = "w";
		longOpt = "window-size";
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName("WINDOW-SIZE")
			.hasArg(true)
	        .withDescription("size of the window used for caching. Make sure this is greater than the read size \n default: " + parameters.getWindowSize())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
	    	String value = line.getOptionValue(opt);
	    	int windowSize = Integer.parseInt(value);
	    	if (windowSize < 1) {
	    		throw new IllegalArgumentException("WINDOW-SIZE too small: " + windowSize);
	    	}

	    	parameters.setWindowSize(windowSize);
		}
	}

}
