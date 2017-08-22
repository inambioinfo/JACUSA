package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.AbstractDistanceFilter;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class SpliceSiteDistanceFilterFactory<T extends BaseQualData>
extends AbstractFilterFactory<T> {

	private static final int FILTER_DISTANCE = 6;
	private static final double MIN_RATIO = 0.5;
	private static final int MIN_COUNT = 2;

	private int filterDistance;
	private double minRatio;
	private int minCount;
	
	private AbstractParameters<T> parameters;
	
	public SpliceSiteDistanceFilterFactory(AbstractParameters<T> parameters) {
		super('S', "Filter distance to Splice Site. Default: " + FILTER_DISTANCE + ":" + MIN_RATIO + " (S:distance:min_ratio)");
		this.parameters = parameters;
		filterDistance = FILTER_DISTANCE;
		minRatio = MIN_RATIO;
		minCount = MIN_COUNT;
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

		// format D:distance:minRatio:minCount
		for (int i = 1; i < s.length; ++i) {
			switch(i) {
			case 1:
				final int distance = Integer.valueOf(s[i]);
				if (distance < 0) {
					throw new IllegalArgumentException("Invalid distance " + line);
				}
				this.filterDistance = distance;
				break;

			case 2:
				final double minRatio = Double.valueOf(s[i]);
				if (minRatio < 0.0 || minRatio > 1.0) {
					throw new IllegalArgumentException("Invalid minRatio " + line);
				}
				this.minRatio = minRatio;
				break;
			
			case 3:
				final int minCount = Integer.valueOf(s[i]);
				if (minCount < 0) {
					throw new IllegalArgumentException("Invalid minCount " + line);
				}
				this.minCount = minCount;
				break;
				
			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}

	public SpliceSiteDistanceFilter<T> createFilter() {
		return new SpliceSiteDistanceFilter<T>(getC(), minRatio, minCount, filterDistance, parameters);
	}

}