package jacusa.cli.parameters;

import jacusa.pileup.BaseReadPileup;
import jacusa.pileup.Data;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasReadInfoCount;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;

public class RTArrestParameters<T extends Data<T> & hasReadInfoCount & hasCoordinate & hasBaseCount & hasRefBase> extends AbstractParameters<T> implements hasStatisticCalculator {

	private StatisticParameters<BaseReadPileup> statisticParameters;

	public RTArrestParameters(int conditions) {
		super(conditions);

		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			getConditionParameters(conditionIndex).setPileupBuilderFactory(
					new RTArrestPileupBuilderFactory<T>(new UnstrandedPileupBuilderFactory<T>()));
		}
		
		statisticParameters = new StatisticParameters<BaseReadPileup>();
	}
	
	public RTArrestParameters() {
		super();

		statisticParameters = new StatisticParameters<BaseReadPileup>();
	}

	@Override
	public StatisticParameters<BaseReadPileup> getStatisticParameters() {
		return statisticParameters;
	}

}