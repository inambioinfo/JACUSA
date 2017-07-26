package jacusa.cli.parameters;

public class TwoConditionPileupParameters extends AbstractParameters implements hasCondition2 {

	private ConditionParameters condition2;

	public TwoConditionPileupParameters() {
		super();

		condition2	= new ConditionParameters();
	}

	@Override
	public ConditionParameters getCondition2() {
		return condition2;
	}


}