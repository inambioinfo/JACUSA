package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.hasCondition2;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.MaxDepthStorageFilter;
import jacusa.filter.storage.DummyFilterFillCache;
import jacusa.util.WindowCoordinates;

public class MaxDepthFilterFactory extends AbstractFilterFactory<Void> {

	public static final char C = 'd';
	
	private AbstractParameters parameters;
	
	public MaxDepthFilterFactory(AbstractParameters parameters) {
		super(C, "Filter sites with condition1 coverage >= ");
		desc += parameters.getCondition1().getMaxDepth();
		if (parameters instanceof hasCondition2) {
			desc += " or condition2 coverage >= " + ((hasCondition2)parameters).getCondition2().getMaxDepth();
		}

		this.parameters = parameters;
	}

	@Override
	public DummyFilterFillCache createFilterStorage(final WindowCoordinates windowCoordinates, final ConditionParameters condition) {
		// storage is not needed - done 
		// Low Quality Base Calls are stored in AbstractBuilder 
		return new DummyFilterFillCache(getC());
	}

	@Override
	public AbstractStorageFilter<Void> createStorageFilter() {
		if (parameters instanceof hasCondition2) {
			return new MaxDepthStorageFilter(getC(), parameters.getCondition1(), ((hasCondition2)parameters).getCondition2());
		}
		return new MaxDepthStorageFilter(getC(), parameters.getCondition1(), null);
	}
	
}