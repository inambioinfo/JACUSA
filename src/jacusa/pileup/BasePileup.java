package jacusa.pileup;

import jacusa.util.Coordinate.STRAND;

import java.util.Arrays;

public class BasePileup extends AbstractData<BasePileup> implements hasBaseCount, hasRefBase {

	private BaseCount baseCount;

	private char refBase;
	
	public BasePileup(final int bases) {
		super();
		
		baseCount 	= new BaseCount(bases);
		refBase		= 'N';
	}

	public BasePileup(final String contig, 
			final int position, 
			final STRAND strand, 
			final int baseLength) {
		this(baseLength);
	}

	public BasePileup(final BasePileup data) {
		super(data.getContig(),
				data.getStart(),
				data.getEnd(),
				data.getStrand());
		
		baseCount	= new BaseCount(data.getBaseCount());
		refBase		= data.refBase;
	}

	public BaseCount getBaseCount() {
		return baseCount;
	}

	public void setBaseCount(BaseCount baseCount) {
		this.baseCount = baseCount;
	}
	
	/*
	@Override
	public void add(final BaseCountData data) {
		baseCounts.addCounts(pileup.getCounts());

		this.readStartCount += pileup.getReadStartCount();
		this.readEndCount += pileup.getReadEndCount();
	}

	@Override
	public void substractPileup(final Pileup pileup) {
		baseCounts.substract(pileup.getCounts());

		this.readStartCount -= pileup.getReadStartCount();
		this.readEndCount -= pileup.getReadEndCount();
	}*/

	@Override
	public int getCoverage() {
		return baseCount.getCoverage();
	}

	public int[] getAlleles() {
		// make this allele
		int[] alleles = new int[baseCount.getBaseLength()];
		int n = 0;
	
		for (int baseI = 0; baseI < baseCount.getBaseLength(); ++baseI) {
			if (baseCount.getBaseCount(baseI) > 0) {
				alleles[n] = baseI;
				++n;
			}
		}
		return Arrays.copyOf(alleles, n);
	}

	public void invertStrand() {
		switch (getStrand()) {
		case FORWARD:
			setStrand(STRAND.REVERSE);
			break;

		case REVERSE:
			setStrand(STRAND.FORWARD);
			break;
			
		case UNKNOWN:
			return;
		}
	}

	
	@Override
	public void setRefBase(final char refBase) {
		this.refBase = refBase;
	}

	@Override
	public char getRefBase() {
		return refBase;
	}
	
	@Override
	public void add(BasePileup data) {
		baseCount.add(data.getBaseCount());
	}

	@Override
	public BasePileup copy() {
		return new BasePileup(this);
	}

	
	
}