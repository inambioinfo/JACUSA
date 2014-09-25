package accusa2.estimate;

import accusa2.pileup.Pileup;
import accusa2.process.phred2prob.Phred2Prob;

// posterior estimation
// p(p|D) ~ D(n + alpha) 
public class BayesEstimateParameters extends AbstractEstimateParameters {

	private final double[] initialAlpha;
	
	public BayesEstimateParameters(final double[] alpha, final Phred2Prob phred2Prob) {
		super("bayes", "Bayes estimate (n + alpha)", phred2Prob);
		this.initialAlpha = alpha;
	}

	@Override
	public double[] estimateAlpha(int[] baseIs, Pileup[] pileups) {
		// use initial alpha to init
		final double[] alpha = initialAlpha.clone();

		for (Pileup pileup : pileups) {
			double[] v = phred2Prob.colSum(baseIs, pileup);
			for(int baseI : baseIs) {
				alpha[baseI] += v[baseI];
			}
		}

		return alpha;
	}

	@Override
	public double[] estimateExpectedProb(int[] baseIs, Pileup[] pileups) {
		double[] expectedValue = new double[baseIs.length];

		int replicates = pileups.length;
		double[][] probs = estimateProbs(baseIs, pileups);
		for (int pileupI = 0; pileupI < replicates; ++pileupI) {
			for (int baseI : baseIs) {
				expectedValue[baseI] += probs[pileupI][baseI];
			}
		}
		if (replicates > 1) {
			for (int baseI : baseIs) {
				expectedValue[baseI] /= (double)replicates;
			}
		}

		return expectedValue;
	}

	@Override
	public double[][] estimateProbs(int[] baseIs, Pileup[] pileups) {
		final double[][] probs = new double[pileups.length][baseIs.length];

		for(int pileupI = 0; pileupI < pileups.length; ++pileupI) {
			// sum the probabilities giving alpha 
			probs[pileupI] = phred2Prob.colMean(baseIs, pileups[pileupI]);
		}

		return probs;
	}
}