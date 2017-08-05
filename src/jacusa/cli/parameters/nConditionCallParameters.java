package jacusa.cli.parameters;

import jacusa.io.format.AbstractOutputFormat;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;

public class nConditionCallParameters extends AbstractParameters implements hasStatisticCalculator {

	private StatisticParameters statisticParameters;

	public nConditionCallParameters() {
		super();
		statisticParameters = new StatisticParameters();
		statisticParameters.setStatisticCalculator(new DirichletMultinomialRobustCompoundError(getBaseConfig(), statisticParameters));
	}
	
	public nConditionCallParameters(int n) {
		this();

		for (int i = 0; i < n; i++) {
			conditionParameters.add(new ConditionParameters());
		}
	}

	@Override
	public StatisticParameters getStatisticParameters() {
		return statisticParameters;
	}

	@Override
	public AbstractOutputFormat<?> getFormat() {
		return super.getFormat();
	}

}