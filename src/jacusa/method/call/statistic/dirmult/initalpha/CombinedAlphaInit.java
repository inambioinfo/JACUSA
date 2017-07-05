package jacusa.method.call.statistic.dirmult.initalpha;

import jacusa.pileup.Pileup;

public class CombinedAlphaInit extends AbstractAlphaInit {

	// initial alpha estimate when replicates > 1 are available
	private AbstractAlphaInit A;
	// initial alpha estimate when NO replicates are available
	private AbstractAlphaInit B;

	protected CombinedAlphaInit(String name, AbstractAlphaInit A, AbstractAlphaInit B) {
		super(name, A.getName() + " + " + B.getName());
		this.A = A;
		this.B = B;
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		
		return new CombinedAlphaInit(getName(), A, B);
	}
	
	@Override
	public double[] init(
			final int[] baseIs,
			final Pileup[] pileups,
			final double[][] pileupMatrix) {

		if (pileups.length == 1) {
			return B.init(baseIs, pileups, pileupMatrix);
		}
		return A.init(baseIs, pileups, pileupMatrix);
	}
		
}
