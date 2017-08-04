package jacusa.pileup.worker;

import jacusa.cli.parameters.RTArrestParameters;

import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.BaseReadPileup;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.RTArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import jacusa.util.Location;

public class RTArrestWorker extends AbstractWorker<BaseReadPileup> {

	private RTArrestParameters<BaseReadPileup> parameters;
	private StatisticCalculator<BaseReadPileup> statisticCalculator;
	
	private final Variant<BaseReadPileup> variant;
	
	public RTArrestWorker(
			final RTArrestWorkerDispatcher workerDispatcher,
			final int threadId,
			final RTArrestParameters<BaseReadPileup> parameters) {
		super(workerDispatcher, 
				threadId,
				parameters);

		this.parameters = parameters;
		statisticCalculator = parameters.getStatisticParameters().getStatisticCalculator().newInstance();
		
		variant = new RTArrestVariantParallelPileup(parameters.getConditionParameters());
	}

	@Override
	protected WindowIterator<BaseReadPileup> buildIterator(final Coordinate coordinate) {
		return new WindowIterator<BaseReadPileup>(coordinate, variant, readers, parameters);
	}
	
	@Override
	protected Result<BaseReadPileup> processParallelData(
			final ParallelData<BaseReadPileup> parallelData, 
			final Location location, 
			final WindowIterator<BaseReadPileup> parallelDataIterator) {
		// result object
		Result<BaseReadPileup> result = new Result<BaseReadPileup>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);
		
		if (statisticCalculator.filter(result.getStatistic())) {
			return null;
		}

		if (parameters.getFilterConfig().hasFiters()) {
			// apply each filter
			for (AbstractFilterFactory<BaseReadPileup> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractStorageFilter<BaseReadPileup> storageFilter = filterFactory.createStorageFilter();
				storageFilter.applyFilter(result, location, parallelDataIterator);
			}
		}

		return result;
	}

}