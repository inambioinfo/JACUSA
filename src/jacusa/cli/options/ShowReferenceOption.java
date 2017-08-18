package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ShowReferenceOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;

	public ShowReferenceOption(final AbstractParameters<?> parameters) {
		this.parameters = parameters;

		opt = "R";
		longOpt = "show-ref";
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
				return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(false)
	        .withDescription("Add reference base to output. BAM file(s) must have MD field!")
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
	    	parameters.setShowReferenceBase(true);
	    }
	}

}