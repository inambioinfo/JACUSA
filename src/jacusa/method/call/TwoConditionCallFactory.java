package jacusa.method.call;

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
import jacusa.cli.options.pileupbuilder.TwoConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.DistanceFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6call;
import jacusa.io.format.VCFcall;
import jacusa.method.AbstractMethodFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.pileup.dispatcher.call.TwoConditionCallWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class TwoConditionCallFactory 
extends AbstractMethodFactory<BaseQualData> {

	private static TwoConditionCallWorkerDispatcher<BaseQualData> instance;

	public TwoConditionCallFactory() {
		super("call-2", "Call variants - two conditions", new CallParameters<BaseQualData>(2));
	}
	
	public void initACOptions() {
		// condition specific setting
		ConditionParameters<BaseQualData> condition1 = getParameters().getConditionParameters(0);
		ConditionParameters<BaseQualData> condition2 = getParameters().getConditionParameters(1);

		for (int conditionIndex = 1; conditionIndex <= 2; ++conditionIndex) {
			initConditionACOptions(conditionIndex, condition1);
			initConditionACOptions(conditionIndex, condition2);
		}
		List<ConditionParameters<BaseQualData>> conditions = new ArrayList<ConditionParameters<BaseQualData>>(2);
		conditions.add(condition1);
		conditions.add(condition2);
		
		
		// global settings
		acOptions.add(new MinMAPQOption<BaseQualData>(conditions));
		acOptions.add(new MinBASQOption<BaseQualData>(conditions));
		acOptions.add(new MinCoverageOption<BaseQualData>(conditions));
		acOptions.add(new MaxDepthOption(getParameters()));
		acOptions.add(new FilterFlagOption<BaseQualData>(conditions));

		acOptions.add(new TwoConditionBaseQualDataBuilderOption<BaseQualData>(condition1, condition2));

		acOptions.add(new BedCoordinatesOption(getParameters()));
		acOptions.add(new ResultFileOption(getParameters()));
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameters().setFormat(getResultFormats().get(BED6call.CHAR));
			acOptions.add(new FormatOption<BaseQualData, AbstractOutputFormat<BaseQualData>>(getParameters(), getResultFormats()));
		}

		acOptions.add(new MaxThreadOption(getParameters()));
		acOptions.add(new WindowSizeOption(getParameters()));
		acOptions.add(new ThreadWindowSizeOption(getParameters()));

		if (getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			getParameters().getStatisticParameters().setStatisticCalculator(getStatistics().get(a[0]));
		} else {
			acOptions.add(new StatisticCalculatorOption<BaseQualData>(
					getParameters().getStatisticParameters(), getStatistics()));
		}

		acOptions.add(new FilterModusOption(getParameters()));
		acOptions.add(new BaseConfigOption(getParameters()));
		acOptions.add(new FilterConfigOption<BaseQualData>(getParameters(), getFilterFactories()));
		
		acOptions.add(new StatisticFilterOption(getParameters().getStatisticParameters()));

		acOptions.add(new ShowReferenceOption(getParameters()));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	@Override
	public TwoConditionCallWorkerDispatcher<BaseQualData> getInstance(
			CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new TwoConditionCallWorkerDispatcher<BaseQualData>(coordinateProvider, getParameters());
		}
		return instance;
	}

	public Map<String, StatisticCalculator<BaseQualData>> getStatistics() {
		Map<String, StatisticCalculator<BaseQualData>> statistics = 
				new TreeMap<String, StatisticCalculator<BaseQualData>>();

		StatisticCalculator<BaseQualData> statistic = null;

		statistic = new DirichletMultinomialRobustCompoundError<BaseQualData>(getParameters());
		statistics.put("DirMult", statistic);
		
		//statistic = new ACCUSA2Statistic(parameters.getBaseConfig(), parameters.getStatisticParameters());
		//statistics.put(statistic.getName(), statistic);
		
		return statistics;
	}

	public Map<Character, AbstractFilterFactory<BaseQualData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<BaseQualData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<BaseQualData>>();

		final List<AbstractFilterFactory<BaseQualData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<BaseQualData>>(10);
		
		filterFactories.add(new DistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new INDEL_DistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new ReadPositionDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new HomozygousFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new MaxAlleleCountFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new HomopolymerFilterFactory<BaseQualData>(getParameters()));

		for (final AbstractFilterFactory<BaseQualData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<BaseQualData>> getResultFormats() {
		Map<Character, AbstractOutputFormat<BaseQualData>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualData>>();

		AbstractOutputFormat<BaseQualData> resultFormat = null;

		// BED like output
		resultFormat = new BED6call(getParameters().getBaseConfig(), getParameters().getFilterConfig(), getParameters().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);

		// VCF output
		resultFormat = new VCFcall(getParameters().getBaseConfig(), getParameters().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public CallParameters<BaseQualData> getParameters() {
		return (CallParameters<BaseQualData>) super.getParameters();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public BaseQualData createDataContainer() {
		return new BaseQualData();
	}
	
	@Override
	public BaseQualData[] createDataContainer(final int n) {
		return new BaseQualData[n];
	}
	
	@Override
	public BaseQualData[][] createDataContainer(final int n, final int m) {
		if (m < 0) {
			return new BaseQualData[n][];
		}

		return new BaseQualData[n][m];
	}
	
	@Override
	public BaseQualData copyDataContainer(final BaseQualData dataContainer) {
		return new BaseQualData(dataContainer);
	}
	
	@Override
	public BaseQualData[] copyDataContainer(final BaseQualData[] dataContainer) {
		BaseQualData[] ret = createDataContainer(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public BaseQualData[][] copyDataContainer(final BaseQualData[][] dataContainer) {
		BaseQualData[][] ret = createDataContainer(dataContainer.length, -1);
		for (int i = 0; i < dataContainer.length; ++i) {
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new BaseQualData(dataContainer[i][j]);
			}	
		}
		
		return ret;
	}
}