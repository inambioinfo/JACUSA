package jacusa.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.data.Result;
import jacusa.filter.storage.AbstractStorage;
import jacusa.filter.storage.AbstractWindowStorage;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.WindowCoordinates;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractFilter<T extends AbstractData> {

	private final char c;
	
	private final Set<AbstractStorage<T>> storages;
	private final Set<AbstractWindowStorage<T>> windowStorages;
	
	private final Set<ProcessRecord> processRecord;
	private final Set<ProcessAlignmentOperator> processAlignment;
	private final Set<ProcessAlignmentBlock> processAlignmentBlock;
	private final Set<ProcessDeletionOperator> processDeletion;
	private final Set<ProcessInsertionOperator> processInsertion;
	private final Set<ProcessSkippedOperator> processSkipped;
	
	public AbstractFilter(final char c) {
		this.c 					= c;
		final int initialCapacity = 2;
		
		storages				= new HashSet<AbstractStorage<T>>(initialCapacity);
		windowStorages			= new HashSet<AbstractWindowStorage<T>>(initialCapacity);
				
		processRecord 			= new HashSet<ProcessRecord>(initialCapacity);
		processAlignment		= new HashSet<ProcessAlignmentOperator>(initialCapacity);
		processAlignmentBlock	= new HashSet<ProcessAlignmentBlock>(initialCapacity);
		processDeletion			= new HashSet<ProcessDeletionOperator>(initialCapacity);
		processInsertion		= new HashSet<ProcessInsertionOperator>(initialCapacity);
		processSkipped			= new HashSet<ProcessSkippedOperator>(initialCapacity);
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
	protected WindowCache getWindowCache(final FilterContainer<T> filterContainer) {
		int filterIndex = filterContainer.getFilterConfig().c2i(c);
		WindowCache windowCache = filterContainer.get(filterIndex).getWindowCache();
		return windowCache;
	}
	*/

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

	public Iterator<ProcessRecord> getProcessRecord() {
		return processRecord.iterator();
	}

	public void addStorage(final AbstractStorage<T> storage) {
		this.storages.add(storage);
	}

	public void addWindowStorage(final AbstractWindowStorage<T> windowStorage) {
		storages.add(windowStorage);
		this.windowStorages.add(windowStorage);
	}
	
	public void addProcessRecord(final ProcessRecord e) {
		processRecord.add(e);
	}
	
	public Iterator<ProcessAlignmentOperator> getProcessAlignment() {
		return processAlignment.iterator();
	}
	
	public void addProcessAlignment(final ProcessAlignmentOperator e) {
		processAlignment.add(e);
	}
	
	public Iterator<ProcessAlignmentBlock> getProcessAlignmentBlock() {
		return processAlignmentBlock.iterator();
	}
	
	public void addProcessAlignmentBlock(final ProcessAlignmentBlock e) {
		processAlignmentBlock.add(e);
	}
	
	public Iterator<ProcessDeletionOperator> getProcessDeletion() {
		return processDeletion.iterator();
	}
	
	public void addProcessDeletion(final ProcessDeletionOperator e) {
		processDeletion.add(e);
	}
	
	public Iterator<ProcessInsertionOperator> getProcessInsertion() {
		return processInsertion.iterator();
	}
	
	public void addProcessInsertion(final ProcessInsertionOperator e) {
		processInsertion.add(e);
	}
	
	public Iterator<ProcessSkippedOperator> getProcessSkipped() {
		return processSkipped.iterator();
	}
	
	public void addProcessSkipped(final ProcessSkippedOperator e) {
		processSkipped.add(e);
	}
	
	public void setCondition(final ConditionParameters<T> condition) {
		for (final AbstractStorage<T> e : storages) {
			e.setCondition(condition);
		}
	}
	
	public void setWindowCoordinates(final WindowCoordinates windowCoordinates) {
		for (final AbstractWindowStorage<T> e : windowStorages) {
			e.setWindowCoordinates(windowCoordinates);
		}
	}

	public abstract int getOverhang();
	public abstract void clear();

}
