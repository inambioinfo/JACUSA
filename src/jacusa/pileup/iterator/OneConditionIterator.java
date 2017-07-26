package jacusa.pileup.iterator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.DefaultPileup;
import jacusa.pileup.Pileup;
import jacusa.util.Coordinate;
import jacusa.util.Location;
import net.sf.samtools.SAMFileReader;

public class OneConditionIterator extends AbstractOneConditionIterator {

	public OneConditionIterator(
			final Coordinate annotatedCoordinate,
			final Variant filter,
			final SAMFileReader[] readers, 
			final ConditionParameters condition,
			final AbstractParameters parameters) {
		super(annotatedCoordinate, filter, readers, condition, parameters);
	}

	@Override
	public boolean hasNext() {
		while (hasNext1()) {
			Location location = locationAdvancer.getLocation();
			
			parallelPileup.setContig(coordinate.getSequenceName());
			parallelPileup.setStart(location.genomicPosition);
			parallelPileup.setEnd(parallelPileup.getStart());

			parallelPileup.setStrand(location.strand);
			parallelPileup.setPileups1(getPileups(location, pileupBuilders));
			parallelPileup.setPileups2(new Pileup[0]);
			
			if (filter.isValid(parallelPileup)) {
				int[] allelesIs = parallelPileup.getPooledPileup1().getAlleles();

				// pick reference base by MD or by majority.
				// all other bases will be converted in pileup2 to refBaseI
				int refBaseI = -1;
				if (parallelPileup.getPooledPileup1().getRefBase() != 'N') {
					char refBase = parallelPileup.getPooledPileup1().getRefBase();
					refBaseI = BaseConfig.BYTE_BASE2INT_BASE[(byte)refBase];
				} else {
					int maxBaseCount = 0;

					for (int baseI : allelesIs) {
						int count = parallelPileup.getPooledPileup1().getCounts().getBaseCount(baseI);
						if (count > maxBaseCount) {
							maxBaseCount = count;
							refBaseI = baseI;
						}
					}
				}

				int [] tmpVariantBasesIs = new int[allelesIs.length];
				int i = 0;
				for (int j = 0; j < allelesIs.length; ++j) {
					if (allelesIs[j] != refBaseI) {
						tmpVariantBasesIs[i] = allelesIs[j];
						++i;
					}
				}
				int[] variantBasesIs = Arrays.copyOf(tmpVariantBasesIs, i);
				parallelPileup.setPileups2(DefaultPileup.flat(parallelPileup.getPileups1(), variantBasesIs, refBaseI));
				
				return true;
			} else {
				// reset
				parallelPileup.setPileups1(new Pileup[0]);
				parallelPileup.setPileups2(new Pileup[0]);

				locationAdvancer.advance();
			}
		}
		
		return false;
	}
	
	@Override
	public Location next() {
		Location current = new Location(locationAdvancer.getLocation());;

		// advance to the next position
		locationAdvancer.advance();

		return current;
	}

	@Override
	public int getAlleleCount(Location location) {
		Set<Integer> alleles = new HashSet<Integer>(4); 
		alleles.addAll(getAlleles(location, pileupBuilders));

		return alleles.size();
	}
	
	@Override
	public int getAlleleCount1(Location location) {
		return getAlleleCount(location, pileupBuilders);
	}
	
	@Override
	public int getAlleleCount2(Location location) {
		return 0;
	}
	
}
