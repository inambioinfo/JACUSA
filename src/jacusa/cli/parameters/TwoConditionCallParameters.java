package jacusa.cli.parameters;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;

public class TwoConditionCallParameters extends AbstractParameters implements hasCondition2, hasStatisticCalculator {
	private ConditionParameters condition2;
	private StatisticParameters statisticParameters;

	public TwoConditionCallParameters() {
		super();

		condition2			= new ConditionParameters();
		statisticParameters = new StatisticParameters();
		statisticParameters.setStatisticCalculator(new DirichletMultinomialRobustCompoundError(getBaseConfig(), statisticParameters));
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