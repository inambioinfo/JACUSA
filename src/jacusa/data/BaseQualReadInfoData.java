package jacusa.data;

import jacusa.util.Coordinate;

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
	
	public BaseQualReadInfoData(final Coordinate coordinate, final char referenceBase, final LIBRARY_TYPE libraryType) {
		super(coordinate, referenceBase, libraryType);
		
		readInfoCount = new ReadInfoCount();
	}
		
	@Override
	public ReadInfoCount getReadInfoCount() {
		return readInfoCount;
	}
	
	@Override
	public void add(AbstractData abstractData) {
		super.add(abstractData);
		
		BaseQualReadInfoData baseQualReadInfoData = (BaseQualReadInfoData) abstractData;
		readInfoCount.add(baseQualReadInfoData.readInfoCount);
	}
	
	@Override
	public BaseQualReadInfoData copy() {
		return new BaseQualReadInfoData(this);
	}
}
