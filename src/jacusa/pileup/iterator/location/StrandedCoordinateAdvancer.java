package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public class StrandedCoordinateAdvancer implements CoordinateAdvancer {

	protected Coordinate coordinate;
	
	public StrandedCoordinateAdvancer(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public void advance() {
		if (coordinate.getStrand() == STRAND.FORWARD) {
			coordinate.setStrand(STRAND.REVERSE);
		} else {
			coordinate.setStrand(STRAND.FORWARD);
			final int currentPosition = coordinate.getStart() + 1;
			coordinate.setPosition(currentPosition);
		}
	}

	@Override
	public void adjustPosition(final int position, final STRAND strand) {
		this.coordinate.setPosition(position);
		this.coordinate.setStrand(strand);
	}

	@Override
	public int getNextPosition() {
		if (coordinate.getStrand() == STRAND.FORWARD) {
			return coordinate.getStart();
		} else {
			return coordinate.getStart() + 1;
		}
	}
	
	public void set(final Coordinate newCoorindate) {
		coordinate = newCoorindate;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}
	
}
