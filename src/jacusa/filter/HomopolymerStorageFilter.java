package jacusa.filter;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualCount;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.MinCountFilter;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;

public class HomopolymerStorageFilter<T extends BaseQualData> 
extends AbstractWindowStorageFilter<T> {

	private AbstractCountFilter<T> countFilter;
	
	public HomopolymerStorageFilter(final char c, final AbstractParameters<T> parameters) {
		super(c);

		countFilter = new MinCountFilter<T>(c, 1, parameters);
	}

	@Override
	protected boolean filter(final Result<T> result, final WindowIterator<T> windowIterator) {
		final ParallelPileupData<T> parallelData = result.getParellelData();

		final int[] variantBaseIndexs = countFilter.getVariantBaseIndexs(parallelData);
		if (variantBaseIndexs.length == 0) {
			return false;
		}

		final Coordinate coordinate = result.getParellelData().getCoordinate();
		BaseQualCount[][] baseQualCounts = new BaseQualCount[parallelData.getConditions()][];
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			
			baseQualCounts[conditionIndex] = getBaseQualData(coordinate, windowIterator.getFilterContainers(conditionIndex, coordinate));
		}
		
		return countFilter.filter(variantBaseIndexs, parallelData, baseQualCounts);
	}
	
}
