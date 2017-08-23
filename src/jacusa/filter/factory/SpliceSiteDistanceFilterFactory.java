package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class SpliceSiteDistanceFilterFactory<T extends BaseQualData>
extends AbstractDistanceFilterFactory<T> {

	public SpliceSiteDistanceFilterFactory(final AbstractParameters<T> parameters) {
		super('S', "Filter distance to Splice Site.", 6, 0.5, 2, parameters);
	}

	public SpliceSiteDistanceFilter<T> createFilter() {
		return new SpliceSiteDistanceFilter<T>(getC(), 
				getFilterDistance(),getFilterMinRatio(), getFilterDistance(), 
				getParameters());
	}

}