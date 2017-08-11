package jacusa.cli.parameters;

import java.util.List;

import jacusa.data.AbstractData;

public interface hasConditions<T extends AbstractData> {

	List<ConditionParameters<T>> getConditionParameters();
	ConditionParameters<T> getConditionParameters(int conditionIndex);
	int getConditions();
	int getReplicates(int conditionIndex);

}
