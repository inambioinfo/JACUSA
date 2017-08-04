package jacusa.method.call.statistic.dirmult;

import jacusa.cli.parameters.StatisticParameters;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.Data;
import jacusa.pileup.DefaultParallelData;
import jacusa.pileup.ParallelData;
import jacusa.pileup.hasBaseCount;

public class DirichletMultinomialRobustCompoundError<T extends Data<T> & hasBaseCount> extends DirichletMultinomialCompoundError<T> {

	public DirichletMultinomialRobustCompoundError(final BaseConfig baseConfig, final StatisticParameters parameters) {
		super(baseConfig, parameters);
	}

	@Override
	public String getName() {
		return "DirMult-RCE";
	}

	@Override
	public String getDescription() {
		return "Robust Compound Err.";  
	}

	@Override
	public double getStatistic(final ParallelData<T> parallelData) {
		/* 
		 * check if any sample is homomorph and
		 * replace this sample with the other sample and make it homomorph 
		 */
		
		// determine the number of alleles per sample: 1, 2, and P 
		int a1 = parallelData.getPooledData(0).getBaseCount().getAlleles().length;
		int a2 = parallelData.getPooledData(1).getBaseCount().getAlleles().length;
		// all observed alleles
		int[] alleles = parallelData.getCombinedPooledData().getBaseCount().getAlleles();
		int aP = alleles.length;

		// get bases that are different between the samples
		int[] variantBaseIs = DefaultParallelData.getVariantBaseIndexs(parallelData);
		// if there are no variant bases than both samples are heteromorph; 
		// use existing parallelPileup to calculate test-statistic
		if (variantBaseIs.length == 0) {
			return super.getStatistic(parallelData);
		}

		// determine common base (shared by both conditions)
		int commonBaseI = -1;
		for (int baseI : alleles) {
			int count1 = parallelData.getPooledData(0).getBaseCount().getBaseCount(baseI);
			int count2 = parallelData.getPooledData(1).getBaseCount().getBaseCount(baseI);
			if (count1 > 0 && count2  > 0) {
				commonBaseI = baseI;
				break;
			}
		}

		@SuppressWarnings("unchecked")
		T[][] data = (T[][]) new Object[parallelData.getConditions()][];
		// container for adjusted parallelPileup
		ParallelData<T> adjustedParallelPileup = null;
		// determine which condition has the variant base
		if (a1 > 1 && a2 == 1 && aP == 2) { // condition1
			
			System.arraycopy(data[0], 0, parallelData.getData(0), 0, parallelData.getData(0).length);
			System.arraycopy(data[1], 0, parallelData.getData(0), 0, parallelData.getData(0).length);

			adjustedParallelPileup = new DefaultParallelData<T>(
					parallelData.getContig(),
					parallelData.getStart(),
					parallelData.getEnd(),
					parallelData.getStrand(),
					data);
			// and replace pileups2 with pileups1 where the variant bases have been replaced with the common base
			adjustedParallelPileup.setData(0, DefaultParallelData.flat(adjustedParallelPileup.getData(0), variantBaseIs, commonBaseI));
		} else if (a2 > 1 && a1 == 1 && aP == 2) { // condition2
			
			System.arraycopy(data[0], 0, parallelData.getData(1), 0, parallelData.getData(1).length);
			System.arraycopy(data[1], 0, parallelData.getData(1), 0, parallelData.getData(1).length);
			
			adjustedParallelPileup = new DefaultParallelData<T>(
					parallelData.getContig(),
					parallelData.getStart(),
					parallelData.getEnd(),
					parallelData.getStrand(),
					data);
			adjustedParallelPileup.setData(1, DefaultParallelData.flat(adjustedParallelPileup.getData(1), variantBaseIs, commonBaseI));
		}
		// aP > 3, just use the existing parallelPileup to calculate the test-statistic
		if (adjustedParallelPileup == null) { 
			return super.getStatistic(parallelData);
		}
		
		return super.getStatistic(adjustedParallelPileup);
	}

}