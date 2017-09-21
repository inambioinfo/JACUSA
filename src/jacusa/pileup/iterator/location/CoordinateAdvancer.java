package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public interface CoordinateAdvancer {

	void advance();
	int getNextPosition();
	
	Coordinate getCoordinate();
	void adjustPosition(final int position, final STRAND strand);

}
