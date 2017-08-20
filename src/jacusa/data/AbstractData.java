package jacusa.data;

import jacusa.util.Coordinate;

/**
 * 
 * @author michael
 *
 * 
 */
public abstract class AbstractData
implements hasCoordinate, hasReferenceBase {

	private Coordinate coordinate;
	private char referenceBase;
	
	public AbstractData() {
		coordinate = new Coordinate();
		referenceBase	= 'N';
	}

	public AbstractData(final AbstractData pileupData) {
		coordinate = new Coordinate(pileupData.getCoordinate());
		this.referenceBase = pileupData.referenceBase; 
	}
	
	public AbstractData(final Coordinate coordinate, final char referenceBase) {
		this.coordinate = new Coordinate(coordinate);
		this.referenceBase 	= referenceBase;
	}
		
	@Override
	public void setReferenceBase(final char referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public char getReferenceBase() {
		return referenceBase;
	}

	public void add(final AbstractData pileupData) {
		referenceBase = pileupData.referenceBase;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public abstract Object copy();
	
}
