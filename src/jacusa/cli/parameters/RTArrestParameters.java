package jacusa.cli.parameters;

import jacusa.data.BaseQualReadInfoData;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;

public class RTArrestParameters<T extends BaseQualReadInfoData>
extends AbstractParameters<T> 
implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameters;

	public RTArrestParameters(int conditions) {
		super(conditions);

		// set
		for (ConditionParameters<T> condition : getConditionParameters()) {
			condition.setPileupBuilderFactory(new RTArrestPileupBuilderFactory<T>(new UnstrandedPileupBuilderFactory<T>()));
		}
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(new BetaBinomial<T>());
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
