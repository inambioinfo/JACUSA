package jacusa.util;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class Coordinate {

	public static final char STRAND_FORWARD_CHAR = '+';
	public static final char STRAND_REVERSE_CHAR = '-';
	public static final char STRAND_UNKNOWN_CHAR = '.';
	
	private String sequenceName;
	private int start;
	private int end;
	private STRAND strand;

	public Coordinate() {
		sequenceName 	= new String();
		start 			= -1;
		end 			= -1;
		strand 			= STRAND.UNKNOWN;
	}

	public Coordinate(final Coordinate coordinate) {
		sequenceName 	= new String(coordinate.sequenceName);
		start 			= coordinate.start;
		end 			= coordinate.end;
		strand			= coordinate.strand;
	}
	
	public Coordinate(final String sequenceName, 
			final int start, final int end) {
		this(sequenceName, start, end, STRAND.UNKNOWN);
	}
	
	public Coordinate(final String sequenceName, 
			final int start, final STRAND strand) {
		this(sequenceName, start, start + 1, strand);
	}
	
	public Coordinate(final String sequenceName, 
			final int start, final int end, STRAND strand) {
		this.sequenceName 	= sequenceName;
		this.start 			= start;
		this.end 			= end;
		this.strand			= strand;
	}
	
	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(final String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public int getStart() {
		return start;
	}

	public void setStart(final int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(final int end) {
		this.end = end;
	}

	public void setPosition(final int position) {
		start = position;
		end = position + 1;
	}
	
	public int getPosition() {
		return start;
	}
	
	public String toString() {
		return sequenceName + "_" + start + "-" + end;
	}
	
	public STRAND getStrand() {
		return strand;
	}
	
	public void setStrand(final STRAND strand) {
		this.strand = strand;
	}
	
	public static STRAND invertStrand(final STRAND strand) {
		switch (strand) {
		case FORWARD:
			return STRAND.REVERSE;

		case REVERSE:
			return STRAND.FORWARD;
	
		case UNKNOWN:
			return STRAND.UNKNOWN;
			
		}
			
		return STRAND.UNKNOWN;
	}
	
	public enum STRAND {
		FORWARD(STRAND_FORWARD_CHAR),REVERSE(STRAND_REVERSE_CHAR),UNKNOWN(STRAND_UNKNOWN_CHAR);
		
		final char c;
		final int i;
		
		private STRAND(char c) {
			this.c = c;
			
			switch(c) {

			case STRAND_FORWARD_CHAR:
				i = 2;
				break;

			case STRAND_REVERSE_CHAR:
				i = 1;
				break;

			default:
				i = 0;
				break;
			}
		}

		public final char character() {
	        return c;
	    }

		public final int integer() {
			return i;
		}

	}
	
}
