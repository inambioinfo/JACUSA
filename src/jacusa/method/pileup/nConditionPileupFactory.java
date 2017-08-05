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
import jacusa.cli.options.pileupbuilder.TwoConditionPileupBuilderOption;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.PileupParameters;
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
import jacusa.io.format.PileupFormat;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.BasePileup;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

public class nConditionPileupFactory extends AbstractMethodFactory<BasePileup> {

	private static MpileupWorkerDispatcher instance;
	
	public nConditionPileupFactory(int conditions) {
		super("pileup", "SAMtools like mpileup", new PileupParameters(conditions));
	}

	protected void initConditionACOptions(int conditionIndex, ConditionParameters condition) {
		acOptions.add(new MinMAPQConditionOption(conditionIndex, condition));
		acOptions.add(new MinBASQConditionOption(conditionIndex, condition));
		acOptions.add(new MinCoverageConditionOption(conditionIndex, condition));
		acOptions.add(new MaxDepthConditionOption(conditionIndex, condition, getParameters()));
		acOptions.add(new FilterNHsamTagOption(conditionIndex, condition));
		acOptions.add(new FilterNMsamTagOption(conditionIndex, condition));
		acOptions.add(new InvertStrandOption(conditionIndex, condition));
	}
	
	public void initACOptions() {
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
		
		if (getOuptutFormats().size() == 1 ) {
			Character[] a = getOuptutFormats().keySet().toArray(new Character[1]);
			getParameters().setFormat(getOuptutFormats().get(a[0]));
		} else {
			getParameters().setFormat(getOuptutFormats().get(PileupFormat.CHAR));
			acOptions.add(new FormatOption<AbstractOutputFormat<BasePileup>>(getParameters(), getOuptutFormats()));
		}

		acOptions.add(new BaseConfigOption(getParameters()));
		acOptions.add(new FilterConfigOption(getParameters(), getFilterFactories()));
		acOptions.add(new WindowSizeOption(getParameters()));
		acOptions.add(new ThreadWindowSizeOption(getParameters()));

		acOptions.add(new MaxThreadOption(getParameters()));
		// acOptions.add(new DebugOption(parameters));
		acOptions.add(new ShowReferenceOption(getParameters()));
		acOptions.add(new HelpOption(CLI.getSingleton()));
	}

	public Map<Character, AbstractOutputFormat<BasePileup>> getOuptutFormats() {
		Map<Character, AbstractOutputFormat<BasePileup>> outputFormats = new HashMap<Character, AbstractOutputFormat<BasePileup>>();

		AbstractOutputFormat<BasePileup> outputFormat = new PileupFormat(getParameters().getBaseConfig(), getParameters().showReferenceBase());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		outputFormat = new BED6callFormat<BasePileup>(getParameters().getBaseConfig(), getParameters().getFilterConfig(), getParameters().showReferenceBase());
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
				new DistanceFilterFactory(getParameters()),
				new INDEL_DistanceFilterFactory(getParameters()),
				new ReadPositionDistanceFilterFactory(getParameters()),
				new SpliceSiteDistanceFilterFactory(getParameters()),
				new HomozygousFilterFactory(getParameters()),
				new MaxAlleleCountFilterFactors(getParameters()),
				new HomopolymerFilterFactory(getParameters())
		};
		for (AbstractFilterFactory<?> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}
	
	@Override
	public MpileupWorkerDispatcher getInstance(CoordinateProvider coordinateProvider) {
		if(instance == null) {
			instance = new MpileupWorkerDispatcher(coordinateProvider, getParameters());
		}

		return instance;
	}

	@Override
	public PileupParameters getParameters() {
		return (PileupParameters) super.getParameters();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
	}
	
}
