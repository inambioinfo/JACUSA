package jacusa.pileup;

import jacusa.util.Info;

/**
 * 
 * @author Michael Piechotta
 */
public class Result {

	private ParallelPileup parallelPileup;
	private double statistic;
	private Info filterInfo;
	private Info resultInfo;
	
	public Result() {
		parallelPileup = null;
		statistic = Double.NaN;
		filterInfo = new Info();
		resultInfo = new Info();
	}
	
	/**
	 * 
	 * @param parallelPileup
	 */
	public void setParellelPileup(ParallelPileup parallelPileup) {
		this.parallelPileup = parallelPileup;
	}
	
	/**
	 * 
	 * @return
	 */
	public ParallelPileup getParellelPileup() {
		return parallelPileup;
	}

	/**
	 * 
	 * @param statistic
	 */
	public void setStatistic(double statistic) {
		this.statistic = statistic;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getStatistic() {
		return statistic;
	}

	/**
	 * 
	 * @return
	 */
	public Info getResultInfo() {
		return resultInfo;
	}
	
	/**
	 * 
	 * @return
	 */
	public Info getFilterInfo() {
		return filterInfo;
	}

}