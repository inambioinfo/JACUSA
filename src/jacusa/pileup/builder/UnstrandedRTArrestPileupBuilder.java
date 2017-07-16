/**
 * 
 */
package jacusa.pileup.builder;


import java.util.Arrays;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.SampleParameters;
import jacusa.pileup.Pileup;
import jacusa.pileup.DefaultPileup.STRAND;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class UnstrandedRTArrestPileupBuilder extends UnstrandedPileupBuilder {
	
	private int[] readStartCount;
	private int[] readEndCount;
	
	public UnstrandedRTArrestPileupBuilder(
			final Coordinate annotatedCoordinate, 
			final SAMFileReader reader, 
			final SampleParameters sample,
			final AbstractParameters parameters) {
		super(annotatedCoordinate, reader, sample, parameters);
		readStartCount	= new int[windowCoordinates.getWindowSize()];
		readEndCount	= new int[windowCoordinates.getWindowSize()];
	}

	@Override
	public Pileup getPileup(int windowPosition, STRAND strand) {
		Pileup pileup = super.getPileup(windowPosition, strand);

		pileup.setReadStartCount(readStartCount[windowPosition]);
		pileup.setReadEndCount(readEndCount[windowPosition]);

		return pileup;
	}

	protected void processRecord(SAMRecord record) {
		super.processRecord(record);
		
		// TODO read coverage / use strand
		// windowPosition set in super.procesRecord
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
		super.clearCache();

		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);		
	}

}