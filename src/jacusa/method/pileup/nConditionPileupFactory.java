package jacusa.method.pileup;

import jacusa.cli.options.BaseConfigOption;
import jacusa.cli.options.BedCoordinatesOption;
import jacusa.cli.options.FilterConfigOption;
import jacusa.cli.options.FormatOption;
import jacusa.cli.options.HelpOption;
import jacusa.cli.options.MaxDepthOption;
import jacusa.cli.options.MaxThreadOption;
import jacusa.cli.options.MinBASQOption;
import jacusa.cli.options.MinCoverageOption;
import jacusa.cli.options.MinMAPQOption;
import jacusa.cli.options.ResultFileOption;
import jacusa.cli.options.ShowReferenceOption;
import jacusa.cli.options.ThreadWindowSizeOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.condition.InvertStrandOption;
import jacusa.cli.options.condition.MaxDepthConditionOption;
import jacusa.cli.options.condition.MinBASQConditionOption;
import jacusa.cli.options.condition.MinCoverageConditionOption;
import jacusa.cli.options.condition.MinMAPQConditionOption;
import jacusa.cli.options.condition.filter.FilterFlagOption;
import jacusa.cli.options.condition.filter.FilterNHsamTagOption;
import jacusa.cli.options.condition.filter.FilterNMsamTagOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.PileupParameters;
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
import jacusa.io.format.PileupFormat;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class nConditionPileupFactory 
extends AbstractMethodFactory<BaseQualData> {

	private static MpileupWorkerDispatcher<BaseQualData> instance;
	
	public nConditionPileupFactory(int conditions) {
		super("pileup", "SAMtools like mpileup", 
				new PileupParameters<BaseQualData>(conditions));
	}

	public nConditionPileupFactory() {
		super("pileup", "SAMtools like mpileup", 
				new PileupParameters<BaseQualData>(0));
	}
	
	protected void initConditionACOptions(int conditionIndex, ConditionParameters<BaseQualData> condition) {
		acOptions.add(new MinMAPQConditionOption(conditionIndex, condition));
		acOptions.add(new MinBASQConditionOption(conditionIndex, condition));
		acOptions.add(new MinCoverageConditionOption(conditionIndex, condition));
		acOptions.add(new MaxDepthConditionOption(conditionIndex, condition));
		acOptions.add(new FilterNHsamTagOption(conditionIndex, condition));
		acOptions.add(new FilterNMsamTagOption(conditionIndex, condition));
		acOptions.add(new InvertStrandOption(conditionIndex, condition));
	}
	
	public void initACOptions() {
		ConditionParameters<BaseQualData> condition1 = getParameters().getConditionParameters(0);
		ConditionParameters<BaseQualData> condition2 = getParameters().getConditionParameters(1);

		for (int conditionIndex = 1; conditionIndex <= 2; ++conditionIndex) {
			initConditionACOptions(conditionIndex, condition1);
			initConditionACOptions(conditionIndex, condition2);
		}
		List<ConditionParameters<BaseQualData>> conditions = 
				new ArrayList<ConditionParameters<BaseQualData>>(2);
		conditions.add(condition1);
		conditions.add(condition2);

		// global settings
		acOptions.add(new MinMAPQOption<BaseQualData>(conditions));
		acOptions.add(new MinBASQOption<BaseQualData>(conditions));
		acOptions.add(new MinCoverageOption<BaseQualData>(conditions));
		acOptions.add(new MaxDepthOption(getParameters()));
		acOptions.add(new FilterFlagOption<BaseQualData>(conditions));
		
		// FIXME acOptions.add(new nConditionBaseQualDataBuilderOption(conditions));

		acOptions.add(new BedCoordinatesOption(getParameters()));
		acOptions.add(new ResultFileOption(getParameters()));
		
		if (getOuptutFormats().size() == 1 ) {
			Character[] a = getOuptutFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getOuptutFormats().get(a[0]));
		} else {
			getParameters().setFormat(getOuptutFormats().get(PileupFormat.CHAR));
			acOptions.add(new FormatOption<BaseQualData,AbstractOutputFormat<BaseQualData>>(getParameters(), getOuptutFormats()));
		}

		acOptions.add(new BaseConfigOption(getParameters()));
		acOptions.add(new FilterConfigOption<BaseQualData>(getParameters(), getFilterFactories()));
		acOptions.add(new WindowSizeOption(getParameters()));
		acOptions.add(new ThreadWindowSizeOption(getParameters()));

		acOptions.add(new MaxThreadOption(getParameters()));
		// acOptions.add(new DebugOption(parameters));
		acOptions.add(new ShowReferenceOption(getParameters()));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	public Map<Character, AbstractOutputFormat<BaseQualData>> getOuptutFormats() {
		final Map<Character, AbstractOutputFormat<BaseQualData>> outputFormats = 
				new HashMap<Character, AbstractOutputFormat<BaseQualData>>();

		AbstractOutputFormat<BaseQualData> outputFormat = 
				new PileupFormat(getParameters().getBaseConfig(), getParameters().showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new BED6call(getParameters().getBaseConfig(), getParameters().getFilterConfig(), getParameters().showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		/*
		outputFormat = new ConsensusOutputFormat(parameters.getBaseConfig(), parameters.getFilterConfig());
		outputFormats.put(outputFormat.getC(), outputFormat);
		*/
		
		return outputFormats;
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
	
	@Override
	public MpileupWorkerDispatcher<BaseQualData> getInstance(CoordinateProvider coordinateProvider) {
		if(instance == null) {
			instance = new MpileupWorkerDispatcher<BaseQualData>(coordinateProvider, getParameters());
		}

		return instance;
	}

	@Override
	public PileupParameters<BaseQualData> getParameters() {
		return (PileupParameters<BaseQualData>) super.getParameters();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
	}

	/* (non-Javadoc)
	 * @see jacusa.method.AbstractMethodFactory#getDataContainer()
	 */
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
