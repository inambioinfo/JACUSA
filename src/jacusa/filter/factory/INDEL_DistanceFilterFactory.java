package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.AbstractDistanceFilter;

public class INDEL_DistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public INDEL_DistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('I', "Filter distance to INDEL position.", 6, 0.2, 2, parameters);
	}

	public INDEL_DistanceFilter<T> createFilter() {
		return new INDEL_DistanceFilter<T>(getC(), getMin, minCount, distance, parameters);
	}

}