package jacusa.pileup.worker;

import jacusa.cli.parameters.PileupParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.AllParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;

public class MpileupWorker<T extends BaseQualData> 
extends AbstractWorker<T> {

	private final Variant<T> variant;
	
	public MpileupWorker(MpileupWorkerDispatcher<T> workerDispatcher,
			int threadId,
			PileupParameters<T> parameters) {
		super(workerDispatcher, 
				threadId,
				parameters);
		variant = new AllParallelPileup<T>();
	}

	@Override
	protected Result<T> processParallelData(
			final ParallelPileupData<T> parallelPileup, 
			final WindowIterator<T> parallelDataIterator) {
		Result<T> result = new Result<T>();
		result.setParallelData(parallelPileup);

		if (getParameters().getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : getParameters().getFilterConfig().getFactories()) {
				AbstractFilter<T> storageFilter = filterFactory.createFilter();
				storageFilter.applyFilter(result, parallelDataIterator);
			}
		}

		return result;
	}

	@Override
	protected WindowIterator<T> buildIterator(Coordinate coordinate) {
		return new WindowIterator<T>(coordinate, variant, getReaders(), getParameters());
	}

	public PileupParameters<T> getParameters() {
		return (PileupParameters<T>) super.getParameters(); 
	}
	
}
