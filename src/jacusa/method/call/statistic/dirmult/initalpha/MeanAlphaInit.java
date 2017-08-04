package jacusa.method.call.statistic.dirmult.initalpha;

import jacusa.pileup.BaseConfig;

public class MeanAlphaInit<T> extends AbstractAlphaInit<T> {

	public MeanAlphaInit() {
		super("mean", "alpha = mean * n * p * q");
	}

	@Override
	public AbstractAlphaInit<T> newInstance(String line) {
		return new MeanAlphaInit<T>();
	}
	
	@Override
	public double[] init(
			final int[] baseIs,
			final T[] pileups,
			final double[][] pileupMatrix) {
		final double[] alpha = new double[BaseConfig.VALID.length];
		final double[] mean = new double[BaseConfig.VALID.length];

		double total = 0.0;
		for (int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			for (int baseI : baseIs) {
				mean[baseI] += pileupMatrix[pileupI][baseI];
				total += pileupMatrix[pileupI][baseI];
			}
		}

		for (int baseI : baseIs) {
			mean[baseI] /= total;
			alpha[baseI] = mean[baseI];
		}

		return alpha;
	}

}
