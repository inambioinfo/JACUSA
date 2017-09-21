package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public class UnstrandedCoordinateAdvancer implements CoordinateAdvancer {

	private Coordinate coordinate;

	public UnstrandedCoordinateAdvancer(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public void advance() {
		coordinate.setPosition(getNextPosition());
	}
	
	@Override
	public int getNextPosition() {
		return coordinate.getStart() + 1;
	}
	
	@Override
	public Coordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public void adjustPosition(final int position, STRAND strand) {
		coordinate.setPosition(position);
	}
	
}
