package jacusa.pileup.iterator.location;

import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public class CoordinateContainer {

	private CoordinateAdvancer[] coordinateAdvancers;

	public CoordinateContainer(final CoordinateAdvancer[] coordinateAdvancers) {
		this.coordinateAdvancers = coordinateAdvancers;
	}

	public void advance(final Coordinate coordinate) {
		for (int i = 0; i < coordinateAdvancers.length; i++) {
			adjustPosition(i, coordinate.getStart(), coordinate.getStrand());
		}
	}

	public void adjustPosition(final int i, final int position, final STRAND strand) {
		coordinateAdvancers[i].adjustPosition(position, strand);
	}

	public Coordinate getCoordinate(final int i) {
		return coordinateAdvancers[i].getCoordinate();
	}

}

