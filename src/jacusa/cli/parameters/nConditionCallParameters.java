package jacusa.cli.parameters;


import jacusa.data.BaseQualData;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class nConditionCallParameters<T extends BaseQualData>
extends AbstractParameters<T> 
implements hasStatisticCalculator<T> {

	private final StatisticParameters<T> statisticParameters;

	public nConditionCallParameters() {
		super();
		statisticParameters = new StatisticParameters<T>();
		/* TODO
		statisticParameters.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(this));
				*/
	}
	
	public nConditionCallParameters(final int n) {
		this();

		for (int i = 0; i < n; i++) {
			conditionParameters.add(new ConditionParameters<T>());
		}
	}

	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
