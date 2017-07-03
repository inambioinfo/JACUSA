package jacusa.method.rcoverage.statistic;

import jacusa.cli.parameters.StatisticParameters;
import jacusa.estimate.MinkaEstimateDirMultParameters;

import jacusa.pileup.Pileup;

public class BetaMultinomial extends AbstractDirichletStatistic {

	protected double estimatedError = 0.01;
	protected double priorError = 0d;

	private StatisticParameters parameters;
	
	public BetaMultinomial(final StatisticParameters parameters) {
		super(new MinkaEstimateDirMultParameters(), parameters);
	}

	@Override
	public BetaMultinomial newInstance() {
		return new BetaMultinomial(parameters);
	}
	
	@Override
	public String getName() {
		return "BetaMult";
	}

	@Override
	public String getDescription() {
		return "Beta-Multinomial";  
	}

	@Override
	protected void populate(Pileup pileup, double[] pileupErrorVector,
			double[] pileupVector) {
		// TODO Auto-generated method stub
		
	}

}