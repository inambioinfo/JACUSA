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
import jacusa.cli.options.SAMPathnameArg;
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
import jacusa.cli.options.pileupbuilder.TwoConditionPileupBuilderOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.TwoConditionPileupParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.DistanceFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactors;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.ReadPositionalBiasFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6ResultFormat;
import jacusa.io.format.DebugResultFormat;
import jacusa.io.format.PileupFormat;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.SAMCoordinateProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;

import net.sf.samtools.SAMSequenceRecord;

public class TwoConditionPileupFactory extends AbstractMethodFactory {

	private static MpileupWorkerDispatcher instance;
	private TwoConditionPileupParameters parameters;

	public TwoConditionPileupFactory() {
		super("pileup", "SAMtools like mpileup for two conditions");

		parameters = new TwoConditionPileupParameters();
	}

	protected void initConditionACOptions(int conditionIndex, ConditionParameters condition) {
		acOptions.add(new MinMAPQConditionOption(conditionIndex, condition));
		acOptions.add(new MinBASQConditionOption(conditionIndex, condition));
		acOptions.add(new MinCoverageConditionOption(conditionIndex, condition));
		acOptions.add(new MaxDepthConditionOption(conditionIndex, condition, parameters));
		acOptions.add(new FilterNHsamTagOption(conditionIndex, condition));
		acOptions.add(new FilterNMsamTagOption(conditionIndex, condition));
		acOptions.add(new InvertStrandOption(conditionIndex, condition));
	}
	
	public void initACOptions() {
		ConditionParameters condition1 = parameters.getCondition1();
		ConditionParameters condition2 = parameters.getCondition2();

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
		acOptions.add(new MaxDepthOption(parameters));
		acOptions.add(new FilterFlagOption(conditions));
		
		acOptions.add(new TwoConditionPileupBuilderOption(parameters, condition1, condition2));

		acOptions.add(new BedCoordinatesOption(parameters));
		acOptions.add(new ResultFileOption(parameters));
		
		if (getOuptutFormats().size() == 1 ) {
			Character[] a = getOuptutFormats().keySet().toArray(new Character[1]);
			parameters.setFormat(getOuptutFormats().get(a[0]));
		} else {
			parameters.setFormat(getOuptutFormats().get(PileupFormat.CHAR));
			acOptions.add(new FormatOption<AbstractOutputFormat>(parameters, getOuptutFormats()));
		}

		acOptions.add(new BaseConfigOption(parameters));
		acOptions.add(new FilterConfigOption(parameters, getFilterFactories()));
		acOptions.add(new WindowSizeOption(parameters));
		acOptions.add(new ThreadWindowSizeOption(parameters));

		acOptions.add(new MaxThreadOption(parameters));
		// acOptions.add(new DebugOption(parameters));
		acOptions.add(new ShowReferenceOption(parameters));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	public Map<Character, AbstractOutputFormat> getOuptutFormats() {
		Map<Character, AbstractOutputFormat> outputFormats = new HashMap<Character, AbstractOutputFormat>();

		AbstractOutputFormat outputFormat = new PileupFormat(parameters.getBaseConfig(), parameters.showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new BED6ResultFormat(parameters.getBaseConfig(), parameters.getFilterConfig(), parameters.showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new DebugResultFormat(parameters.getBaseConfig());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		/*
		outputFormat = new ConsensusOutputFormat(parameters.getBaseConfig(), parameters.getFilterConfig());
		outputFormats.put(outputFormat.getC(), outputFormat);
		*/
		
		return outputFormats;
	}

	public Map<Character, AbstractFilterFactory<?>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<?>> abstractPileupFilters = new HashMap<Character, AbstractFilterFactory<?>>();

		AbstractFilterFactory<?>[] filterFactories = new AbstractFilterFactory[] {
				new ReadPositionalBiasFilterFactory(parameters),
				new DistanceFilterFactory(parameters),
				new INDEL_DistanceFilterFactory(parameters),
				new ReadPositionDistanceFilterFactory(parameters),
				new SpliceSiteDistanceFilterFactory(parameters),
				new HomozygousFilterFactory(parameters),
				new MaxAlleleCountFilterFactors(parameters),
				new HomopolymerFilterFactory(parameters)
		};
		for (AbstractFilterFactory<?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}
	
	@Override
	public MpileupWorkerDispatcher getInstance(String[] pathnames1, String[] pathnames2, CoordinateProvider coordinateProvider) {
		if(instance == null) {
			instance = new MpileupWorkerDispatcher(pathnames1, pathnames2, coordinateProvider, parameters);
		}

		return instance;
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
	
}
