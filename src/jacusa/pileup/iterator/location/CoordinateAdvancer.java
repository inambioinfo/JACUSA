package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;

public interface CoordinateAdvancer {

	void advance();
	void advance(final int conditionIndex);

	Coordinate get(final int conditionIndex);
	void set(final int conditionIndex, final Coordinate newCoordinate);
	
}
