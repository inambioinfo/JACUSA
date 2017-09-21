package jacusa.util;

public class WindowCoordinates extends Coordinate {

	private int windowSize;
	private int maxGenomicPosition;

	public WindowCoordinates(final String contig, 
			final int genomicWindowStart, 
			final int windowSize, 
			final int maxGenomicPosition) {
		setContig(contig);
		setStart(genomicWindowStart);
		this.windowSize = windowSize;
		setMaxGenomicPosition(maxGenomicPosition);
	}

	public void setStart(final int start) {
		super.setStart(start);
		_setEnd();
	}
	
	public int getEnd() {
		return Math.min(getStart() + windowSize - 1, maxGenomicPosition);
	}
	
	/**
	 * End of window (inclusive)
	 * @return
	 */
	private void _setEnd() {
		setEnd(Math.min(getStart() + windowSize - 1, maxGenomicPosition));
	}
	
	public int getWindowSize() {
		return windowSize;
	}

	public int getMaxGenomicPosition() {
		return maxGenomicPosition;
	}

	public void setMaxGenomicPosition(int maxGenomicPosition) {
		this.maxGenomicPosition = maxGenomicPosition;
		_setEnd();
	}

	/**
	 * 
	 * @param genomicPosition
	 * @return
	 */
	public boolean isContainedInWindow(int genomicPosition) {
		return genomicPosition >= getStart() && genomicPosition <= getEnd();
	}

	/**
	 * Calculates genomicPosition or -1 or -2 if genomicPosition is outside the window
	 * -1 if downstream of windowEnd
	 * -2 if upstream of windowStart
	 * @param genomicPosition
	 * @return
	 */
	public int convert2WindowPosition(final int genomicPosition) {
		/*
		if(genomicPosition < genomicWindowStart) {
			return -2;
		} else if(genomicPosition > getGenomicWindowEnd()){
			return -1;
		}
		*/
		
		if(genomicPosition > getEnd()){
			return -1;
		}

		return genomicPosition - getStart();
	}

	/**
	 * 
	 * @param windowPosition
	 * @return
	 */
	public int getGenomicPosition(int windowPosition) {
		return getStart() + windowPosition;
	}
	
	public int getOrientation(final int genomicPosition) {
		if(genomicPosition < getStart()) {
			return -1;
		}
		
		if(genomicPosition > getEnd()){
			return 1;
		}
		
		return 0;
	}

}