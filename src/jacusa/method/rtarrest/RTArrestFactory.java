package jacusa.method.rtarrest;

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
import jacusa.cli.options.SAMPathnameArg;
import jacusa.cli.options.ShowReferenceOption;
import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.VersionOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.filter.FilterFlagOption;
import jacusa.cli.options.pileupbuilder.TwoConditionPileupBuilderOption;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.cli.parameters.ConditionParameters;

import jacusa.filter.factory.AbstractFilterFactory;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6ResultFormat;
import jacusa.io.format.RTArrestResultFormat;

import jacusa.method.AbstractMethodFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.method.rtarrest.statistic.BetaMultinomial;

import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;

import jacusa.pileup.worker.AbstractWorker;

import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.SAMCoordinateProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import net.sf.samtools.SAMSequenceRecord;

import org.apache.commons.cli.ParseException;

public class RTArrestFactory extends AbstractMethodFactory {

	public final static String NAME = "rt-arrest";
	private ConditionParameters condition1;
	private ConditionParameters condition2;
	private RTArrestParameters parameters;

	private static RTArrestWorkerDispatcher instance;

	public RTArrestFactory() {
		super(NAME, "Reverse Transcription Arrest - two conditions");
		
		parameters = new RTArrestParameters();
	}
	
	public void initACOptions() {
		// condition specific setting
		condition1 = parameters.getCondition1();
		condition2 = parameters.getCondition2();

		for (int i = 1; i <= 2; ++i) {
			initConditionACOptions(i, condition1);
			initConditionACOptions(i, condition2);
		}
		ConditionParameters[] conditions = new ConditionParameters[] {
			condition1, condition2
		};
		
		// global settings
		acOptions.add(new MinMAPQOption(conditions));

		acOptions.add(new MinBASQOption(conditions));
		acOptions.add(new MinCoverageOption(conditions));
		acOptions.add(new MaxDepthOption(parameters));
		acOptions.add(new FilterFlagOption(conditions));
		
		acOptions.add(new TwoConditionPileupBuilderOption(parameters, condition1, condition2));

		acOptions.add(new BedCoordinatesOption(parameters));
		acOptions.add(new ResultFileOption(parameters));
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			parameters.setFormat(getResultFormats().get(a[0]));
		} else {
			parameters.setFormat(getResultFormats().get(BED6ResultFormat.CHAR));
			acOptions.add(new FormatOption<AbstractOutputFormat>(parameters, getResultFormats()));
		}

		acOptions.add(new MaxThreadOption(parameters));
		acOptions.add(new WindowSizeOption(parameters));
		acOptions.add(new ThreadWindowSizeOption(parameters));

		if (getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			parameters.getStatisticParameters().setStatisticCalculator(getStatistics().get(a[0]));
		} else {
			acOptions.add(new StatisticCalculatorOption(parameters.getStatisticParameters(), getStatistics()));
		}

		// TODO do we need this?
		acOptions.add(new FilterModusOption(parameters));
		// TODO do we need this?
		acOptions.add(new FilterConfigOption(parameters, getFilterFactories()));
		
		acOptions.add(new StatisticFilterOption(parameters.getStatisticParameters()));

		// TODO do we need this?  
		acOptions.add(new ShowReferenceOption(parameters));
		acOptions.add(new HelpOption(CLI.getSingleton()));
		acOptions.add(new VersionOption());
	}

	public Map<String, StatisticCalculator> getStatistics() {
		Map<String, StatisticCalculator> statistics = new TreeMap<String, StatisticCalculator>();

		StatisticCalculator statistic = null;
		statistic = new BetaMultinomial(condition1, condition2, parameters.getStatisticParameters());
		statistics.put(statistic.getName(), statistic);

		return statistics;
	}

	// TODO
	public Map<Character, AbstractFilterFactory<?>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<?>> abstractPileupFilters = new HashMap<Character, AbstractFilterFactory<?>>();

		AbstractFilterFactory<?>[] filterFactories = new AbstractFilterFactory[] {};
		for (AbstractFilterFactory<?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat> getResultFormats() {
		Map<Character, AbstractOutputFormat> resultFormats = new HashMap<Character, AbstractOutputFormat>();

		AbstractOutputFormat resultFormat = null;

		resultFormat = new RTArrestResultFormat(parameters.getBaseConfig(), parameters.getFilterConfig(), parameters.showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public void initCoordinateProvider() throws Exception {
		String[] pathnames1 = parameters.getCondition1().getPathnames();
		String[] pathnames2 = parameters.getCondition2().getPathnames();
		List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames1, pathnames2);
		coordinateProvider = new SAMCoordinateProvider(records);
	}

	@Override
	public AbstractParameters getParameters() {
		return parameters;
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		SAMPathnameArg pa = new SAMPathnameArg(1, parameters.getCondition1());
		pa.processArg(args[0]);
		pa = new SAMPathnameArg(2, parameters.getCondition2());
		pa.processArg(args[1]);

		return true;
	}

	@Override
	public AbstractWorkerDispatcher<? extends AbstractWorker> getInstance(
			String[] pathnames1, String[] pathnames2,
			CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new RTArrestWorkerDispatcher(pathnames1, pathnames2, coordinateProvider, parameters);
		}
		return instance;
	}
	
}