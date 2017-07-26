package jacusa.pileup.worker;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.TwoConditionCallParameters;
import jacusa.pileup.dispatcher.call.TwoConditionCallWorkerDispatcher;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.pileup.iterator.TwoConditionIterator;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.iterator.variant.VariantParallelPileup;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class TwoConditionCallWorker extends AbstractCallWorker {

	private SAMFileReader[] readers1;
	private SAMFileReader[] readers2;
	private TwoConditionCallParameters parameters;
	
	private final Variant variant;
	
	public TwoConditionCallWorker(
			final TwoConditionCallWorkerDispatcher threadDispatcher,
			final int threadId,
			final TwoConditionCallParameters parameters) {
		super(
				threadDispatcher, 
				threadId,
				parameters.getStatisticParameters(), 
				parameters
		);

		this.parameters = parameters;
		readers1 = initReaders(parameters.getCondition1().getPathnames());
		readers2 = initReaders(parameters.getCondition2().getPathnames());

		variant = new VariantParallelPileup();
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