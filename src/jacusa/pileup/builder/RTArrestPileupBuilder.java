/**
 * 
 */
package jacusa.pileup.builder;


import java.util.Arrays;

import jacusa.filter.FilterContainer;
import jacusa.pileup.Pileup;
import jacusa.pileup.DefaultPileup.STRAND;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilder extends AbstractPileupBuilder {
	
	final private int[] readStartCount;
	final private int[] readEndCount;

	final private AbstractPileupBuilder pileupBuilder;
	
	public RTArrestPileupBuilder(AbstractPileupBuilder pileupBuilder) {
		super(pileupBuilder.windowCoordinates,
				pileupBuilder.strand,
				pileupBuilder.reader,
				pileupBuilder.condition, 
				pileupBuilder.parameters,
				pileupBuilder.libraryType);
		this.pileupBuilder = pileupBuilder;
		
		readStartCount	= new int[windowCoordinates.getWindowSize()];
		readEndCount	= new int[windowCoordinates.getWindowSize()];
	}
	
	@Override
	public Pileup getPileup(int windowPosition, STRAND strand) {
		Pileup pileup = pileupBuilder.getPileup(windowPosition, strand);

		pileup.setReadStartCount(readStartCount[windowPosition]);
		pileup.setReadEndCount(readEndCount[windowPosition]);

		return pileup;
	}

	protected void processRecord(SAMRecord record) {
		super.processRecord(record);
		
		int genomicPosition = record.getAlignmentStart();
		int windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
		
		if (windowPosition >= 0) {
			readStartCount[windowPosition] += 1;
		}
		int windowPositionReadEnd = windowCoordinates.convert2WindowPosition(record.getAlignmentEnd());
		if (windowPositionReadEnd >= 0) {
			readEndCount[windowPositionReadEnd] += 1;
		}
	}
	
	@Override
	public void clearCache() {
		pileupBuilder.clearCache();

		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);		
	}

	@Override
	protected void addHighQualityBaseCall(int windowPosition, int base,	int qual, STRAND strand) {
		pileupBuilder.addHighQualityBaseCall(windowPosition, base, qual, strand);
	}

	@Override
	protected void addLowQualityBaseCall(int windowPosition, int base, int qual, STRAND strand) {
		pileupBuilder.addLowQualityBaseCall(windowPosition, base, qual, strand);
	}

	@Override
	public boolean isCovered(int windowPosition, STRAND strand) {
		return pileupBuilder.isCovered(windowPosition, strand);
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		return getCoverage(windowPosition, strand);
	}

	@Override
	public WindowCache getWindowCache(STRAND strand) {
		return getWindowCache(strand);
	}

	@Override
	public FilterContainer getFilterContainer(int windowPosition, STRAND strand) {
		return getFilterContainer(windowPosition, strand);
	}

}