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
import jacusa.cli.options.ShowReferenceOption;
import jacusa.cli.options.StatisticCalculatorOption;
import jacusa.cli.options.StatisticFilterOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.filter.FilterFlagOption;
import jacusa.cli.options.pileupbuilder.TwoConditionPileupBuilderOption;

import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.cli.parameters.ConditionParameters;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.DistanceFilterFactory;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6callFormat;
import jacusa.io.format.RTArrestResultFormat;

import jacusa.method.AbstractMethodFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.method.rtarrest.statistic.BetaMultinomial;

import jacusa.pileup.BaseReadPileup;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasReadInfoCount;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;


import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class RTArrestFactory<T extends Data<T> & hasReadInfoCount & hasCoordinate & hasBaseCount & hasRefBase> extends AbstractMethodFactory<T> {

	public final static String NAME = "rt-arrest";

	private static RTArrestWorkerDispatcher instance;

	public RTArrestFactory() {
		super(NAME, "Reverse Transcription Arrest - two conditions", new RTArrestParameters<T>(2));
	}
		
	public void initACOptions() {
		// condition specific setting
		// TODO make for n
		ConditionParameters condition1 = getParameters().getConditionParameters(1);
		ConditionParameters condition2 = getParameters().getConditionParameters(2);

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
			acOptions.add(new FormatOption<AbstractOutputFormat<T>>(getParameters(), getResultFormats()));
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
		acOptions.add(new FilterConfigOption(getParameters(), getFilterFactories()));
		
		acOptions.add(new StatisticFilterOption(getParameters().getStatisticParameters()));

 
		acOptions.add(new ShowReferenceOption(getParameters()));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	public Map<String, StatisticCalculator<BaseReadPileup>> getStatistics() {
		Map<String, StatisticCalculator<BaseReadPileup>> statistics = new TreeMap<String, StatisticCalculator<BaseReadPileup>>();

		StatisticCalculator<BaseReadPileup> statistic = null;
		
		// TODO make for n
		ConditionParameters condition1 = getParameters().getConditionParameters(1);
		ConditionParameters condition2 = getParameters().getConditionParameters(2);

		statistic = new BetaMultinomial<BaseReadPileup>(condition1, condition2, getParameters().getStatisticParameters());
		statistics.put(statistic.getName(), statistic);

		return statistics;
	}

	public Map<Character, AbstractFilterFactory<?>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<?>> abstractPileupFilters = new HashMap<Character, AbstractFilterFactory<?>>();

		AbstractFilterFactory<?>[] filterFactories = new AbstractFilterFactory[] {
				new DistanceFilterFactory<BaseReadPileup>(getParameters()),
		};
		
		
		for (AbstractFilterFactory<?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<T>> getResultFormats() {
		Map<Character, AbstractOutputFormat<T>> resultFormats = new HashMap<Character, AbstractOutputFormat<T>>();

		AbstractOutputFormat<T> resultFormat = null;

		resultFormat = new RTArrestResultFormat<T>(getParameters().getBaseConfig(), getParameters().getFilterConfig(), getParameters().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public RTArrestParameters<T> getParameters() {
		return (RTArrestParameters<T>) super.getParameters();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public RTArrestWorkerDispatcher getInstance(
			CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new RTArrestWorkerDispatcher(coordinateProvider, getParameters());
		}
		return instance;
	}
	
}