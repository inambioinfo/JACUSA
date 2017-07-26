package jacusa.io.format;

import jacusa.filter.FilterConfig;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Pileup;
import jacusa.pileup.Result;

public class BED6ResultFormat extends AbstractOutputFormat {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';
	
	protected FilterConfig filterConfig;
	protected BaseConfig baseConfig;
	private boolean showReferenceBase;

	public BED6ResultFormat(
			final char c,
			final String desc,
			final BaseConfig baseConfig, 
			final FilterConfig filterConfig,
			final boolean showReferenceBase) {
		super(c, desc);
		
		this.baseConfig = baseConfig;
		this.filterConfig = filterConfig;

		this.showReferenceBase = showReferenceBase;
	}

	public BED6ResultFormat(
			final BaseConfig baseConfig, 
			final FilterConfig filterConfig,
			final boolean showReferenceBase) {
		this(CHAR, "Default", baseConfig, filterConfig, showReferenceBase);
	}

	@Override
	public String getHeader(String[] pathnames1, String[] pathnames2) {
		final StringBuilder sb = new StringBuilder();

		sb.append(COMMENT);

		// position (0-based)
		sb.append("contig");
		sb.append(getSEP());
		sb.append("start");
		sb.append(getSEP());
		sb.append("end");
		sb.append(getSEP());

		sb.append("name");
		sb.append(getSEP());

		// stat	
		sb.append("stat");
		sb.append(getSEP());
		
		sb.append("strand");
		sb.append(getSEP());
		
		// (1) first condition  infos
		addConditionHeader(sb, '1', pathnames1.length);
		sb.append(getSEP());
		// (2) second condition  infos
		addConditionHeader(sb, '2', pathnames2.length);

		sb.append(getSEP());
		sb.append("info");
		
		// add filtering info
		if (filterConfig.hasFiters()) {
			sb.append(getSEP());
			sb.append("filter_info");
		}

		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append("refBase");
		}
		
		return sb.toString();
	}
	
	protected void addConditionHeader(StringBuilder sb, char condition, int replicates) {
		sb.append("bases");
		sb.append(condition);
		sb.append(1);
		if (replicates == 1) {
			return;
		}
		
		for (int i = 2; i <= replicates; ++i) {
			sb.append(SEP);
			sb.append("bases");
			sb.append(condition);
			sb.append(i);
		}
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

		// (1) first pileups
		addPileups(sb, parallelPileup.getPileups1());
		// (2) second pileups
		addPileups(sb, parallelPileup.getPileups2());

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
	
	/*
	 * Helper function
	 */
	protected void addPileups(StringBuilder sb, Pileup[] pileups) {
		// output condition: Ax,Cx,Gx,Tx
		for (Pileup pileup : pileups) {
			sb.append(SEP);

			int i = 0;
			char b = BaseConfig.VALID[i];
			int baseI = baseConfig.getBaseI((byte)b);
			int count = 0;
			if (baseI >= 0) {
				count = pileup.getCounts().getBaseCount(baseI);
			}
			sb.append(count);
			++i;
			for (; i < BaseConfig.VALID.length; ++i) {
				b = BaseConfig.VALID[i];
				baseI = baseConfig.getBaseI((byte)b);
				count = 0;
				if (baseI >= 0) {
					count = pileup.getCounts().getBaseCount(baseI);
				}
				sb.append(SEP2);
				sb.append(count);
			}
		}
	}
	
	public char getCOMMENT() {
		return COMMENT;
	}

	public char getEMPTY() {
		return EMPTY;
	}

	public char getSEP() {
		return SEP;
	}
	
	public char getSEP2() {
		return SEP2;
	}

}