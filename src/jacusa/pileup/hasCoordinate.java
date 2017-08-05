package jacusa.pileup;

import jacusa.util.Coordinate.STRAND;

public interface hasCoordinate {
	
	String getContig();
	int getPosition();
	int getStart();
	int getEnd();
	STRAND getStrand();
	
	void setContig(final String contig);
	void setPosition(final int position);
	void setStart(final int start);
	void setEnd(final int end);
	void setStrand(final STRAND strand);
	
}
