package jacusa.pileup.iterator.location;

import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public class StrandedCoordinateAdvancer implements CoordinateAdvancer {

	protected boolean[] isStranded; 
	protected Coordinate[] coordinates;
	
	public StrandedCoordinateAdvancer(final boolean[] isStranded, final Coordinate[] coordinates) {
		this.isStranded = isStranded;
		this.coordinates = coordinates;
	}
	
	public Coordinate get(final int conditionIndex) {
		return coordinates[conditionIndex];
	}
	
	public void advance() {
		for (int conditionIndex = 0; conditionIndex < coordinates.length; conditionIndex++) {
			advance(conditionIndex);
		}
	}
	
	public void advance(final int conditionIndex) {
		if (isStranded[conditionIndex]) {
			if (coordinates[conditionIndex].getStrand() == STRAND.FORWARD) {
				coordinates[conditionIndex].setStrand(STRAND.REVERSE);
			} else {
				coordinates[conditionIndex].setStrand(STRAND.FORWARD);
				final int currentPosition = coordinates[conditionIndex].getPosition() + 1;
				coordinates[conditionIndex].setPosition(currentPosition);
			}
		} else {
			final int currentPosition = coordinates[conditionIndex].getPosition() + 1;
			coordinates[conditionIndex].setPosition(currentPosition);
		}
	}
	
	public void set(final int conditionIndex, final Coordinate newCoorindate) {
		coordinates[conditionIndex].setPosition(newCoorindate.getPosition());
		if (isStranded[conditionIndex]) {
			coordinates[conditionIndex].setStrand(newCoorindate.getStrand());
		}
	}

	public Coordinate get() {
		return coordinates[WindowIterator.CONDITON_INDEX];
	}

	public boolean[] getIsStranded() {
		return isStranded;
	}
	
	public Coordinate[] getCoordinates() {
		return coordinates;
	}
	
}
