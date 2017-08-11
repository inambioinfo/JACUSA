package jacusa.pileup.worker;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.pileup.dispatcher.call.OneConditionCallWorkerDispatcher;
import jacusa.pileup.iterator.OneConditionCallIterator;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.iterator.variant.VariantParallelPileup;
import jacusa.util.Coordinate;
import jacusa.util.Location;

public class OneConditionCallWorker<T extends BaseQualData> 
extends AbstractWorker<T> {

	private final Variant<T> variant;

	public OneConditionCallWorker(
			final OneConditionCallWorkerDispatcher<T> threadDispatcher,
			final int threadId,
			final CallParameters<T> parameters) {
		super(threadDispatcher,
				threadId,
				parameters);
		variant = new VariantParallelPileup<T>();
	}

	@Override
	protected OneConditionCallIterator<T> buildIterator(final Coordinate coordinate) {
		return new OneConditionCallIterator<T>(coordinate, variant, getReaders(), getParameters());
	}
	
	public CallParameters<T> getParameters() {
		return (CallParameters<T>) super.getParameters();
	}

	@Override
	protected Result<T> processParallelData(
			ParallelPileupData<T> parallelData, Location location,
			WindowIterator<T> parallelPileupIterator) {
		// TODO Auto-generated method stub
		return null;
	}

}
