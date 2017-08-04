package jacusa.cli.parameters;

import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;

public class CallParameters<T extends Data<T> & hasCoordinate & hasBaseCount & hasRefBase> extends AbstractParameters<T> implements hasStatisticCalculator {

	private StatisticParameters<T> statisticParameters;

	public CallParameters() {
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(getBaseConfig(), 
						statisticParameters));
	}
	
	public CallParameters(int conditions) {
		super(conditions);
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(getBaseConfig(), 
						statisticParameters));
	}

	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}