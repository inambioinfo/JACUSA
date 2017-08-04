package jacusa.pileup.iterator;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.BasePileup;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class OneConditionCallIterator extends WindowIterator<BasePileup> {

	public OneConditionCallIterator(
			final Coordinate coordinate,
			final Variant<BasePileup> filter,
			final SAMFileReader[][] readers, 
			final AbstractParameters<BasePileup> parameters) {
		super(coordinate, filter, readers, parameters);
	}

	@Override
	public boolean hasNext() {
		while (super.hasNext()) {
			BasePileup bp = parallelData.getPooledData(0);
			int[] allelesIs = bp.getAlleles();

			// pick reference base by MD or by majority.
			// all other bases will be converted in pileup2 to refBaseI
			int refBaseIndex = -1;
			//if (bp.getRefBase() != 'N') {
			// FIXME
			if (refBaseIndex == -1) {
				//char refBase = parallelData.getPooledPileup1().getRefBase();
				//refBaseI = BaseConfig.BYTE_BASE2INT_BASE[(byte)refBase];
			} else {
				int maxBaseCount = 0;

				for (int baseIndex : allelesIs) {
					int count = bp.getBaseCount().getBaseCount(baseIndex);
					if (count > maxBaseCount) {
						maxBaseCount = count;
						refBaseIndex = baseIndex;
					}
				}
			}

			int [] tmpVariantBasesIs = new int[allelesIs.length];
			int i = 0;
			for (int j = 0; j < allelesIs.length; ++j) {
				if (allelesIs[j] != refBaseIndex) {
					tmpVariantBasesIs[i] = allelesIs[j];
					++i;
				}
			}
			// int[] variantBasesIs = Arrays.copyOf(tmpVariantBasesIs, i);
			// FIXME parallelData.setPileups2(DefaultPileup.flat(parallelData.getPileups1(), variantBasesIs, refBaseIndex));

			return true;
		}
		
		return false;
	}

}
