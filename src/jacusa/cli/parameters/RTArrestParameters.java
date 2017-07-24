package jacusa.cli.parameters;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.pileup.builder.UnstrandedRTArrestPileupBuilderFactory;

public class RTArrestParameters extends AbstractParameters implements hasSample2, hasStatisticCalculator {

	private SampleParameters sample2;
	private StatisticParameters statisticParameters;

	public RTArrestParameters() {
		super();

		getSample1().setPileupBuilderFactory(new UnstrandedRTArrestPileupBuilderFactory());
		sample2				= new SampleParameters();
		sample2.setPileupBuilderFactory(new UnstrandedRTArrestPileupBuilderFactory());
		statisticParameters = new StatisticParameters();
	}

	@Override
	public SampleParameters getSample2() {
		return sample2;
	}

	@Override
	public StatisticParameters getStatisticParameters() {
		return statisticParameters;
	}

	@Override
	public AbstractOutputFormat getFormat() {
		return (AbstractOutputFormat)super.getFormat();
	}

}