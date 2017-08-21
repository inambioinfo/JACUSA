package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;

public class UnstrandedCoordinateAdvancer implements CoordinateAdvancer {

	private Coordinate[] coordinates;

	public UnstrandedCoordinateAdvancer(final Coordinate[] coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public void advance() {
		for (int conditionIndex = 0; conditionIndex < coordinates.length; conditionIndex++) {
			advance(conditionIndex); 
		}
	}

	@Override
	public void advance(final int conditionIndex) {
		coordinates[conditionIndex].setPosition(
				coordinates[conditionIndex].getPosition() + 1);
	}

	@Override
	public Coordinate get(final int conditionIndex) {
		return coordinates[conditionIndex];
	}

	@Override
	public void set(final int conditionIndex, final Coordinate newCoordinate) {
		coordinates[conditionIndex] = newCoordinate;
	}
	
}
