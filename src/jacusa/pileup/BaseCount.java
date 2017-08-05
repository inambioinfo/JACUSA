package jacusa.pileup;

import java.util.Arrays;

import jacusa.phred2prob.Phred2Prob;

public class BaseCount {

	// container
	private int[] baseCount;
	private int[][] base2qual;
	private int[] minQual;

	public BaseCount(final int baseLength) {
		baseCount 	= new int[baseLength];
		base2qual	= new int[baseLength][Phred2Prob.MAX_Q];
		minQual		= new int[baseLength];
		Arrays.fill(minQual, Phred2Prob.MAX_Q);
	}

	public BaseCount(final int[] baseCount, final int[][] base2qual, int[] minMapq) {
		this(baseCount.length);
		
		System.arraycopy(baseCount, 0, this.baseCount, 0, baseCount.length);
		for (int baseI = 0; baseI < baseCount.length; ++baseI) {
			if (baseCount[baseI] > 0) {
				System.arraycopy(base2qual[baseI], 0, this.base2qual[baseI], 0, base2qual[baseI].length);
			}
		}
		System.arraycopy(minMapq, 0, this.minQual, 0, minMapq.length);
	}
	
	public BaseCount(BaseCount counts) {
		this(counts.baseCount.length);
		
		System.arraycopy(counts.baseCount, 0, this.baseCount, 0, counts.baseCount.length);
		for (int baseI = 0; baseI < counts.baseCount.length; ++baseI) {
			if (counts.baseCount[baseI] > 0) {
				System.arraycopy(counts.base2qual[baseI], 0, base2qual[baseI], 0, counts.base2qual[baseI].length);
			}
		}
		System.arraycopy(counts.minQual,0, minQual, 0, counts.minQual.length);
	}

	public BaseCount copy() {
		return new BaseCount(this);
	}
	
	public int getCoverage() {
		int coverage = 0;
		
		for (int c : baseCount) {
			coverage += c;
		}

		return coverage;
	}

	public int getQualCount(final int base, final int qualI) {
		return base2qual[base][qualI];
	}
	
	public int getBaseCount(final int base) {
		return baseCount[base];
	}
	
	public void add(final BaseCount counts) {
		for (int baseIndex = 0; baseIndex < counts.baseCount.length; ++baseIndex) {
			if (counts.baseCount[baseIndex] > 0) {
				add(baseIndex, counts);
			}
		}
	}

	public void add(final int baseIndex, final BaseCount counts) {
		baseCount[baseIndex] += counts.baseCount[baseIndex];

		for (int qualI = counts.minQual[baseIndex]; qualI < Phred2Prob.MAX_Q ; ++qualI) {
			if (counts.base2qual[baseIndex][qualI] > 0) {
				base2qual[baseIndex][qualI] += counts.base2qual[baseIndex][qualI];
			}
		}
	}

	public void add(final int baseIndex, final int baseIndex2, final BaseCount counts) {
		baseCount[baseIndex] += counts.baseCount[baseIndex2];

		for (int qualIndex = counts.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (counts.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] += counts.base2qual[baseIndex2][qualIndex];
			}
		}
	}
	
	public void substract(final int baseIndex, final BaseCount counts) {
		baseCount[baseIndex] -= counts.baseCount[baseIndex];

		for (int qualIndex = counts.minQual[baseIndex]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (counts.base2qual[baseIndex][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] -= counts.base2qual[baseIndex][qualIndex];
			}
		}
	}

	public void substract(final int baseIndex, final int baseIndex2, final BaseCount counts) {
		baseCount[baseIndex] -= counts.baseCount[baseIndex2];

		for (int qualIndex = counts.minQual[baseIndex2]; qualIndex < Phred2Prob.MAX_Q ; ++qualIndex) {
			if (counts.base2qual[baseIndex2][qualIndex] > 0) {
				base2qual[baseIndex][qualIndex] -= counts.base2qual[baseIndex2][qualIndex];
			}
		}
	}
	
	public void substract(final BaseCount counts) {
		for (int baseIndex = 0; baseIndex < counts.baseCount.length; ++baseIndex) {
			if (baseCount[baseIndex] > 0) {
				substract(baseIndex, counts);
			}
		}
	}

	public void invert() {
		int[] tmpBaseCount = new int[baseCount.length];
		int[][] tmpQualCount = new int[baseCount.length][Phred2Prob.MAX_Q];

		for (int baseIndex = 0; baseIndex < baseCount.length; ++baseIndex) {
			// int complementaryBase = Bases.COMPLEMENT[base];
			// FIXME
			int complementaryBaseI = baseCount.length - baseIndex - 1;  

			// invert base count
			tmpBaseCount[complementaryBaseI] = baseCount[baseIndex];
			// invert qualCount
			tmpQualCount[complementaryBaseI] = base2qual[baseIndex];
		}

		baseCount = tmpBaseCount;
		base2qual = tmpQualCount;
	}

	public int getBaseLength() {
		return baseCount.length;
	}
	
	public int[] getAlleles() {
		// make this allele
		int[] alleles = new int[getBaseLength()];
		int n = 0;
	
		for (int baseIndex = 0; baseIndex < getBaseLength(); ++baseIndex) {
			if (getBaseCount(baseIndex) > 0) {
				alleles[n] = baseIndex;
				++n;
			}
		}
		return Arrays.copyOf(alleles, n);
	}
	
}
