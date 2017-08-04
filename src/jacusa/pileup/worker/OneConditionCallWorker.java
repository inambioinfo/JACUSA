package jacusa.pileup.worker;

import jacusa.cli.parameters.CallParameters;
import jacusa.pileup.Result;
import jacusa.pileup.BasePileup;
import jacusa.pileup.ParallelData;
import jacusa.pileup.dispatcher.call.OneConditionCallWorkerDispatcher;
import jacusa.pileup.iterator.OneConditionCallIterator;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.iterator.variant.VariantParallelPileup;
import jacusa.util.Coordinate;
import jacusa.util.Location;

public class OneConditionCallWorker extends AbstractWorker<BasePileup> {

	private final Variant<BasePileup> variant;

	public OneConditionCallWorker(
			final OneConditionCallWorkerDispatcher threadDispatcher,
			final int threadId,
			final CallParameters<BasePileup> parameters) {
		super(threadDispatcher,
				threadId,
				parameters);
		variant = new VariantParallelPileup();
	}

	@Override
	protected OneConditionCallIterator buildIterator(final Coordinate coordinate) {
		return new OneConditionCallIterator(coordinate, 
				variant, readers, getParameters());
	}
	
	public CallParameters<BasePileup> getParameters() {
		return (CallParameters<BasePileup>) super.getParameters();
	}

	@Override
	protected Result<BasePileup> processParallelData(
			ParallelData<BasePileup> parallelData, Location location,
			WindowIterator<BasePileup> parallelPileupIterator) {
		// TODO Auto-generated method stub
		return null;
	}

}