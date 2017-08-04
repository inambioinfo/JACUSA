package jacusa.io.format;

import jacusa.filter.FilterConfig;
import jacusa.pileup.BaseConfig;
import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasRefBase;

public class BED6callFormat<T extends Data<T> & hasBaseCount & hasRefBase> extends AbstractOutputFormat<T> {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';
	
	protected FilterConfig filterConfig;
	protected BaseConfig baseConfig;
	private boolean showReferenceBase;

	public BED6callFormat(
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

	public BED6callFormat(
			final BaseConfig baseConfig, 
			final FilterConfig filterConfig,
			final boolean showReferenceBase) {
		this(CHAR, "Default", baseConfig, filterConfig, showReferenceBase);
	}

	@Override
	public String getHeader(String[][] pathnames) {
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
		
		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			addConditionHeader(sb, conditionIndex, pathnames[conditionIndex].length);
			sb.append(getSEP());
		}
		
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
	
	protected void addConditionHeader(StringBuilder sb, int condition, int replicates) {
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

	public String convert2String(Result<T> result) {
		final ParallelData<T> parallelData = result.getParellelData();
		final double statistic = result.getStatistic();
		final StringBuilder sb = new StringBuilder();

		// coordinates
		sb.append(parallelData.getContig());
		sb.append(SEP);
		sb.append(parallelData.getStart() - 1);
		sb.append(SEP);
		sb.append(parallelData.getEnd());
		
		sb.append(SEP);
		sb.append("variant");
		
		sb.append(SEP);
		if (Double.isNaN(statistic)) {
			sb.append("NA");
		} else {
			sb.append(statistic);
		}

		sb.append(SEP);
		sb.append(parallelData.getStrand().character());

		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addPileups(sb, parallelData.getData(conditionIndex));
		}

		sb.append(getSEP());
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (filterConfig.hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append(parallelData.getCombinedPooledData().getRefBase());
		}

		return sb.toString();		
	}
	
	/*
	 * Helper function
	 */
	protected void addPileups(final StringBuilder sb, final T[] pileups) {
		// output condition: Ax,Cx,Gx,Tx
		for (final T pileup : pileups) {
			sb.append(SEP);

			int i = 0;
			char b = BaseConfig.VALID[i];
			int baseI = baseConfig.getBaseI((byte)b);
			int count = 0;
			if (baseI >= 0) {
				count = pileup.getBaseCount().getBaseCount(baseI);
			}
			sb.append(count);
			++i;
			for (; i < BaseConfig.VALID.length; ++i) {
				b = BaseConfig.VALID[i];
				baseI = baseConfig.getBaseI((byte)b);
				count = 0;
				if (baseI >= 0) {
					count = pileup.getBaseCount().getBaseCount(baseI);
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