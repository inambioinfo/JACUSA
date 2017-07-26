package jacusa.cli.parameters;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;

public class RTArrestParameters extends AbstractParameters implements hasCondition2, hasStatisticCalculator {

	private ConditionParameters condition2;
	private StatisticParameters statisticParameters;

	public RTArrestParameters() {
		super();

		getCondition1().setPileupBuilderFactory(new RTArrestPileupBuilderFactory(new UnstrandedPileupBuilderFactory()));
		condition2				= new ConditionParameters();
		condition2.setPileupBuilderFactory(new RTArrestPileupBuilderFactory(new UnstrandedPileupBuilderFactory()));
		statisticParameters = new StatisticParameters();
	}

	@Override
	public ConditionParameters getCondition2() {
		return condition2;
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