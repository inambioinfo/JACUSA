package jacusa.io.format;

import jacusa.filter.FilterConfig;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Result;

public class BED6OneConditionResultFormat extends BED6ResultFormat {

	private boolean showReferenceBase;
	
	public BED6OneConditionResultFormat(
			final BaseConfig baseConfig, 
			final FilterConfig filterConfig,
			final boolean showReferenceBase) {
		super('B', "Default", baseConfig, filterConfig, showReferenceBase);
		this.showReferenceBase = showReferenceBase;
	}

	@Override
	public String convert2String(Result result) {
		final ParallelPileup parallelPileup = result.getParellelPileup();
		final double statistic = result.getStatistic();
		final StringBuilder sb = new StringBuilder();

		// coordinates
		sb.append(parallelPileup.getContig());
		sb.append(SEP);
		sb.append(parallelPileup.getStart() - 1);
		sb.append(SEP);
		sb.append(parallelPileup.getEnd());
		
		sb.append(SEP);
		sb.append("variant");
		
		sb.append(SEP);
		if (Double.isNaN(statistic)) {
			sb.append("NA");
		} else {
			sb.append(statistic);
		}

		sb.append(SEP);
		sb.append(parallelPileup.getStrand().character());

		// (1) first pileups / actual condition
		addPileups(sb, parallelPileup.getPileups2());
		
		// (2) first pileups / actual condition
		addPileups(sb, parallelPileup.getPileups1());
		
		sb.append(getSEP());
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (filterConfig.hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append(parallelPileup.getPooledPileup().getRefBase());
		}
		
		return sb.toString();		
	}

}
