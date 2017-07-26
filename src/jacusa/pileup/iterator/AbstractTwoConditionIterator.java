package jacusa.pileup.iterator;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.filter.FilterContainer;
import jacusa.pileup.DefaultParallelPileup;
import jacusa.pileup.builder.AbstractPileupBuilder;
import jacusa.pileup.iterator.location.AbstractLocationAdvancer;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import jacusa.util.Location;
import net.sf.samtools.SAMFileReader;

public abstract class AbstractTwoConditionIterator extends AbstractWindowIterator {

	// condition 1
	protected final AbstractPileupBuilder[] pileupBuilders1;	

	// condition 2
	protected final AbstractPileupBuilder[] pileupBuilders2;

	public AbstractTwoConditionIterator(
			final Coordinate coordinate,
			final Variant filter,
			final SAMFileReader[] readers1,	final SAMFileReader[] readers2,
			final ConditionParameters condition1, final ConditionParameters condition2,
			AbstractParameters parameters) {
		super(coordinate, filter, parameters);

		pileupBuilders1 = createPileupBuilders(condition1.getPileupBuilderFactory(), coordinate, readers1, condition1, parameters);
		final Location loc1 = initLocation(coordinate, condition1.getPileupBuilderFactory().isStranded(), pileupBuilders1);

		pileupBuilders2 = createPileupBuilders(condition2.getPileupBuilderFactory(), coordinate, readers2, condition2, parameters);
		final Location loc2 = initLocation(coordinate, condition2.getPileupBuilderFactory().isStranded(), pileupBuilders2);
		
		// create the correct LocationAdvancer
		locationAdvancer = AbstractLocationAdvancer.getInstance(
				condition1.getPileupBuilderFactory().isStranded(), loc1, 
				condition2.getPileupBuilderFactory().isStranded(), loc2);

		parallelPileup = new DefaultParallelPileup(pileupBuilders1.length, pileupBuilders2.length);
	}

	protected boolean hasNext1() {
		return hasNext(locationAdvancer.getLocation1(), pileupBuilders1);
	}

	protected boolean hasNext2() {
		return hasNext(locationAdvancer.getLocation2(), pileupBuilders2);
	}
	
	public FilterContainer[] getFilterContainers4Replicates1(Location location) {
		return getFilterCaches4Replicates(location, pileupBuilders1);
	}

	public FilterContainer[] getFilterContainers4Replicates2(Location location) {
		return getFilterCaches4Replicates(location, pileupBuilders2);
	}

}