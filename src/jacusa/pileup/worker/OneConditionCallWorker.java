package jacusa.pileup.worker;

import jacusa.cli.parameters.OneConditionCallParameters;
import jacusa.pileup.dispatcher.call.OneConditionCallWorkerDispatcher;
import jacusa.pileup.iterator.OneConditionIterator;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.iterator.variant.VariantParallelPileup1;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class OneConditionCallWorker extends AbstractCallWorker {

	private SAMFileReader[] readers1;
	private final OneConditionCallParameters parameters;
	
	private final Variant variant;

	public OneConditionCallWorker(
			final OneConditionCallWorkerDispatcher threadDispatcher,
			final int threadId,
			final OneConditionCallParameters parameters) {
		super(
				threadDispatcher,
				threadId,
				parameters.getStatisticParameters(),
				parameters
		);

		this.parameters = parameters;
		readers1 = initReaders(parameters.getCondition1().getPathnames());

		variant = new VariantParallelPileup1();
	}

	@Override
	protected OneConditionIterator buildIterator(final Coordinate coordinate) {
		return new OneConditionIterator(coordinate, variant, readers1, parameters.getCondition1(), parameters);
	}

	@Override
	protected void close() {
		close(readers1);
	}

}