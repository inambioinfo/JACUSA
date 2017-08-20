package jacusa.io.format;

import jacusa.JACUSA;
import jacusa.data.BaseQualData;
import jacusa.data.BaseConfig;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.FilterConfig;
import jacusa.filter.factory.AbstractFilterFactory;

import java.util.Calendar;

public class VCFcall extends AbstractOutputFormat<BaseQualData> {

	public static final char CHAR = 'V';
	
	private BaseConfig baseConfig;
	private FilterConfig<BaseQualData> filterConfig;
	
	public VCFcall(final BaseConfig baseConfig, final FilterConfig<BaseQualData> filterConfig) {
		super(CHAR, "VCF Output format. Option -P will be ignored (VCF is unstranded)");
		this.baseConfig = baseConfig;
		this.filterConfig = filterConfig;
	}
	
	@Override
	public String getHeader(String[][] pathnames) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getCOMMENT());
		sb.append("#fileformat=VCFv4.0");
		sb.append('\n');
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		sb.append("##fileDate=");
		sb.append(year);
		if (month < 10)
			sb.append(0);	
		sb.append(month);
		if (day < 10)
			sb.append(0);
		sb.append(day);
		sb.append('\n');

		sb.append("##source=");
		sb.append(JACUSA.NAME + "-" + JACUSA.VERSION);
		sb.append('\n');

		// add filter descriptions to header
		for (final AbstractFilterFactory<BaseQualData> filter : filterConfig.getFactories()) {
			sb.append("##FILTER=<ID=");
			sb.append(filter.getC());
			sb.append(",Description=");
			sb.append("\"" + filter.getDesc() + "\"");
			sb.append('\n');
		}
		sb.append("##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Read Depth\">\n");
		sb.append("##FORMAT=<ID=BC,Number=4,Type=Integer,Description=\"Base counts A,C,G,T\">\n");
		
		String[] cols = {
				"CHROM",
				"POS",
				"ID",
				"REF",
				"ALT",
				"QUAL",
				"FILTER",
				"INFO",
				"FORMAT"
		};
		
		sb.append(getCOMMENT());
		sb.append(cols[0]);
		for (int i = 1; i < cols.length; ++i) {
			sb.append(getSEP());
			sb.append(cols[i]);
		}
		
		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			for (String pathname : pathnames[conditionIndex])  {
				sb.append(getSEP());
				sb.append(pathname);
			}
		}


		return sb.toString();
	}

	@Override
	public String convert2String(Result<BaseQualData> result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelPileupData<BaseQualData> parallelData = result.getParellelData();
		String filterInfo = result.getFilterInfo().combine();
		if (filterInfo == null || filterInfo.length() == 0) {
			filterInfo = "PASS";
		}

		StringBuilder sb2 = new StringBuilder();
		boolean first = true;
		for (int allelIndex : parallelData.getCombinedPooledData().getBaseQualCount().getAlleles()) {
			if (parallelData.getCombinedPooledData().getReferenceBase() != baseConfig.getBases()[allelIndex]) {
				if (! first) {
					sb2.append(',');
				} else {
					first = false;
				}
				sb2.append(baseConfig.getBases()[allelIndex]);
			}
		}

		String[] cols = {
				// contig
				parallelData.getCombinedPooledData().getCoordinate().getSequenceName(),
				// position
				Integer.toString(parallelData.getCombinedPooledData().getCoordinate().getPosition()),
				// ID
				Character.toString(getEMPTY()),
				// REF
				Character.toString(parallelData.getCombinedPooledData().getReferenceBase()),
				// ALT
				sb2.toString(),
				// QUAL
				Character.toString(getEMPTY()),
				// FILTER
				filterInfo,
				// INFO
				".",
				// FORMAT
				"DP" + getSEP3() + "BC",
		};

		sb.append(cols[0]);
		for (int i = 1; i < cols.length; ++i) {
			sb.append(getSEP());
			sb.append(cols[i]);
		}

		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addParallelPileup(sb, parallelData.getData(conditionIndex));
		}
		
		return sb.toString();
	}

	private void addParallelPileup(final StringBuilder sb, final BaseQualData data[]) {
		for (int i = 0; i < data.length; ++i) {
			// add DP
			sb.append(getSEP());
			sb.append(data[i].getBaseQualCount().getCoverage());
			
			sb.append(getSEP3());
			
			// add BC - base counts
			int j = 0;
			char b = BaseConfig.BASES[j];
			int baseIndex = baseConfig.getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = data[i].getBaseQualCount().getBaseCount(baseIndex);
			}
			sb.append(count);
			++j;
			for (; j < BaseConfig.BASES.length; ++j) {
				b = BaseConfig.BASES[j];
				baseIndex = baseConfig.getBaseIndex((byte)b);
				count = 0;
				if (baseIndex >= 0) {
					count = data[i].getBaseQualCount().getBaseCount(baseIndex);
				}
				sb.append(',');
				sb.append(count);
			}
		}
	}
	
	public char getCOMMENT() {
		return '#';
	}

	public char getSEP() {
		return '\t';
	}

	public char getSEP2() {
		return ';';
	}

	public char getSEP3() {
		return ':';
	}
	
	public char getEMPTY() {
		return '.';
	}

}