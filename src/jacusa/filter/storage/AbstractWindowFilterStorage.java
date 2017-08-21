package jacusa.filter.storage;

import java.util.Arrays;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseConfig;

import jacusa.pileup.builder.WindowCache;
import jacusa.util.WindowCoordinates;

import net.sf.samtools.SAMRecord;

public abstract class AbstractWindowFilterStorage extends AbstractFilterStorage {

	// count indel, read start/end, splice site as only 1!!!
	// this ensure that a base-call will only be counted once...
	private boolean[] visited;
	private BaseConfig baseConfig;

	private int windowSize;
	protected WindowCache windowCache;

	private ConditionParameters<?> condition;
	
	// container for current SAMrecord
	protected SAMRecord record;

	public AbstractWindowFilterStorage(final char c, 
			final WindowCoordinates windowCoordinates, 
			final ConditionParameters<?> condition,
			final int windowSize,
			final BaseConfig baseConfig) {
		super(c);

		final int bases = baseConfig.getBases().length;
		setWindowCache(new WindowCache(windowCoordinates, bases));
		this.condition = condition;
		this.windowSize = windowSize;
		this.baseConfig = baseConfig;
		
		visited = new boolean[windowSize];
		windowCache = getWindowCache();
	}
	
	protected void addRegion(int windowPosition, int length, int readPosition, SAMRecord record) {
		if (this.record != record) {
			this.record = record;
			Arrays.fill(visited, false);
		}

		int offset = 0;

		if (readPosition < 0) {
			offset += Math.abs(readPosition);
			
			windowPosition += offset;
			readPosition += offset;
			length -= offset;
		}

		if (windowPosition < 0) {
			offset += Math.abs(windowPosition);
			
			windowPosition += offset;
			readPosition += offset;
			length -= offset;
		}

		for (int i = 0; i < length && windowPosition + i < windowSize && readPosition + i < record.getReadLength(); ++i) {
			if (! visited[windowPosition + i]) {
				int baseIndex = -1;

				// TODO move this to instantiation
				switch (condition.getDataBuilderFactory().getLibraryType()) {
				case UNSTRANDED:
				case FR_SECONDSTRAND:
					baseIndex = baseConfig.getBaseIndex(record.getReadBases()[readPosition + i]);
					break;
					
				case FR_FIRSTSTRAND:
					baseIndex = baseConfig.getComplementBaseIndex(record.getReadBases()[readPosition + i]);
				} 
				if (condition.isInvertStrand()) {
					baseIndex = baseConfig.getComplementbyte2int()[baseIndex];
				}

				// corresponds to N -> ignore
				if (baseIndex < 0) {
					continue;
				}

				byte qual = record.getBaseQualities()[readPosition + i];
				// int genomicPosition = windowCache.getWindowCoordinates().getGenomicPosition(windowPosition + i);
				if (qual >= condition.getMinBASQ()) {
					windowCache.addHighQualityBaseCall(windowPosition + i, baseIndex, qual);
					visited[windowPosition + i] = true;
				}
			}
		}
	}

	@Override
	public void clearContainer() {
		getWindowCache().clear();		
	}

}