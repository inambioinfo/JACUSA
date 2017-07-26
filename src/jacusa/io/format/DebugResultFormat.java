package jacusa.io.format;

import jacusa.pileup.BaseConfig;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Pileup;
import jacusa.pileup.Result;

public class DebugResultFormat extends AbstractOutputFormat {

	public static final char CHAR = 'D';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';

	public DebugResultFormat(final BaseConfig baseConfig) {
		super(CHAR, "Debug BED like output");
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
		
		// (1) first condition infos
		addConditionHeader(sb, '1', pathnames1.length);
		sb.append(getSEP());
		// (2) second condition infos
		addConditionHeader(sb, '2', pathnames2.length);
		
		sb.append("reference");
		sb.append(getSEP());
		
		return sb.toString();
	}
	
	private void addConditionHeader(StringBuilder sb, char condition, int replicates) {
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

	private StringBuilder convert2StringHelper(final ParallelPileup parallelPileup, final double value) {
		final StringBuilder sb = new StringBuilder();

		// coordinates
		sb.append(parallelPileup.getContig());
		sb.append(SEP);
		sb.append(parallelPileup.getStart() - 1);
		sb.append(SEP);
		sb.append(parallelPileup.getStart());
		
		sb.append(SEP);
		sb.append("variant");
		
		sb.append(SEP);
		if (Double.isNaN(value)) {
			sb.append("NA");
		} else {
			sb.append(value);
		}

		sb.append(SEP);
		sb.append(parallelPileup.getStrand().character());

		// (1) first pileups
		addPileups(sb, parallelPileup.getPileups1());
		// (2) second pileups
		addPileups(sb, parallelPileup.getPileups2());
		
		sb.append(SEP);
		sb.append(parallelPileup.getPooledPileup().getRefBase());
		
		return sb;
	}
	
	@Override
	public String convert2String(final Result result) {
		final ParallelPileup parallelPileup = result.getParellelPileup();
		final StringBuilder sb = convert2StringHelper(parallelPileup, Double.NaN);
		return sb.toString();		
	}
	
	/*
	 * Helper function
	 */
	private void addPileups(StringBuilder sb, Pileup[] pileups) {
		// output condition: Ax,Cx,Gx,Tx
		for (Pileup pileup : pileups) {
			sb.append(SEP);
			int baseI = 0;
			sb.append(pileup.getCounts().getBaseCount(baseI));
			baseI++;
			for (; baseI < pileup.getCounts().getBaseLength() ; ++baseI) {
				sb.append(SEP2);
				sb.append(pileup.getCounts().getBaseCount(baseI));
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