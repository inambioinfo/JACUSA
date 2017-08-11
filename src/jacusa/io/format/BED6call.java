package jacusa.io.format;

import jacusa.data.BaseQualData;
import jacusa.data.BaseConfig;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.FilterConfig;

public class BED6call extends AbstractOutputFormat<BaseQualData> {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';
	
	protected FilterConfig<BaseQualData> filterConfig;
	protected BaseConfig baseConfig;
	private boolean showReferenceBase;

	public BED6call(
			final char c,
			final String desc,
			final BaseConfig baseConfig, 
			final FilterConfig<BaseQualData> filterConfig,
			final boolean showReferenceBase) {
		super(c, desc);
		
		this.baseConfig = baseConfig;
		this.filterConfig = filterConfig;

		this.showReferenceBase = showReferenceBase;
	}

	public BED6call(
			final BaseConfig baseConfig, 
			final FilterConfig<BaseQualData> filterConfig,
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

	public String convert2String(Result<BaseQualData> result) {
		final ParallelPileupData<BaseQualData> parallelData = result.getParellelData();
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
		sb.append(parallelData.getCombinedPooledData().getStrand().character());

		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addData(sb, parallelData.getData(conditionIndex));
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
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
		}

		return sb.toString();		
	}
	
	/*
	 * Helper function
	 */
	protected void addData(final StringBuilder sb, final BaseQualData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (final BaseQualData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseConfig.BASES[i];
			int baseI = baseConfig.getBaseIndex((byte)b);
			int count = 0;
			if (baseI >= 0) {
				count = d.getBaseQualCount().getBaseCount(baseI);
			}
			sb.append(count);
			++i;
			for (; i < BaseConfig.BASES.length; ++i) {
				b = BaseConfig.BASES[i];
				baseI = baseConfig.getBaseIndex((byte)b);
				count = 0;
				if (baseI >= 0) {
					count = d.getBaseQualCount().getBaseCount(baseI);
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