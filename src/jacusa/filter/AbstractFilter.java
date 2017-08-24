package jacusa.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.data.BaseQualData;
import jacusa.data.Result;
import jacusa.filter.storage.AbstractStorage;
import jacusa.filter.storage.AbstractWindowStorage;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import jacusa.pileup.builder.WindowCache;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;
import jacusa.util.WindowCoordinates;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractFilter<T extends AbstractData> {

	private final char c;
	
	private AbstractStorage<T> storage;
	private AbstractWindowStorage<T> windowStorage;
	
	private ProcessRecord processRecord;
	private ProcessAlignmentOperator processAlignment;
	private ProcessAlignmentBlock processAlignmentBlock;
	private ProcessDeletionOperator processDeletion;
	private ProcessInsertionOperator processInsertion;
	private ProcessSkippedOperator processSkipped;
	
	public AbstractFilter(final char c) {
		this.c = c;
	}

	/**
	 * 
	 * @return
	 */
	public final char getC() {
		return c;
	}
	
	/**
	 * 
	 * @param filterContainer
	 * @return
	 */
	protected WindowCache getWindowCache() {
		return windowStorage.getWindowCache();
	}

	// TODO check
	public BaseQualData[] getBaseQualData(final Coordinate coordinate, 
			final List<FilterContainer<T>> filterContainers) {
		final int n = filterContainers.size();
		BaseQualData[] baseQualData = new BaseQualData[n];

		for (int replicateIndex = 0; replicateIndex < n; ++replicateIndex) {
			final FilterContainer<T> filterContainer = filterContainers.get(replicateIndex);
			final WindowCache windowCache = getWindowCache(filterContainer);
			final int windowPosition = filterContainer.getWindowCoordinates()
					.convert2WindowPosition(coordinate.getPosition());

			BaseQualData d = new BaseQualData(
					coordinate, 'N', filterContainer.getL);
			d.setBaseQualCount(windowCache.getBaseCount(windowPosition));
			/* TODO
			if (invert) {
				baseQualData[replicateIndex].invert();
			}
			*/
			baseQualData[replicateIndex] = d;
		}

		return baseQualData;
	}
	
	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	protected abstract boolean filter(final Result<T> result, final WindowIterator<T> windowIterator);
	
	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	public boolean applyFilter(final Result<T> result, final WindowIterator<T> windowIterator) {
		if (filter(result, windowIterator)) {
			addFilterInfo(result);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param result
	 */
	public void addFilterInfo(Result<T> result) {
		result.getFilterInfo().add(Character.toString(getC()));
	}

	public ProcessRecord getProcessRecord() {
		return processRecord;
	}

	public void registerStorage(final AbstractStorage<T> storage) {
		this.storage = storage;
	}

	public void registerWindowStorage(final AbstractWindowStorage<T> windowStorage) {
		storage = windowStorage;
		this.windowStorage = windowStorage;
	}
	
	public void registerProcessRecord(final ProcessRecord e) {
		processRecord = e;
	}
	
	public ProcessAlignmentOperator getProcessAlignment() {
		return processAlignment;
	}
	
	public void registerProcessAlignment(final ProcessAlignmentOperator e) {
		processAlignment = e;
	}
	
	public ProcessAlignmentBlock getProcessAlignmentBlock() {
		return processAlignmentBlock;
	}
	
	public void registerProcessAlignmentBlock(final ProcessAlignmentBlock e) {
		processAlignmentBlock = e;
	}
	
	public ProcessDeletionOperator getProcessDeletion() {
		return processDeletion;
	}
	
	public void registerProcessDeletion(final ProcessDeletionOperator e) {
		processDeletion = e;
	}
	
	public ProcessInsertionOperator getProcessInsertion() {
		return processInsertion;
	}
	
	public void registerProcessInsertion(final ProcessInsertionOperator e) {
		processInsertion = e;
	}
	
	public ProcessSkippedOperator getProcessSkipped() {
		return processSkipped;
	}
	
	public void registerProcessSkipped(final ProcessSkippedOperator e) {
		processSkipped = e;
	}
	
	public void setCondition(final ConditionParameters<T> condition) {
		storage.setCondition(condition);
	}
	
	public void setWindowCoordinates(final WindowCoordinates windowCoordinates) {
		windowStorage.setWindowCoordinates(windowCoordinates);
	}

	public abstract int getOverhang();
	public abstract void clear();

}
