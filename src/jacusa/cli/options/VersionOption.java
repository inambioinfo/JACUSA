package jacusa.cli.options;

import jacusa.JACUSA;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class VersionOption extends AbstractACOption {

	public VersionOption() {
		opt = "v";
		longOpt = "version";
	}

	// TODO
	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
			System.err.print(JACUSA.VERSION + "\n"); 
	    	System.exit(0);
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.hasArg(false)
	        .withDescription("Print version information.")
	        .create(opt);
	}

}