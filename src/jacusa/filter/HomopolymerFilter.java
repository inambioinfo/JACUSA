package jacusa.filter;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualCount;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.counts.AbstractCountFilter;
import jacusa.filter.counts.MinCountFilter;
import jacusa.filter.storage.HomopolymerStorage;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;

public class HomopolymerFilter<T extends BaseQualData> 
extends AbstractFilter<T> {

	private AbstractCountFilter<T> countFilter;
	private HomopolymerStorage<T> homopolymerStorage;

	public HomopolymerFilter(final char c, final int length, final AbstractParameters<T> parameters) {
		super(c);

		homopolymerStorage = new HomopolymerStorage<T>(c, length, parameters.getBaseConfig()); 
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
			
			// TODO baseQualCounts[conditionIndex] = getBaseQualData(coordinate, windowIterator.getFilterContainers(conditionIndex, coordinate));
		}
		
		return countFilter.filter(variantBaseIndexs, parallelData, baseQualCounts);
	}

	@Override
	public int getOverhang() {
		return 0;
	}

	@Override
	public void clear() {}
	
}
