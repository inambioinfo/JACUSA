package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;

public class ReadPositionDistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public ReadPositionDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('B', "Filter distance to Read Start/End.", 6, 0.5, 2, parameters);
	}

	public ReadPositionDistanceFilter<T> createFilter() {
		return new ReadPositionDistanceFilter<T>(getC(), 
				getFilterDistance(), getFilterMinRatio(), getFilterMinCount(),
				getParameters());
	}

}