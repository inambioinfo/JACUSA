package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameters;
import jacusa.data.AbstractData;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class StatisticCalculatorOption<T extends AbstractData> 
extends AbstractACOption {

	private StatisticParameters<T> parameters;
	private Map<String,StatisticCalculator<T>> statistics;

	public StatisticCalculatorOption(final StatisticParameters<T> parameters, 
			final Map<String, StatisticCalculator<T>> pileup2Statistic) {
		this.parameters = parameters;

		opt = "u";
		longOpt = "modus";

		this.statistics = pileup2Statistic;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuilder sb = new StringBuilder();

		for (String name : statistics.keySet()) {
			StatisticCalculator<T> statistic = statistics.get(name);

			if(parameters.getStatisticCalculator() != null && statistic.getName().equals(parameters.getStatisticCalculator().getName())) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + name);
			sb.append(" : ");
			sb.append(statistic.getDescription());
			sb.append("\n");
		}

		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
			.withDescription("Choose between different modes:\n" + sb.toString())
			.create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(opt)) {
			String name = line.getOptionValue(opt);
			String str = Character.toString(AbstractFilterFactory.SEP);
			if (name.indexOf(str) > -1) {
				String[] cols = name.split(str, 2);
				name = cols[0];
			}

			if (! statistics.containsKey(name)) {
				throw new IllegalArgumentException("Unknown statistic: " + name);
			}
			parameters.setStatisticCalculator(statistics.get(name));
			parameters.getStatisticCalculator().processCLI(line.getOptionValue(opt));
		}
	}

}
