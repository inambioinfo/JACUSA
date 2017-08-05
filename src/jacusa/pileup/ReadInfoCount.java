package jacusa.pileup;

public class ReadInfoCount {

	// container
	private int start;
	private int inner;
	private int end;

	public ReadInfoCount() {}
	
	public ReadInfoCount(final ReadInfoCount readInfoCount) {
		this.start 	= readInfoCount.start;
		this.inner 	= readInfoCount.inner;
		this.end 	= readInfoCount.end;
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getInner() {
		return inner;
	}

	public void setInner(int inner) {
		this.inner = inner;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getCoverage() {
		return start + inner + end;
	}

	public void add(final ReadInfoCount readInfoCount) {
		start += readInfoCount.start;
		inner += readInfoCount.inner;
		end += readInfoCount.end;
	}
	
	public ReadInfoCount copy() {
		return new ReadInfoCount(this);
	}
	
}