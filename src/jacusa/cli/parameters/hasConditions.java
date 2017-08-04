package jacusa.cli.parameters;

public interface hasConditions {

	ConditionParameters[] getConditionParameters();
	ConditionParameters getConditionParameters(int conditionIndex);
	int getConditions();
	int getReplicates(int conditionIndex);

}