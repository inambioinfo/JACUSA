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
import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.InvertStrandOption;
import jacusa.cli.options.condition.filter.FilterFlagOption;
import jacusa.cli.options.condition.filter.FilterNHsamTagOption;
import jacusa.cli.options.condition.filter.FilterNMsamTagOption;
import jacusa.cli.options.pileupbuilder.OneConditionBaseQualDataBuilderOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.CallParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.DistanceFilterFactory;
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
import jacusa.pileup.dispatcher.call.OneConditionCallWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.SAMCoordinateProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

import net.sf.samtools.SAMSequenceRecord;

public class OneConditionCallFactory 
extends AbstractMethodFactory<BaseQualData> {

	private static OneConditionCallWorkerDispatcher<BaseQualData> instance;
	
	public OneConditionCallFactory() {
		super("call-1", "Call variants - one condition", new CallParameters<BaseQualData>(1));
	}
	
	public void initACOptions() {
		final ConditionParameters<BaseQualData> condition = 
				getParameters().getConditionParameters(0);
		/*
		final ConditionParameters[] conditions = new ConditionParameters[] {
				condition
		};
		
		initGlobalACOptions(conditions);
		*/
		
		final int conditionIndex = 1;
		acOptions.add(new FilterNHsamTagOption(conditionIndex, condition));
		acOptions.add(new FilterNMsamTagOption(conditionIndex, condition));
		acOptions.add(new InvertStrandOption(conditionIndex, condition));
		
		// FIXME
		acOptions.add(new OneConditionBaseQualDataBuilderOption<BaseQualData>(condition));

		acOptions.add(new BedCoordinatesOption(getParameters()));
		acOptions.add(new ResultFileOption(getParameters()));
		if(getFormats().size() == 1 ) {
			Character[] a = getFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getFormats().get(a[0]));
		} else {
			getParameters().setFormat(new BED6call(getParameters().getBaseConfig(), getParameters().getFilterConfig(), true));
			// FIXME
			acOptions.add(new FormatOption<BaseQualData,AbstractOutputFormat<BaseQualData>>(getParameters(), getFormats()));
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

		acOptions.add(new FilterConfigOption<BaseQualData>(getParameters(), getFilterFactories()));
		acOptions.add(new FilterModusOption(getParameters()));
		
		acOptions.add(new BaseConfigOption(getParameters()));
		acOptions.add(new StatisticFilterOption(getParameters().getStatisticParameters()));
		
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	protected void initGlobalOptions(final List<ConditionParameters<BaseQualData>> conditions) {
		acOptions.add(new MinMAPQOption<BaseQualData>(conditions));
		acOptions.add(new MinBASQOption<BaseQualData>(conditions));
		acOptions.add(new MinCoverageOption<BaseQualData>(conditions));
		acOptions.add(new MaxDepthOption(getParameters()));
		acOptions.add(new FilterFlagOption<BaseQualData>(conditions));
	}
	
	@Override
	public OneConditionCallWorkerDispatcher<BaseQualData> getInstance(
			final CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new OneConditionCallWorkerDispatcher<BaseQualData>(coordinateProvider, getParameters());
		}
		return instance;
	}
	
	public Map<String, StatisticCalculator<BaseQualData>> getStatistics() {
		Map<String, StatisticCalculator<BaseQualData>> statistics = 
				new TreeMap<String, StatisticCalculator<BaseQualData>>();

		StatisticCalculator<BaseQualData> statistic = null;

		/*
		statistic = new ACCUSA2Statistic(getParameters().getBaseConfig(), getParameters().getStatisticParameters());
		statistics.put(statistic.getName(), statistic);
		*/
		
		/*
		statistic = new DirichletMultinomialCompoundError(getParameters().getBaseConfig(), getParameters().getStatisticParameters());
		statistics.put(statistic.getName(), statistic);
		*/

		statistic = new DirichletMultinomialRobustCompoundError<BaseQualData>(getParameters());
		statistics.put(statistic.getName(), statistic);

		return statistics;
	}

	public Map<Character, AbstractFilterFactory<BaseQualData>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<BaseQualData>> c2filterFactory = 
				new HashMap<Character, AbstractFilterFactory<BaseQualData>>();

		final List<AbstractFilterFactory<BaseQualData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<BaseQualData>>(5);
		
		filterFactories.add(new DistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new INDEL_DistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new ReadPositionDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new MaxAlleleCountFilterFactory<BaseQualData>(getParameters()));

		for (final AbstractFilterFactory<BaseQualData> filterFactory : filterFactories) {
			c2filterFactory.put(filterFactory.getC(), filterFactory);
		}

		return c2filterFactory;
	}

	public Map<Character, AbstractOutputFormat<BaseQualData>> getFormats() {
		Map<Character, AbstractOutputFormat<BaseQualData>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualData>>();

		AbstractOutputFormat<BaseQualData> resultFormat = null;

		// BED like output
		resultFormat = new BED6call(getParameters().getBaseConfig(), getParameters().getFilterConfig(), true);
		resultFormats.put(resultFormat.getC(), resultFormat);

		// VCF output
		resultFormat = new VCFcall(getParameters().getBaseConfig(), getParameters().getFilterConfig());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public void initCoordinateProvider() throws Exception {
		String[] pathnames = getParameters().getConditionParameters(0).getPathnames();

		List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames);
		coordinateProvider = new SAMCoordinateProvider(records);
	}

	@Override
	public CallParameters<BaseQualData> getParameters() {
		return (CallParameters<BaseQualData>) super.getParameters();
	}
	
	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 1) {
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
