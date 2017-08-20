package jacusa.data;

import jacusa.util.Coordinate;

/**
 * 
 * @author michael
 *
 * 
 */
public class BaseQualData 
extends AbstractData
implements hasBaseQualCount {

	private BaseQualCount baseQualCount;
	
	public BaseQualData() {
		super();
		
		baseQualCount 	= new BaseQualCount();
	}

	public BaseQualData(final BaseQualData pileupData) {
		super(pileupData);
		this.baseQualCount = pileupData.baseQualCount.copy();
	}
	
	public BaseQualData(final Coordinate coordinate, final char referenceBase) {
		super(coordinate, referenceBase);
		
		baseQualCount		= new BaseQualCount();
	}
		
	@Override
	public BaseQualCount getBaseQualCount() {
		return baseQualCount;
	}

	@Override
	public void setBaseQualCount(BaseQualCount baseQualCount) {
		this.baseQualCount = baseQualCount;
	}

	/* TODO
	public void invertStrand() {
		switch (getStrand()) {
		case FORWARD:
			setStrand(STRAND.REVERSE);
			break;

		case REVERSE:
			setStrand(STRAND.FORWARD);
			break;
			
		case UNKNOWN:
			return;
		}
	}
	*/
	
	public BaseQualData copy() {
		return new BaseQualData(this);
	}
	
}
