package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseConfig;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class BaseConfigOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;

	public BaseConfigOption(final AbstractParameters<?> parameters) {
		this.parameters = parameters;

		opt = "C";
		longOpt = "base-config";
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuilder sb = new StringBuilder();
		for(char c : parameters.getBaseConfig().getBases()) {
			sb.append(c);
		}

		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
	        .withDescription("Choose what bases should be considered for variant calling: TC or AG or ACGT or AT...\n default: " + sb.toString())
	        .create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
	    	char[] values = line.getOptionValue(opt).toCharArray();
	    	if(values.length < 2 || values.length > BaseConfig.BASES.length) {
	    		throw new IllegalArgumentException("Possible values for " + longOpt.toUpperCase() + ": TC, AG, ACGT, AT...");
	    	}
	    	parameters.getBaseConfig().setBases(values);
	    }
	}

}