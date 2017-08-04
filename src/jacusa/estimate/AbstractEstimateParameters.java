package jacusa.estimate;

import jacusa.phred2prob.Phred2Prob;
import jacusa.pileup.hasBaseCount;

public abstract class AbstractEstimateParameters {

	private final String name;
	private final String desc;
	protected final Phred2Prob phred2Prob;

	public AbstractEstimateParameters(final String name, final String desc, final Phred2Prob phred2Prob) {
		this.name = name;
		this.desc = desc;
		this.phred2Prob = phred2Prob;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public Phred2Prob getPhred2Prob() {
		return phred2Prob;
	}

	public abstract double[] estimateAlpha(int[] baseIs, hasBaseCount[] pileups);
	public abstract double[][] probabilityMatrix(int[] baseIs, hasBaseCount[] pileups);

}