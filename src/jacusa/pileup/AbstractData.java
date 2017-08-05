package jacusa.pileup;

import jacusa.util.Coordinate.STRAND;

public abstract class AbstractData<T> implements Data<T>, hasCoordinate {

	private String contig;
	private int start;
	private int end;
	private STRAND strand;
	
	public AbstractData() {
		contig = new String();
		start = -1;
		end = -1;
		strand = STRAND.UNKNOWN;
	}

	public AbstractData(final String contig, 
			final int start, final int end, 
			final STRAND strand) {
		this.contig 	= contig;
		this.start 		= start;
		this.end		= end;
		this.strand		= strand;
	}

	public String getContig() {
		return contig;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getPosition() {
		return start;
	}
	
	public STRAND getStrand() {
		return strand;
	}

	public void setContig(final String contig) {
		this.contig = contig;
	}

	public void setStart(final int start) {
		this.start = start;
	}

	public void setEnd(final int end) {
		this.end = end;
	}

	public void setPosition(final int position) {
		start = position;
		end = position + 1;
	}

	public void setStrand(final STRAND strand) {
		this.strand = strand;
	}

}
