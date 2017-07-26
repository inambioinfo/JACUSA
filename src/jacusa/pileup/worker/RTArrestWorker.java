package jacusa.pileup.worker;

import jacusa.cli.parameters.RTArrestParameters;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.pileup.iterator.TwoConditionIterator;
import jacusa.pileup.iterator.variant.RTArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class RTArrestWorker extends AbstractRTArrestWorker {

	private SAMFileReader[] readers1;
	private SAMFileReader[] readers2;
	private RTArrestParameters parameters;
	
	private final Variant variant;
	
	public RTArrestWorker(
			final RTArrestWorkerDispatcher threadDispatcher,
			final int threadId,
			final RTArrestParameters parameters) {
		super(
				threadDispatcher, 
				threadId,
				parameters.getStatisticParameters(), 
				parameters
		);

		this.parameters = parameters;
		readers1 = initReaders(parameters.getCondition1().getPathnames());
		readers2 = initReaders(parameters.getCondition2().getPathnames());

		variant = new RTArrestVariantParallelPileup();
	}

	@Override
	protected AbstractWindowIterator buildIterator(final Coordinate coordinate) {
		ConditionParameters condition1 = parameters.getCondition1();
		ConditionParameters condition2 = parameters.getCondition2();
		
		return new TwoConditionIterator(coordinate, variant, readers1, readers2, condition1, condition2, parameters);
	}

	@Override
	protected void close() {
		close(readers1);
		close(readers2);
	}

}