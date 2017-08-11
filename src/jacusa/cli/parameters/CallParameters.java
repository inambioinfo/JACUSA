package jacusa.cli.parameters;

import jacusa.data.BaseQualData;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class CallParameters<T extends BaseQualData> 
extends AbstractParameters<T> 
implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameters;

	public CallParameters() {
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(this));
	}
	
	public CallParameters(int conditions) {
		super(conditions);
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(this));
	}

	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}