package jacusa.cli.parameters;

import jacusa.data.BaseQualData;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class PileupParameters<T extends BaseQualData>
extends AbstractParameters<T> {

	public PileupParameters(int conditions) {
		super(conditions);
		
		// set
		for (ConditionParameters<T> condition : getConditionParameters()) {
			condition.setPileupBuilderFactory(new UnstrandedPileupBuilderFactory<T>());
		}
	}

}
