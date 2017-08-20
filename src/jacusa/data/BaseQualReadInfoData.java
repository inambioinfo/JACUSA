package jacusa.data;

import jacusa.util.Coordinate;

/**
 * 
 * @author michael
 *
 * 
 */
public class BaseQualReadInfoData
extends BaseQualData
implements hasReadInfoCount {

	private ReadInfoCount readInfoCount;
	
	public BaseQualReadInfoData() {
		super();
		
		readInfoCount = new ReadInfoCount();
	}

	public BaseQualReadInfoData(final BaseQualReadInfoData pileupData) {
		super(pileupData);
		
		this.readInfoCount = pileupData.readInfoCount.copy();
	}
	
	public BaseQualReadInfoData(final Coordinate coordinate, final char referenceBase) {
		super(coordinate, referenceBase);
		
		readInfoCount = new ReadInfoCount();
	}
		
	@Override
	public ReadInfoCount getReadInfoCount() {
		return readInfoCount;
	}
	
	public BaseQualReadInfoData copy() {
		return new BaseQualReadInfoData(this);
	}
	
}
