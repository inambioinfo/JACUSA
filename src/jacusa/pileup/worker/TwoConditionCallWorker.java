package jacusa.pileup.worker;

import jacusa.cli.parameters.CallParameters;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.BasePileup;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.dispatcher.call.TwoConditionCallWorkerDispatcher;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.iterator.variant.VariantParallelPileup;
import jacusa.util.Coordinate;
import jacusa.util.Location;

public class TwoConditionCallWorker extends AbstractWorker<BasePileup> {

	final private CallParameters<BasePileup> parameters;
	
	final private StatisticCalculator<BasePileup> statisticCalculator;
	
	final private Variant<BasePileup> variant;
	
	public TwoConditionCallWorker(
			final TwoConditionCallWorkerDispatcher workerDispatcher,
			final int threadId,
			final CallParameters<BasePileup> parameters) {
		super(workerDispatcher,
				threadId,
				parameters);

		this.statisticCalculator = parameters.getStatisticParameters().getStatisticCalculator();
		
		this.parameters = parameters;
		variant = new VariantParallelPileup();
	}

	@Override
	protected Result<BasePileup> processParallelData(
			final ParallelData<BasePileup> parallelData, 
			final Location location, 
			final WindowIterator<BasePileup> parallelDataIterator) {
		// result object
		Result<BasePileup> result = new Result<BasePileup>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);
		
		if (statisticCalculator.filter(result.getStatistic())) {
			return null;
		}

		if (parameters.getFilterConfig().hasFiters()) {
			// apply each filter
			for (AbstractFilterFactory<BasePileup> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractStorageFilter<BasePileup> storageFilter = filterFactory.createStorageFilter();
				storageFilter.applyFilter(result, location, parallelDataIterator);
			}
		}

		return result;
	}
	
	@Override
	protected WindowIterator<BasePileup> buildIterator(final Coordinate coordinate) {
		return new WindowIterator<BasePileup>(coordinate, 
				variant, readers, parameters);
	}

}