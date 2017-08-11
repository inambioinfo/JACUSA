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

		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			getConditionParameters(conditionIndex).setPileupBuilderFactory(
					new RTArrestPileupBuilderFactory<T>(new UnstrandedPileupBuilderFactory<T>()));
		}
		
		statisticParameters = new StatisticParameters<T>();
	}
	
	public RTArrestParameters() {
		super();

		statisticParameters = new StatisticParameters<T>();
	}

	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
