package jacusa.method.call;

import jacusa.JACUSA;
import jacusa.cli.options.AbstractACOption;
import jacusa.cli.options.BaseConfigOption;
import jacusa.cli.options.BedCoordinatesOption;
import jacusa.cli.options.FilterConfigOption;
import jacusa.cli.options.FilterModusOption;
import jacusa.cli.options.FormatOption;
import jacusa.cli.options.HelpOption;
import jacusa.cli.options.MaxDepthOption;
import jacusa.cli.options.MaxThreadOption;
import jacusa.cli.options.MinBASQOption;
import jacusa.cli.options.MinCoverageOption;
import jacusa.cli.options.MinMAPQOption;
import jacusa.cli.options.ResultFileOption;
import jacusa.cli.options.ShowReferenceOption;
import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.filter.FilterFlagOption;
import jacusa.cli.options.pileupbuilder.TwoConditionPileupBuilderOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.CallParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.DistanceFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactors;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6callFormat;
import jacusa.io.format.VCF_ResultFormat;
import jacusa.method.AbstractMethodFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.pileup.BasePileup;
import jacusa.pileup.dispatcher.call.TwoConditionCallWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class TwoConditionCallFactory extends AbstractMethodFactory<BasePileup> {

	private static TwoConditionCallWorkerDispatcher instance;

	public TwoConditionCallFactory() {
		super("call-2", "Call variants - two conditions", new CallParameters<BasePileup>(2));
	}
	
	public void initACOptions() {
		// condition specific setting
		ConditionParameters condition1 = getParameters().getConditionParameters(0);
		ConditionParameters condition2 = getParameters().getConditionParameters(1);

		for (int conditionIndex = 1; conditionIndex <= 2; ++conditionIndex) {
			initConditionACOptions(conditionIndex, condition1);
			initConditionACOptions(conditionIndex, condition2);
		}
		ConditionParameters[] conditions = new ConditionParameters[] {
			condition1, condition2
		};
		
		// global settings
		acOptions.add(new MinMAPQOption(conditions));
		acOptions.add(new MinBASQOption(conditions));
		acOptions.add(new MinCoverageOption(conditions));
		acOptions.add(new MaxDepthOption(getParameters()));
		acOptions.add(new FilterFlagOption(conditions));

		acOptions.add(new TwoConditionPileupBuilderOption(getParameters(), condition1, condition2));

		acOptions.add(new BedCoordinatesOption(getParameters()));
		acOptions.add(new ResultFileOption(getParameters()));
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameters().setFormat(getResultFormats().get(BED6callFormat.CHAR));
			acOptions.add(new FormatOption<AbstractOutputFormat<BasePileup>>(getParameters(), getResultFormats()));
		}

		acOptions.add(new MaxThreadOption(getParameters()));
		acOptions.add(new WindowSizeOption(getParameters()));
		acOptions.add(new ThreadWindowSizeOption(getParameters()));

		if (getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			getParameters().getStatisticParameters().setStatisticCalculator(getStatistics().get(a[0]));
		} else {
			acOptions.add(new StatisticCalculatorOption(getParameters().getStatisticParameters(), getStatistics()));
		}

		acOptions.add(new FilterModusOption(getParameters()));
		acOptions.add(new BaseConfigOption(getParameters()));
		acOptions.add(new FilterConfigOption(getParameters(), getFilterFactories()));
		
		acOptions.add(new StatisticFilterOption(getParameters().getStatisticParameters()));

		acOptions.add(new ShowReferenceOption(getParameters()));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	@Override
	public TwoConditionCallWorkerDispatcher getInstance(
			CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new TwoConditionCallWorkerDispatcher(coordinateProvider, getParameters());
		}
		return instance;
	}
	
	public Map<String, StatisticCalculator<BasePileup>> getStatistics() {
		Map<String, StatisticCalculator<BasePileup>> statistics = new TreeMap<String, StatisticCalculator<BasePileup>>();

		StatisticCalculator<BasePileup> statistic = null;

		statistic = new DirichletMultinomialRobustCompoundError<BasePileup>(getParameters().getBaseConfig(), 
				getParameters().getStatisticParameters());
		statistics.put("DirMult", statistic);
		
		//statistic = new ACCUSA2Statistic(parameters.getBaseConfig(), parameters.getStatisticParameters());
		//statistics.put(statistic.getName(), statistic);
		
		return statistics;
	}

	// FIXME ?
	public Map<Character, AbstractFilterFactory<?>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<?>> abstractPileupFilters = new HashMap<Character, AbstractFilterFactory<?>>();

		AbstractFilterFactory<?>[] filterFactories = new AbstractFilterFactory[] {
				new DistanceFilterFactory(getParameters()),
				new INDEL_DistanceFilterFactory(getParameters()),
				new ReadPositionDistanceFilterFactory(getParameters()),
				new SpliceSiteDistanceFilterFactory(getParameters()),
				new HomozygousFilterFactory(getParameters()),
				new MaxAlleleCountFilterFactors(getParameters()),
				new HomopolymerFilterFactory(getParameters()),
		};
		for (AbstractFilterFactory<?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<BasePileup>> getResultFormats() {
		Map<Character, AbstractOutputFormat<BasePileup>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<BasePileup>>();

		AbstractOutputFormat<BasePileup> resultFormat = null;

		// BED like output
		resultFormat = new BED6callFormat<BasePileup>(getParameters().getBaseConfig(), getParameters().getFilterConfig(), getParameters().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);

		// VCF output
		resultFormat = new VCF_ResultFormat<BasePileup>(getParameters().getBaseConfig(), getParameters().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}



	@Override
	public CallParameters<BasePileup> getParameters() {
		return (CallParameters<BasePileup>) super.getParameters();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(160);

		Set<AbstractACOption> acOptions = getACOptions();
		Options options = new Options();
		for (AbstractACOption acoption : acOptions) {
			options.addOption(acoption.getOption());
		}
		
		formatter.printHelp(JACUSA.JAR + " [OPTIONS] BAM1_1[,BAM1_2,BAM1_3,...] BAM2_1[,BAM2_2,BAM2_3,...]", options);
	}
	
}