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
import jacusa.cli.options.VersionOption;
import jacusa.cli.options.WindowSizeOption;
import jacusa.cli.options.pileupbuilder.TwoSamplePileupBuilderOption;
import jacusa.cli.options.sample.InvertStrandOption;
import jacusa.cli.options.sample.MaxDepthSampleOption;
import jacusa.cli.options.sample.MinBASQSampleOption;
import jacusa.cli.options.sample.MinCoverageSampleOption;
import jacusa.cli.options.sample.MinMAPQSampleOption;
import jacusa.cli.options.sample.filter.FilterFlagOption;
import jacusa.cli.options.sample.filter.FilterNHsamTagOption;
import jacusa.cli.options.sample.filter.FilterNMsamTagOption;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.CLI;
import jacusa.cli.parameters.SampleParameters;
import jacusa.cli.parameters.TwoSamplePileupParameters;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.DistanceFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactors;
import jacusa.filter.factory.MinDifferenceFilterFactory;
import jacusa.filter.factory.RareEventFilterFactory;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.ReadPositionalBiasFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.io.format.BED6ResultFormat;
import jacusa.io.format.DebugResultFormat;
// import jacusa.io.format.ConsensusOutputFormat;
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

public class TwoSamplePileupFactory extends AbstractMethodFactory {

	private static MpileupWorkerDispatcher instance;
	private TwoSamplePileupParameters parameters;

	public TwoSamplePileupFactory() {
		super("pileup", "SAMtools like mpileup for two samples");

		parameters = new TwoSamplePileupParameters();
	}

	protected void initSampleACOptions(int sample, SampleParameters sampleParameters) {
		acOptions.add(new MinMAPQSampleOption(sample, sampleParameters));
		acOptions.add(new MinBASQSampleOption(sample, sampleParameters));
		acOptions.add(new MinCoverageSampleOption(sample, sampleParameters));
		acOptions.add(new MaxDepthSampleOption(sample, sampleParameters, parameters));
		acOptions.add(new FilterNHsamTagOption(sample, sampleParameters));
		acOptions.add(new FilterNMsamTagOption(sample, sampleParameters));
		acOptions.add(new InvertStrandOption(sample, sampleParameters));
	}
	
	public void initACOptions() {
		SampleParameters sample1 = parameters.getSample1();
		SampleParameters sample2 = parameters.getSample2();

		for (int sampleI = 1; sampleI <= 2; ++sampleI) {
			initSampleACOptions(sampleI, sample1);
			initSampleACOptions(sampleI, sample2);
		}
		SampleParameters[] samples = new SampleParameters[] {
			sample1, sample2
		};

		// global settings
		acOptions.add(new MinMAPQOption(samples));
		acOptions.add(new MinBASQOption(samples));
		acOptions.add(new MinCoverageOption(samples));
		acOptions.add(new MaxDepthOption(parameters));
		acOptions.add(new FilterFlagOption(samples));
		
		acOptions.add(new TwoSamplePileupBuilderOption(sample1, sample2));

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
		acOptions.add(new VersionOption(CLI.getSingleton()));
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
//				new BASQBiasFilterFactory(parameters),
//				new MAPQBiasFilterFactory(parameters),
//				new OutlierFilterFactory(parameters.getStatisticParameters()),
//				new ZeroCountFilterFactory(parameters.getStatisticParameters()),
				new DistanceFilterFactory(parameters),
				new INDEL_DistanceFilterFactory(parameters),
				new ReadPositionDistanceFilterFactory(parameters),
				new SpliceSiteDistanceFilterFactory(parameters),
				new HomozygousFilterFactory(parameters),
				new MaxAlleleCountFilterFactors(parameters),
				new HomopolymerFilterFactory(parameters),
				new RareEventFilterFactory(parameters),
				new MinDifferenceFilterFactory(parameters)
//				new FDRFilterFactory(parameters.getStatisticParameters()),
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
		String[] pathnames1 = parameters.getSample1().getPathnames();
		String[] pathnames2 = parameters.getSample2().getPathnames();

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

		SAMPathnameArg pa = new SAMPathnameArg(1, parameters.getSample1());
		pa.processArg(args[0]);
		pa = new SAMPathnameArg(2, parameters.getSample2());
		pa.processArg(args[1]);

		return true;
	}
	
}
