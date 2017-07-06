package jacusa.cli.parameters;

import jacusa.io.format.AbstractOutputFormat;

// TODO change name
public class ReverseTranscriptionArrestParameters extends AbstractParameters implements hasSample2, hasStatisticCalculator {

	private SampleParameters sample2;
	private StatisticParameters statisticParameters;

	public ReverseTranscriptionArrestParameters() {
		super();

		sample2				= new SampleParameters();
		statisticParameters = new StatisticParameters();
		// TODO choose default
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