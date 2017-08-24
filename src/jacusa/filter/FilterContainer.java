package jacusa.filter;

import java.util.ArrayList;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import jacusa.util.WindowCoordinates;
import jacusa.util.Coordinate.STRAND;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterContainer<T extends AbstractData> {

	private FilterConfig<T> filterConfig;
	private List<AbstractFilter<T>> filters;
	private WindowCoordinates windowCoordinates;
	private STRAND strand;
	
	private int overhang;
	
	private List<ProcessRecord> processRecord;
	private List<ProcessAlignmentOperator> processAlignment;
	private List<ProcessAlignmentBlock> processAlignmentBlock;
	private List<ProcessDeletionOperator> processDeletion;
	private List<ProcessInsertionOperator> processInsertion;
	private List<ProcessSkippedOperator> processSkipped;
	
	public FilterContainer(
			final FilterConfig<T> filterConfig, final List<AbstractFilter<T>> filters,
			final STRAND strand, final WindowCoordinates windowCoordinates,
			final ConditionParameters<T> condition) {
		this.filterConfig 		= filterConfig;
		this.filters			= filters;
		this.windowCoordinates 	= windowCoordinates;
		this.strand				= strand;
		
		overhang 				= 0;

		final int initialCapacity = 3;
		processRecord 			= new ArrayList<ProcessRecord>(initialCapacity);
		processAlignment		= new ArrayList<ProcessAlignmentOperator>(initialCapacity);
		processAlignmentBlock	= new ArrayList<ProcessAlignmentBlock>(initialCapacity);
		processDeletion			= new ArrayList<ProcessDeletionOperator>(initialCapacity);
		processInsertion		= new ArrayList<ProcessInsertionOperator>(initialCapacity);
		processSkipped			= new ArrayList<ProcessSkippedOperator>(initialCapacity);

		for (final AbstractFilter<T> filter : filters) {
			filter.setCondition(condition);
			filter.setWindowCoordinates(windowCoordinates);
			
			
			if (filter.getProcessRecord() != null) {
				processRecord.add(filter.getProcessRecord());
			}
						
			if (filter.getProcessAlignment() != null){
				processAlignment.add(filter.getProcessAlignment());
			}
			
			if (filter.getProcessAlignmentBlock() != null) {
				processAlignmentBlock.add(filter.getProcessAlignmentBlock());
			}
			
			if (filter.getProcessDeletion() != null) {
				processDeletion.add(filter.getProcessDeletion());
			}
			
			if (filter.getProcessInsertion() != null) {
				processInsertion.add(filter.getProcessInsertion());
			}
			
			if (filter.getProcessSkipped() != null) {
				processSkipped.add(filter.getProcessSkipped());
			}

			overhang = Math.max(filter.getOverhang(), overhang);
		}
	}
	
	public void clear() {
		for (final AbstractFilter<T> filter : filters) {
			filter.clear();
		}
	}

	public int getOverhang() {
		return overhang;
	}

	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}

	public WindowCoordinates getWindowCoordinates() {
		return windowCoordinates;
	}

	public STRAND getStrand() {
		return strand;
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

}