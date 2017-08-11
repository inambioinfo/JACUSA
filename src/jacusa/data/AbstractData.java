package jacusa.data;

import jacusa.util.Coordinate.STRAND;

/**
 * 
 * @author michael
 *
 * 
 */
public abstract class AbstractData
implements hasCoordinate, hasReferenceBase {

	private String contig;
	private int start;
	private int end;
	private STRAND strand;
	
	private char referenceBase;
	
	public AbstractData() {
		contig 	= new String();
		start 	= -1;
		end 	= -1;
		strand 	= STRAND.UNKNOWN;
		
		referenceBase	= 'N';
	}

	public AbstractData(final AbstractData pileupData) {
		this.contig = pileupData.contig;
		this.start	= pileupData.start;
		this.end	= pileupData.end;
		this.strand = pileupData.strand;
		
		this.referenceBase = pileupData.referenceBase; 
	}
	
	public AbstractData(final String contig, 
			final int start, final int end, 
			final STRAND strand, final char referenceBase) {
		this.contig 		= contig;
		this.start 			= start;
		this.end			= end;
		this.strand			= strand;

		this.referenceBase 	= referenceBase;
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
	public void setReferenceBase(final char referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public char getReferenceBase() {
		return referenceBase;
	}

	public void add(final AbstractData pileupData) {
		referenceBase = pileupData.referenceBase;
	}
	
	public abstract Object copy();
	
}
