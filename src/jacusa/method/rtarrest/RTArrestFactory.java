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
import jacusa.cli.options.pileupbuilder.TwoConditionBaseQualDataBuilderOption;

import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.RTArrestParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualReadInfoData;

import jacusa.filter.factory.AbstractFilterFactory;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6call;
import jacusa.io.format.RTArrestResultFormat;

import jacusa.method.AbstractMethodFactory;
import jacusa.method.call.statistic.StatisticCalculator;

import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;


import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class RTArrestFactory 
extends AbstractMethodFactory<BaseQualReadInfoData> {

	public final static String NAME = "rt-arrest";

	private static RTArrestWorkerDispatcher<BaseQualReadInfoData> instance;

	public RTArrestFactory() {
		super(NAME, "Reverse Transcription Arrest - two conditions", 
				new RTArrestParameters<BaseQualReadInfoData>(2));
	}
		
	public void initACOptions() {
		// condition specific setting
		// TODO make for n
		ConditionParameters<BaseQualReadInfoData> condition1 = getParameters().getConditionParameters(1);
		ConditionParameters<BaseQualReadInfoData> condition2 = getParameters().getConditionParameters(2);

		for (int i = 1; i <= 2; ++i) {
			initConditionACOptions(i, condition1);
			initConditionACOptions(i, condition2);
		}
		List<ConditionParameters<BaseQualReadInfoData>> conditions = 
				new ArrayList<ConditionParameters<BaseQualReadInfoData>>(2);
		conditions.add(condition1);
		conditions.add(condition2);
		
		// global settings
		acOptions.add(new MinMAPQOption<BaseQualReadInfoData>(conditions));

		acOptions.add(new MinBASQOption<BaseQualReadInfoData>(conditions));
		acOptions.add(new MinCoverageOption<BaseQualReadInfoData>(conditions));
		acOptions.add(new MaxDepthOption(getParameters()));
		acOptions.add(new FilterFlagOption<BaseQualReadInfoData>(conditions));
		
		acOptions.add(new TwoConditionBaseQualDataBuilderOption<BaseQualReadInfoData>(condition1, condition2));

		acOptions.add(new BedCoordinatesOption(getParameters()));
		acOptions.add(new ResultFileOption(getParameters()));
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getResultFormats().get(a[0]));
		} else {
			getParameters().setFormat(getResultFormats().get(BED6call.CHAR));
			acOptions.add(new FormatOption<BaseQualReadInfoData, AbstractOutputFormat<BaseQualReadInfoData>>(getParameters(), getResultFormats()));
		}

		acOptions.add(new MaxThreadOption(getParameters()));
		acOptions.add(new WindowSizeOption(getParameters()));
		acOptions.add(new ThreadWindowSizeOption(getParameters()));

		if (getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			getParameters().getStatisticParameters().setStatisticCalculator(getStatistics().get(a[0]));
		} else {
			acOptions.add(new StatisticCalculatorOption<BaseQualReadInfoData>(getParameters().getStatisticParameters(), getStatistics()));
		}

		acOptions.add(new FilterModusOption(getParameters()));
		acOptions.add(new FilterConfigOption<BaseQualReadInfoData>(getParameters(), getFilterFactories()));
		
		acOptions.add(new StatisticFilterOption(getParameters().getStatisticParameters()));

 
		acOptions.add(new ShowReferenceOption(getParameters()));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	public Map<String, StatisticCalculator<BaseQualReadInfoData>> getStatistics() {
		Map<String, StatisticCalculator<BaseQualReadInfoData>> statistics = 
				new TreeMap<String, StatisticCalculator<BaseQualReadInfoData>>();
		return statistics;
	}

	public Map<Character, AbstractFilterFactory<BaseQualReadInfoData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<BaseQualReadInfoData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<BaseQualReadInfoData>>();

		List<AbstractFilterFactory<BaseQualReadInfoData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<BaseQualReadInfoData>>(5);

		for (final AbstractFilterFactory<BaseQualReadInfoData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	public Map<Character, AbstractOutputFormat<BaseQualReadInfoData>> getResultFormats() {
		Map<Character, AbstractOutputFormat<BaseQualReadInfoData>> resultFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualReadInfoData>>();

		AbstractOutputFormat<BaseQualReadInfoData> resultFormat = null;

		resultFormat = new RTArrestResultFormat(getParameters().getBaseConfig(), 
				getParameters().getFilterConfig(), getParameters().showReferenceBase());
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	@Override
	public RTArrestParameters<BaseQualReadInfoData> getParameters() {
		return (RTArrestParameters<BaseQualReadInfoData>) super.getParameters();
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

	@Override
	public RTArrestWorkerDispatcher<BaseQualReadInfoData> getInstance(
			CoordinateProvider coordinateProvider) throws IOException {
		if(instance == null) {
			instance = new RTArrestWorkerDispatcher<BaseQualReadInfoData>(coordinateProvider, getParameters());
		}
		return instance;
	}

	@Override
	public BaseQualReadInfoData createDataContainer() {
		return new BaseQualReadInfoData();
	}
	
	@Override
	public BaseQualReadInfoData[] createDataContainer(final int n) {
		return new BaseQualReadInfoData[n];
	}
	
	@Override
	public BaseQualReadInfoData[][] createDataContainer(final int n, final int m) {
		if (m < 0) {
			return new BaseQualReadInfoData[n][];
		}

		return new BaseQualReadInfoData[n][m];
	}

	@Override
	public BaseQualReadInfoData copyDataContainer(final BaseQualReadInfoData dataContainer) {
		return new BaseQualReadInfoData(dataContainer);
	}
	
	@Override
	public BaseQualReadInfoData[] copyDataContainer(final BaseQualReadInfoData[] dataContainer) {
		BaseQualReadInfoData[] ret = createDataContainer(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualReadInfoData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public BaseQualReadInfoData[][] copyDataContainer(final BaseQualReadInfoData[][] dataContainer) {
		BaseQualReadInfoData[][] ret = createDataContainer(dataContainer.length, -1);
		for (int i = 0; i < dataContainer.length; ++i) {
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new BaseQualReadInfoData(dataContainer[i][j]);
			}	
		}
		
		return ret;
	}
	
}
