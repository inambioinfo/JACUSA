package jacusa.filter;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Pileup;
import jacusa.pileup.Result;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.util.Location;

public class MaxDepthStorageFilter extends AbstractStorageFilter<Void> {

	private ConditionParameters condition1;
	private ConditionParameters condition2;
	
	public MaxDepthStorageFilter(
			final char c, 
			final ConditionParameters condition1, 
			final ConditionParameters condition2) {
		super(c);
		this.condition1 = condition1;
		this.condition2 = condition2;
	}

	@Override
	public boolean filter(final Result result, final Location location,	final AbstractWindowIterator windowIterator) {
		final ParallelPileup pp = result.getParellelPileup();
		
		if (condition1 != null) {
			if (filter(condition1.getMaxDepth(), pp.getPileups1())) {
				return true;
			}
		}
		
		if (condition2 != null) {
			if (filter(condition2.getMaxDepth(), pp.getPileups2())) {
				return true;
			}
		}
		
		return false;
	}

	private boolean filter(int maxDepth, Pileup[] pileups) {
		if (maxDepth > 0) { 
			for (final Pileup pileup : pileups) {
				if (pileup.getCoverage() > maxDepth) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}