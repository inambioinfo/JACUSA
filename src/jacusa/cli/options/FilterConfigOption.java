package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.AbstractData;
import jacusa.filter.factory.AbstractFilterFactory;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class FilterConfigOption<T extends AbstractData> extends AbstractACOption {

	final private AbstractParameters<T> parameters;

	private static final char OR = ',';
	//private static char AND = '&'; // Future Feature add logic

	final private Map<Character, AbstractFilterFactory<T>> pileupFilterFactories;

	public FilterConfigOption(final AbstractParameters<T> parameters, 
			final Map<Character, AbstractFilterFactory<T>> pileupFilterFactories) {
		this.parameters = parameters;

		opt = "a";
		longOpt = "pileup-filter";

		this.pileupFilterFactories = pileupFilterFactories;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		for (final char c : pileupFilterFactories.keySet()) {
			final AbstractFilterFactory<T> pileupFilterFactory = pileupFilterFactories.get(c);
			sb.append(pileupFilterFactory.getC());
			sb.append(" | ");
			sb.append(pileupFilterFactory.getDesc());
			sb.append("\n");
		}

		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
			.withDescription(
					"chain of " + longOpt.toUpperCase() + " to apply to pileups:\n" + sb.toString() + 
					"\nSeparate multiple " + longOpt.toUpperCase() + " with '" + OR + "' (e.g.: D,I)")
			.create(opt); 
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
			final String s = line.getOptionValue(opt);
			final String[] t = s.split(Character.toString(OR));

			for (final String a : t) {
				char c = a.charAt(0);
				if (! pileupFilterFactories.containsKey(c)) {
					throw new IllegalArgumentException("Unknown SAM processing: " + c);
				}
				AbstractFilterFactory<T> filterFactory = pileupFilterFactories.get(c);
				if (a.length() > 1) {
					filterFactory.processCLI(a);
				}
				parameters.getFilterConfig().addFactory(filterFactory);
			}
		}
	}

}