package jacusa.method.call;

import jacusa.cli.options.BaseConfigOption;
import jacusa.cli.options.BedCoordinatesOption;
import jacusa.cli.options.FilterConfigOption;
import jacusa.cli.options.FilterModusOption;
import jacusa.cli.options.FormatOption;
import jacusa.cli.options.HelpOption;
import jacusa.cli.options.MaxThreadOption;
import jacusa.cli.options.ResultFileOption;
import jacusa.cli.options.ShowReferenceOption;
import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.InvertStrandOption;
import jacusa.cli.options.condition.MaxDepthConditionOption;
import jacusa.cli.options.condition.MinBASQConditionOption;
import jacusa.cli.options.condition.MinCoverageConditionOption;
import jacusa.cli.options.condition.MinMAPQConditionOption;
import jacusa.cli.options.condition.filter.FilterFlagConditionOption;
import jacusa.cli.options.condition.filter.FilterNHsamTagOption;
import jacusa.cli.options.condition.filter.FilterNMsamTagOption;
import jacusa.cli.options.pileupbuilder.OneConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.CallParameters;
import jacusa.cli.parameters.ConditionParameters;
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
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.dispatcher.call.CallWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class CallFactory
extends AbstractMethodFactory<BaseQualData> {

	protected static CallWorkerDispatcher<BaseQualData> instance;
	
	public CallFactory(final int conditions) {
		super("call-" + (conditions == -1 ? "n" : conditions), 
				"Call variants - " + 
						(conditions == -1 ? "n" : conditions) + 
						(conditions == -1 || conditions == 2 ? " conditions" : " condition"), 
				new CallParameters<BaseQualData>(conditions));
	}

	protected void initGlobalACOptions() {
		addACOption(new FilterModusOption(getParameters()));
		addACOption(new BaseConfigOption(getParameters()));
		addACOption(new FilterConfigOption<BaseQualData>(getParameters(), getFilterFactories()));
		
		addACOption(new StatisticFilterOption(getParameters().getStatisticParameters()));

		addACOption(new ShowReferenceOption(getParameters()));
		addACOption(new HelpOption(CLI.getSingleton()));
		
		addACOption(new MaxThreadOption(getParameters()));
		addACOption(new WindowSizeOption(getParameters()));
		addACOption(new ThreadWindowSizeOption(getParameters()));
		
		addACOption(new BedCoordinatesOption(getParameters()));
		addACOption(new ResultFileOption(getParameters()));
	}
		
	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new MinBASQConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<BaseQualData>(getParameters().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<BaseQualData>(getParameters().getConditionParameters()));
		addACOption(new InvertStrandOption<BaseQualData>(getParameters().getConditionParameters()));
		
		addACOption(new OneConditionBaseQualDataBuilderOption<BaseQualData>(getParameters().getConditionParameters()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameters().getConditions(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			addACOption(new InvertStrandOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionBaseQualDataBuilderOption<BaseQualData>(conditionIndex, getParameters().getConditionParameters().get(conditionIndex)));
		}
	}

	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
		
		// statistic
		if (getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			getParameters().getStatisticParameters().setStatisticCalculator(
					getStatistics().get(a[0]));
		} else {
			addACOption(new StatisticCalculatorOption<BaseQualData>(
					getParameters().getStatisticParameters(), getStatistics()));
		}
		
		// result format
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameters().setFormat(getResultFormats().get(BED6call.CHAR));
			addACOption(new FormatOption<BaseQualData, AbstractOutputFormat<BaseQualData>>(
					getParameters(), getResultFormats()));
		}
	}

	@Override
	public AbstractWorkerDispatcher<BaseQualData> getInstance(
			final CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new CallWorkerDispatcher<BaseQualData>(coordinateProvider, getParameters());
		}

		return instance;
	}
	
	public Map<String, StatisticCalculator<BaseQualData>> getStatistics() {
		final Map<String, StatisticCalculator<BaseQualData>> statistics = 
				new TreeMap<String, StatisticCalculator<BaseQualData>>();

		StatisticCalculator<BaseQualData> statistic = null;

		statistic = new DirichletMultinomialRobustCompoundError<BaseQualData>(getParameters());
		statistics.put("DirMult", statistic);

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
		final Map<Character, AbstractOutputFormat<BaseQualData>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualData>>();

		AbstractOutputFormat<BaseQualData> resultFormat = null;

		// BED like output
		resultFormat = new BED6call(getParameters().getBaseConfig(), 
				getParameters().getFilterConfig(), getParameters().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);

		// VCF output
		resultFormat = new VCFcall(getParameters().getBaseConfig(), 
				getParameters().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public CallParameters<BaseQualData> getParameters() {
		return (CallParameters<BaseQualData>) super.getParameters();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			throw new ParseException("BAM File is not provided!");
		}

		// infer numer of conditions from number of file(s)
		final int conditions = args.length;
		getParameters().getConditionParameters().clear();
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			getParameters().getConditionParameters().add(new ConditionParameters<BaseQualData>());
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