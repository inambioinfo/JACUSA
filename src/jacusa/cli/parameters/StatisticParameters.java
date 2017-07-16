package jacusa.cli.parameters;

import jacusa.method.call.statistic.StatisticCalculator;

public class StatisticParameters {

	// filter: statistic
	private StatisticCalculator statisticCalculator;
	private double threshold;
	
	public StatisticParameters() {
		threshold = Double.NaN;
	}

	/**
	 * @return the maxStat
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the stat to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	/**
	 * @return the statisticCalculator
	 */
	public StatisticCalculator getStatisticCalculator() {
		return statisticCalculator;
	}

	/**
	 * @param statisticCalculator the statisticCalculator to set
	 */
	public void setStatisticCalculator(StatisticCalculator statisticCalculator) {
		this.statisticCalculator = statisticCalculator;
	}

}