package jacusa.filter.factory;

import java.util.HashSet;
import java.util.Set;

import net.sf.samtools.CigarOperator;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.AbstractDistanceFilter;
import jacusa.filter.storage.DistanceStorage;
import jacusa.util.WindowCoordinates;

public class ReadPositionDistanceFilterFactory<T extends BaseQualData> 
extends AbstractDistanceFilterFactory<T> {

	public ReadPositionDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('B', "Filter distance to Read Start/End.", 6, 0.5, 2, parameters);
	}

	public ReadPositionDistanceFilter<T> createFilter() {
		return new ReadPositionDistance<T>(getC(), get, minCount, filterDistance, parameters);
	}

}