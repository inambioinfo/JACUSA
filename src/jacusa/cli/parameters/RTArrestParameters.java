package jacusa.cli.parameters;

import jacusa.io.format.AbstractOutputFormat;

public class RTArrestParameters extends AbstractParameters implements hasSample2, hasStatisticCalculator {

	private SampleParameters sample2;
	private StatisticParameters statisticParameters;

	public RTArrestParameters() {
		super();

		sample2				= new SampleParameters();
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