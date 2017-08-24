package jacusa.filter;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.CombinedCountFilter;
import jacusa.filter.storage.DistanceStorage;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;

public abstract class AbstractDistanceFilter<T extends BaseQualData> 
extends AbstractFilter<T> {

	private final int filterDistance;
	private final AbstractCountFilter<T> countFilter;

	private DistanceStorage<T> distanceStorage;
	
	public AbstractDistanceFilter(final char c, 
			final int filterDistance, final double minRatio, final int minCount,
			final AbstractParameters<T> parameters) {
		super(c);
		this.filterDistance	= filterDistance;
		
		countFilter 	= new CombinedCountFilter<T>(minRatio, minCount, parameters);

		distanceStorage = new DistanceStorage<T>(c, filterDistance, parameters.getBaseConfig());
		registerStorage(distanceStorage);
		registerWindowStorage(distanceStorage);
	}

	@Override
	protected boolean filter(final Result<T> result, final WindowIterator<T> windowIterator) {
		final ParallelPileupData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		final Coordinate coordinate = parallelData.getCoordinate();
		final BaseQualData[][] baseQualData = new BaseQualData[parallelData.getConditions()][];
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			baseQualData[conditionIndex] = distanceStorage.getBaseQualData(coordinate, 
					windowIterator.getFilterContainers(conditionIndex, coordinate));
		}
		
		return countFilter.filter(variantBaseIndexs, parallelData, baseQualData);
	}
	
	protected DistanceStorage<T> getDistanceStorage() {
		return distanceStorage;
	}

	public int getFilterDistance() {
		return filterDistance;
	}
	
	@Override
	public void clear() {
		distanceStorage.clear();
	}
	
	@Override
	public int getOverhang() {
		return 0;
	}
	
}
