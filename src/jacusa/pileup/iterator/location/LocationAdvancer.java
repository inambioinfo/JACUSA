package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate.STRAND;
import jacusa.util.Location;

public class LocationAdvancer {

	protected boolean[] isStranded; 
	protected Location[] locs;
	
	public LocationAdvancer(final boolean[] isStranded, final Location[] locs) {
		this.isStranded = isStranded;
		this.locs = locs;
	}
	
	public Location getLocation(final int conditionIndex) {
		return locs[conditionIndex];
	}
	
	public void advance() {
		for (int conditionIndex = 0; conditionIndex < locs.length; conditionIndex++) {
			final Location loc = locs[conditionIndex]; 
			if (isStranded[conditionIndex]) {
				if (loc.strand == STRAND.FORWARD) {
					loc.strand = STRAND.REVERSE;
					return;
				} else {
					loc.strand = STRAND.FORWARD;

					++loc.genomicPosition;
				}
			} else {
				++loc.genomicPosition;
			}
		}
	}

	public void advanceLocation() {
		for (final Location loc : locs) {
			loc.genomicPosition += 1;
		}
	}

	public void advanceLocation(final Location loc) {
		for (int conditionIndex = 0; conditionIndex < locs.length; conditionIndex++) {
			if (locs[conditionIndex] == loc) {
				advanceLocation(conditionIndex);
				return;
			}
		}
	}
	
	public void advanceLocation(final int conditionIndex) {
		if (isStranded[conditionIndex]) {
			strandedAdvanceLocation(locs[conditionIndex]);
		} else {
			locs[conditionIndex].genomicPosition++;
		}
	}
	
	// FIXME
	public  void setLocation(final int conditionIndex, Location loc) {
		/*
		loc1.genomicPosition = loc2.genomicPosition;
		loc2.strand = STRAND.FORWARD;
		loc1.strand = loc2.strand;
		*/
	}

	// FIXME
	public Location getLocation() {
		return null;
	}

	// FIXME
	public boolean checkStrand() {
		return false;
	}
	
	protected void strandedAdvanceLocation(Location location) {
		switch (location.strand) {
		case FORWARD:
			location.strand = STRAND.REVERSE;
			break;

		case REVERSE:
			++location.genomicPosition;
			location.strand = STRAND.FORWARD;
			break;

		case UNKNOWN:
		default:
			throw new IllegalArgumentException("Strand in location: " + location + " cannot be unknown!");
		}
	}
	
	public boolean[] getIsStranded() {
		return isStranded;
	}
	
	public Location[] getLocations() {
		return locs;
	}
	
}
