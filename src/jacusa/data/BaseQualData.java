package jacusa.data;

import jacusa.util.Coordinate;

public class BaseQualData
extends AbstractData
implements hasBaseQualCount {

	private BaseQualCount baseQualCount;
	private char referenceBase;

	public BaseQualData() {
		super();

		baseQualCount 	= new BaseQualCount();
		referenceBase	= 'N';
	}

	public BaseQualData(final BaseQualData pileupData) {
		super(pileupData);
		this.baseQualCount = pileupData.getBaseQualCount().copy();
		this.referenceBase = pileupData.getReferenceBase();
	}
	
	public BaseQualData(final Coordinate coordinate, final char referenceBase) {
		super(coordinate);
		
		baseQualCount		= new BaseQualCount();
		this.referenceBase	= referenceBase;
	}
		
	@Override
	public BaseQualCount getBaseQualCount() {
		return baseQualCount;
	}

	@Override
	public void setBaseQualCount(BaseQualCount baseQualCount) {
		this.baseQualCount = baseQualCount;
	}

	@Override
	public void setReferenceBase(final char referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public char getReferenceBase() {
		return referenceBase;
	}

	public void add(AbstractData abstractData) {
		BaseQualData baseQualData = (BaseQualData) abstractData;
		baseQualCount.add(baseQualData.getBaseQualCount());
		this.referenceBase = baseQualData.getReferenceBase();
	}
	
	@Override
	public BaseQualData copy() {
		return new BaseQualData(this);
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
	
}
