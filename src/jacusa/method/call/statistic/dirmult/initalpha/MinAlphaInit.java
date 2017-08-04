package jacusa.method.call.statistic.dirmult.initalpha;

import java.util.Arrays;

import jacusa.pileup.BaseConfig;

public class MinAlphaInit<T> extends AbstractAlphaInit<T> {

	public MinAlphaInit() {
		super("min", "alpha = min_k mean(p)");
	}

	@Override
	public AbstractAlphaInit<T> newInstance(String line) {
		return new MinAlphaInit<T>();
	}

	@Override
	public double[] init(
			final int[] baseIs,
			final T[] pileups,
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
