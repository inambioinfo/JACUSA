package jacusa.filter;

import java.util.ArrayList;
import java.util.List;

import jacusa.data.AbstractData;
import jacusa.data.Result;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import jacusa.pileup.iterator.WindowIterator;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractFilter<T extends AbstractData> {

	private final char c;
	
	private final List<ProcessRecord> processRecord;
	private final List<ProcessAlignmentOperator> processAlignment;
	private final List<ProcessAlignmentBlock> processAlignmentBlock;
	private final List<ProcessDeletionOperator> processDeletion;
	private final List<ProcessInsertionOperator> processInsertion;
	private final List<ProcessSkippedOperator> processSkipped;
	
	public AbstractFilter(final char c) {
		this.c 					= c;
		
		processRecord 			= new ArrayList<ProcessRecord>(2);
		processAlignment		= new ArrayList<ProcessAlignmentOperator>(2);
		processAlignmentBlock	= new ArrayList<ProcessAlignmentBlock>(2);
		processDeletion			= new ArrayList<ProcessDeletionOperator>(2);
		processInsertion		= new ArrayList<ProcessInsertionOperator>(2);
		processSkipped			= new ArrayList<ProcessSkippedOperator>(2);
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

	public List<ProcessRecord> getProcessRecord() {
		return processRecord;
	}
	
	public List<ProcessAlignmentOperator> getProcessAlignment() {
		return processAlignment;
	}
	
	public List<ProcessAlignmentBlock> getProcessAlignmentBlock() {
		return processAlignmentBlock;
	}
	
	public List<ProcessDeletionOperator> getProcessDeletion() {
		return processDeletion;
	}
	
	public List<ProcessInsertionOperator> getProcessInsertion() {
		return processInsertion;
	}
	
	public List<ProcessSkippedOperator> getProcessSkipped() {
		return processSkipped;
	}
	
	public abstract int getOverhang();
	
	public abstract void clear();
}
