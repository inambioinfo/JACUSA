package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;

public class CombinedDistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public CombinedDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('I', "Filter distance to TODO position.", 5, 0.5, 1, parameters);
	}

	public CombinedDistanceFilter<T> createFilter() {
		return new CombinedDistanceFilter<T>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				getParameters());
	}

}