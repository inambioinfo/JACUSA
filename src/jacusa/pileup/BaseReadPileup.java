package jacusa.pileup;

import jacusa.util.Coordinate.STRAND;

public class BaseReadPileup extends AbstractData<BaseReadPileup> implements hasCoordinate, hasBaseCount, hasReadInfoCount, hasRefBase {

	private BaseCount baseCount;
	private ReadInfoCount readInfoCount;
	
	private char refBase;
	
	public BaseReadPileup(final int bases) {
		super();
		
		baseCount		= new BaseCount(bases);
		readInfoCount 	= new ReadInfoCount();
		
		refBase 		= 'N';
	}

	public BaseReadPileup(final String contig, 
			final int position, 
			final STRAND strand, 
			final int baseLength) {
		this(baseLength);
		setContig(contig);
		setPosition(position);
		setStrand(strand);
	}

	public BaseReadPileup(final BaseReadPileup data) {
		super(data.getContig(),
				data.getStart(),
				data.getEnd(),
				data.getStrand());

		refBase = data.refBase;
		baseCount		= new BaseCount(data.getBaseCount());
		readInfoCount 	= new ReadInfoCount(data.getReadInfoCount());
	}

	public BaseCount getBaseCount() {
		return baseCount;
	}
	
	public void setBaseCount(final BaseCount baseCount) {
		this.baseCount = baseCount;
	}

	@Override
	public char getRefBase() {
		return refBase;
	}
	
	@Override
	public void setRefBase(char refBase) {
		this.refBase = refBase;
	}
	
	@Override
	public int getCoverage() {
		return baseCount.getCoverage();
	}
	
	public ReadInfoCount getReadInfoCount() {
		return readInfoCount;
	}

	public void setBaseCount(final ReadInfoCount readInfoCount) {
		this.readInfoCount = readInfoCount;
	}

	@Override
	public void add(BaseReadPileup data) {
		baseCount.add(data.getBaseCount());
		readInfoCount.add(data.getReadInfoCount());
	}

	@Override
	public BaseReadPileup copy() {
		return new BaseReadPileup(this);
	}

}