package jacusa.pileup.builder;

import java.util.Arrays;

import jacusa.data.BaseQualCount;
import jacusa.phred2prob.Phred2Prob;
import jacusa.util.WindowCoordinates;

public class WindowCache {

	private WindowCoordinates windowCoordinates;
	private final int bases;

	private int[] coverage;
	private int[][] baseCount;
	private int[][][] qualCount;

	private byte[] reference;
	
	private int[][] minQual;
	
	private int[] alleleCount;
	private int[] alleleMask;

	private int windowSize;

	public WindowCache(final WindowCoordinates windowCoordinates, final int bases) {
		this.windowCoordinates 	= windowCoordinates;
		this.bases 				= bases;

		windowSize	= windowCoordinates.getWindowSize();
		coverage = new int[windowSize];
		baseCount = new int[windowSize][bases];
		qualCount = new int[windowSize][bases][Phred2Prob.MAX_Q];

		reference = new byte[windowSize]; 
		
		minQual	= new int[windowSize][bases];;
		
		alleleCount = new int[windowSize];
		alleleMask = new int[windowSize];
	}

	public void clear() {
		Arrays.fill(coverage, 0);
		Arrays.fill(reference, (byte)'N');
		for (int windowPositionI = 0; windowPositionI < windowSize; ++windowPositionI) {
			Arrays.fill(baseCount[windowPositionI], 0);
			for (int baseI = 0; baseI < bases; ++baseI) {
				Arrays.fill(qualCount[windowPositionI][baseI], 0);
			}
			Arrays.fill(minQual[windowPositionI], 20);
		}

		Arrays.fill(alleleCount, 0);
		Arrays.fill(alleleMask, 0);
	}

	public void addReferenceBase(final int windowPosition, final byte referenceBase) {
		reference[windowPosition] = referenceBase;
	}
	
	public byte getReferenceBase(final int windowPosition) {
		return reference[windowPosition];
	}
	
	public void addHighQualityBaseCall(final int windowPosition, final int baseIndex, final int qual) {
		// make sure we don't exceed...
		Math.min(Phred2Prob.MAX_Q - 1, qual);
		++coverage[windowPosition];
		++baseCount[windowPosition][baseIndex];
		++qualCount[windowPosition][baseIndex][qual];

		int r = 2 << baseIndex;
		int t = alleleMask[windowPosition] & r;
		if (t == 0) {
			alleleMask[windowPosition] += r;
			++alleleCount[windowPosition];
		}
	}

	// only count for alleles
	public void addLowQualityBaseCall(final int windowPosition, final int baseIndex, final int qual) {
		int r = 2 << baseIndex;
		int t = alleleMask[windowPosition] & r;
		if (t == 0) {
			alleleMask[windowPosition] += r;
			++alleleCount[windowPosition];
		}
		minQual[windowPosition][baseIndex] = Math.min(minQual[windowPosition][baseIndex], qual);
	}

	public int getCoverage(final int windowPosition) {
		return coverage[windowPosition];
	}

	public int getAlleleCount(final int windowPosition) {
		return alleleCount[windowPosition];
	}

	public int getAlleleMask(final int windowPosition) {
		return alleleMask[windowPosition];
	}
	
	public int[] getAlleles(int windowPosition) {
		int alleles[] = new int[getAlleleCount(windowPosition)];
		int mask = getAlleleMask(windowPosition);

		int i = 0;
		for (int baseI = 0; baseI < bases; ++baseI) {
			if ((mask & 2 << baseI) > 0) {
				alleles[i] = baseI;
				++i;
			}
		}
		
		return alleles;
	}

	public BaseQualCount getBaseCount(final int windowPosition) {
		return new BaseQualCount(baseCount[windowPosition], qualCount[windowPosition], minQual[windowPosition]);
	}
	
	public WindowCoordinates getWindowCoordinates() {
		return windowCoordinates;
	}

	public int getWindowSize() {
		return windowSize;
	}
	
	public byte[] getReference() {
		return reference;
	}

}