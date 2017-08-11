package jacusa.pileup.worker;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.StatisticCalculator;
import jacusa.pileup.dispatcher.call.TwoConditionCallWorkerDispatcher;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.pileup.iterator.variant.VariantParallelPileup;
import jacusa.util.Coordinate;
import jacusa.util.Location;

public class TwoConditionCallWorker<T extends BaseQualData> 
extends AbstractWorker<T> {

	final private CallParameters<T> parameters;
	
	final private StatisticCalculator<T> statisticCalculator;
	
	final private Variant<T> variant;
	
	public TwoConditionCallWorker(
			final TwoConditionCallWorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final CallParameters<T> parameters) {
		super(workerDispatcher,
				threadId,
				parameters);

		this.statisticCalculator = parameters.getStatisticParameters().getStatisticCalculator();
		
		this.parameters = parameters;
		variant = new VariantParallelPileup<T>();
	}

	@Override
	protected Result<T> processParallelData(
			final ParallelPileupData<T> parallelData, 
			final Location location, 
			final WindowIterator<T> parallelDataIterator) {
		// result object
		Result<T> result = new Result<T>();
		result.setParallelData(parallelData);
		statisticCalculator.addStatistic(result);
		
		if (statisticCalculator.filter(result.getStatistic())) {
			return null;
		}

		if (parameters.getFilterConfig().hasFiters()) {
			// apply each filter
			for (final AbstractFilterFactory<T> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractStorageFilter<T> storageFilter = filterFactory.createStorageFilter();
				storageFilter.applyFilter(result, location, parallelDataIterator);
			}
		}

		return result;
	}
	
	@Override
	protected WindowIterator<T> buildIterator(final Coordinate coordinate) {
		return new WindowIterator<T>(coordinate, variant, getReaders(), parameters);
	}

}
