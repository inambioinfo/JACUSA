package jacusa.io.format;

import jacusa.phred2prob.Phred2Prob;

import jacusa.pileup.BaseConfig;
import jacusa.pileup.BasePileup;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.util.Coordinate.STRAND;
import net.sf.samtools.SAMUtils;

public class PileupFormat extends AbstractOutputFormat<BasePileup> {

	public final static char CHAR = 'M';
	public static char EMPTY 	= '*';
	public static char COMMENT = '#';
	public static char SEP 	= '\t';
	public static char SEP2 	= ',';

	private boolean showReferenceBase;
	private BaseConfig baseConfig;

	public PileupFormat(final BaseConfig baseConfig, final boolean showReferenceBase) {
		super(CHAR, "samtools mpileup like format (base columns without: $ ^ < > *)");
		this.showReferenceBase = showReferenceBase;
		this.baseConfig = baseConfig;
	}

	@Override
	public String convert2String(final Result<BasePileup> result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData<BasePileup> parallelPileup = result.getParellelData();

		// coordinates
		sb.append(parallelPileup.getContig());
		sb.append(SEP);
		sb.append(parallelPileup.getStart());

		for (int conditionIndex = 0; conditionIndex < parallelPileup.getConditions(); conditionIndex++) {
			addPileups(sb, parallelPileup.getStrand(conditionIndex), parallelPileup.getData(conditionIndex));
		}

		if (showReferenceBase) {
			sb.append(getSEP());
			// FIXME sb.append(parallelPileup.getCombinedPooledData().getRefBase());
		}

		return sb.toString();		
	}
	
	protected void addPileups(StringBuilder sb, STRAND strand, BasePileup[] pileups) {
		sb.append(SEP);
		sb.append(strand.character());
		
		for(final BasePileup pileup : pileups) {

			sb.append(SEP);
			sb.append(pileup.getCoverage());
			sb.append(SEP);
			
			for (int baseI : pileup.getAlleles()) {
				// print bases 
				for (int i = 0; i < pileup.getBaseCount().getBaseCount(baseI); ++i) {
					sb.append(baseConfig.getBases()[baseI]);
				}
			}

			sb.append(SEP);

			// print quals
			for (int base : pileup.getAlleles()) {
				for (byte qual = 0; qual < Phred2Prob.MAX_Q; ++qual) {

					int count = pileup.getBaseCount().getQualCount(base, qual);
					if (count > 0) {
						// repeat count times
						for (int j = 0; j < count; ++j) {
							sb.append(SAMUtils.phredToFastq(qual));
						}
					}
				}
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