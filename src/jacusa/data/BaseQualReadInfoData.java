package jacusa.data;

import jacusa.util.Coordinate.STRAND;

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
	
	public BaseQualReadInfoData(final String contig, 
			final int start, final int end, 
			final STRAND strand, final int bases, final char referenceBase) {
		super(contig, start, end, strand, bases, referenceBase);
		
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
