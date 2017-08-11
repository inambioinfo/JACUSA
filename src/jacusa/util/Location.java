package jacusa.util;

import jacusa.util.Coordinate.STRAND;

public class Location {

	public String contig;
	public int genomicPosition;
	public STRAND strand;

	public Location(final String contig, final int genomicPosition, final STRAND strand) {
		this.contig 			= contig;
		this.genomicPosition 	= genomicPosition;
		this.strand 			= strand;
	}

	public Location(final Location location) {
		this(location.contig, location.genomicPosition, location.strand);
	}

	@Override
	public String toString() {
		return contig + "_" + genomicPosition + "-" + (genomicPosition + 1);
	}

}