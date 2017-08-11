package jacusa.data;

import jacusa.util.Coordinate.STRAND;

/**
 * 
 * @author michael
 *
 * 
 */
public class PileupData 
implements hasCoordinate, hasBaseQualCount, hasReadInfoCount, hasReferenceBase {

	private String contig;
	private int start;
	private int end;
	private STRAND strand;
	
	private BaseQualCount baseQualCount;
	private ReadInfoCount readInfoCount;
	private char referenceBase;
	
	public PileupData() {
		contig 	= new String();
		start 	= -1;
		end 	= -1;
		strand 	= STRAND.UNKNOWN;
		
		baseQualCount 	= new BaseQualCount();
		readInfoCount	= new ReadInfoCount();
		referenceBase	= 'N';
	}

	public PileupData(final PileupData pileupData) {
		this.contig = pileupData.contig;
		this.start	= pileupData.start;
		this.end	= pileupData.end;
		this.strand = pileupData.strand;
		
		this.baseQualCount = pileupData.baseQualCount.copy();
		this.readInfoCount = pileupData.readInfoCount.copy();
		this.referenceBase = pileupData.referenceBase; 
	}
	
	public PileupData(final String contig, 
			final int start, final int end, 
			final STRAND strand, final int bases, final char referenceBase) {
		this.contig 		= contig;
		this.start 			= start;
		this.end			= end;
		this.strand			= strand;
		this.referenceBase 	= referenceBase;
		
		baseQualCount		= new BaseQualCount();
		readInfoCount		= new ReadInfoCount();
	}
	
	@Override
	public String getContig() {
		return contig;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public int getPosition() {
		return start;
	}
	
	@Override
	public STRAND getStrand() {
		return strand;
	}

	@Override
	public void setContig(final String contig) {
		this.contig = contig;
	}

	@Override
	public void setStart(final int start) {
		this.start = start;
	}

	@Override
	public void setEnd(final int end) {
		this.end = end;
	}

	@Override
	public void setPosition(final int position) {
		start = position;
		end = position + 1;
	}

	@Override
	public void setStrand(final STRAND strand) {
		this.strand = strand;
	}
	
	@Override
	public BaseQualCount getBaseQualCount() {
		return baseQualCount;
	}

	@Override
	public void setBaseQualCount(BaseQualCount baseQualCount) {
		this.baseQualCount = baseQualCount;
	}

	// TODO do we need this?
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
	public ReadInfoCount getReadInfoCount() {
		return readInfoCount;
	}
	
	@Override
	public void setReferenceBase(final char referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public char getReferenceBase() {
		return referenceBase;
	}

	public void add(final PileupData pileupData) {
		baseQualCount.add(pileupData.baseQualCount);
		readInfoCount.add(pileupData.readInfoCount);
		referenceBase = pileupData.referenceBase;
	}
	
	public PileupData copy() {
		return new PileupData(this);
	}
	
}
