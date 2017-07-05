package jacusa.method.call.statistic.dirmult.initalpha;

import java.util.Arrays;

import jacusa.pileup.BaseConfig;
import jacusa.pileup.Pileup;

public class MinAlphaInit extends AbstractAlphaInit {

	public MinAlphaInit() {
		super("min", "alpha = min_k mean(p)");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MinAlphaInit();
	}

	@Override
	public double[] init(
			final int[] baseIs,
			final Pileup[] pileups,
			final double[][] pileupMatrix) {
		final double[] alpha = new double[BaseConfig.VALID.length];
		Arrays.fill(alpha, Double.MAX_VALUE);

		double[] pileupCoverages = getCoverages(baseIs, pileupMatrix);

		double[][] pileupProportionMatrix = new double[pileups.length][baseIs.length];
		for (int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			for (int baseI : baseIs) {
				pileupProportionMatrix[pileupI][baseI] = pileupMatrix[pileupI][baseI] / pileupCoverages[pileupI];
				alpha[baseI] = Math.min(alpha[baseI], pileupProportionMatrix[pileupI][baseI]);
			}
		}

		return alpha;
	}
	
}
