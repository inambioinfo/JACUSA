package jacusa.pileup.worker;

import jacusa.cli.parameters.PileupParameters;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.pileup.BasePileup;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.AllParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import jacusa.util.Location;

public class MpileupWorker extends AbstractWorker<BasePileup> {

	private final Variant<BasePileup> variant;
	
	public MpileupWorker(
			MpileupWorkerDispatcher workerDispatcher,
			int threadId,
			PileupParameters parameters) {
		super(workerDispatcher, 
				threadId,
				parameters);
		variant = new AllParallelPileup();
	}

	@Override
	protected Result<BasePileup> processParallelData(
			final ParallelData<BasePileup> parallelPileup, 
			final Location location, 
			final WindowIterator<BasePileup> parallelDataIterator) {
		Result<BasePileup> result = new Result<BasePileup>();
		result.setParallelData(parallelPileup);

		if (getParameters().getFilterConfig().hasFiters()) {
			// apply each filter
			for (AbstractFilterFactory<BasePileup> filterFactory : getParameters().getFilterConfig().getFactories()) {
				AbstractStorageFilter<BasePileup> storageFilter = filterFactory.createStorageFilter();
				storageFilter.applyFilter(result, location, parallelDataIterator);
			}
		}

		return result;
	}

	@Override
	protected WindowIterator<BasePileup> buildIterator(Coordinate coordinate) {
		return new WindowIterator<BasePileup>(coordinate, variant, readers, getParameters());
	}

	public PileupParameters getParameters() {
		return (PileupParameters) super.getParameters(); 
	}
	
}