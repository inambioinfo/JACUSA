package jacusa.data;

import jacusa.util.Coordinate.STRAND;

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
	
	public BaseQualData(final String contig, 
			final int start, final int end, 
			final STRAND strand, final int bases, final char referenceBase) {
		super(contig, start, end, strand, referenceBase);
		
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

	// TODO do we need this?
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
	
	public BaseQualData copy() {
		return new BaseQualData(this);
	}
	
}
