package jacusa.filter.storage;

import java.util.Arrays;
import java.util.List;

import jacusa.data.AbstractData;
import jacusa.data.BaseConfig;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterContainer;

import jacusa.pileup.builder.WindowCache;
import jacusa.util.Coordinate;
import jacusa.util.WindowCoordinates;
import jacusa.util.Coordinate.STRAND;

import net.sf.samtools.SAMRecord;

public abstract class AbstractWindowStorage<T extends AbstractData> 
extends AbstractStorage<T> {

	// count indel, read start/end, splice site as only 1!!!
	// this ensure that a base-call will only be counted once...
	private boolean[] visited;
	private final BaseConfig baseConfig;

	private WindowCache windowCache;
	
	// container for current SAMrecord
	protected SAMRecord record;

	public AbstractWindowStorage(final char c, final BaseConfig baseConfig) {
		super(c);
		this.baseConfig = baseConfig;
	}
	
	// visited = new boolean[windowSize];
	
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

		for (int i = 0; i < length && windowPosition + i < windowCache.getWindowSize() && readPosition + i < record.getReadLength(); ++i) {
			if (! visited[windowPosition + i]) {
				int baseIndex = -1;

				// TODO move this to instantiation
				switch (getCondition().getDataBuilderFactory().getLibraryType()) {
				case UNSTRANDED:
				case FR_SECONDSTRAND:
					baseIndex = baseConfig.getBaseIndex(record.getReadBases()[readPosition + i]);
					break;
					
				case FR_FIRSTSTRAND:
					baseIndex = baseConfig.getComplementBaseIndex(record.getReadBases()[readPosition + i]);
				} 
				if (getCondition().isInvertStrand()) {
					baseIndex = baseConfig.getComplementbyte2int()[baseIndex];
				}

				// corresponds to N -> ignore
				if (baseIndex < 0) {
					continue;
				}

				byte qual = record.getBaseQualities()[readPosition + i];
				if (qual >= getCondition().getMinBASQ()) {
					windowCache.addHighQualityBaseCall(windowPosition + i, baseIndex, qual);
					visited[windowPosition + i] = true;
				}
			}
		}
	}

	// TODO check
		protected BaseQualData[] getBaseQualData(final Coordinate coordinate, 
				final List<FilterContainer<T>> filterContainers) {
			final int n = filterContainers.size();
			BaseQualData[] baseQualData = new BaseQualData[n];

			// FIXME
			// correct orientation in U,S S,U cases
			boolean invert = false;
			if (coordinate.getStrand() == STRAND.REVERSE && filterContainers.get(0).getStrand() == STRAND.UNKNOWN) {
				invert = true;
			}

			/*
			for (int replicateIndex = 0; replicateIndex < n; ++replicateIndex) {
				final FilterContainer<T> filterContainer = filterContainers.get(replicateIndex);
				final WindowCache windowCache = getWindowCache(filterContainer);
				final int windowPosition = filterContainer.getWindowCoordinates().convert2WindowPosition(coordinate.getPosition());

				baseQualData[replicateIndex] = windowCache.getBaseCount(windowPosition);
				if (invert) {
					baseQualData[replicateIndex].invert();
				}
			}
			*/

			return baseQualData;
		}
	
	public void setWindowCoordinates(final WindowCoordinates windowCoordinates) {
		windowCache = new WindowCache(windowCoordinates, baseConfig.getBases().length);
	}

	public WindowCache getWindowCache() {
		return windowCache;
	}

	@Override
	public void clear() {
		windowCache.clear();		
	}

}