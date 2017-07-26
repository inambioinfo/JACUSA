package jacusa.pileup.worker;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.cli.parameters.TwoConditionPileupParameters;
import jacusa.filter.AbstractStorageFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.pileup.ParallelPileup;
import jacusa.pileup.Result;
import jacusa.pileup.dispatcher.pileup.MpileupWorkerDispatcher;
import jacusa.pileup.iterator.AbstractTwoConditionIterator;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.pileup.iterator.TwoConditionIterator;
import jacusa.pileup.iterator.variant.AllParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import jacusa.util.Location;

import net.sf.samtools.SAMFileReader;

public class MpileupWorker extends AbstractWorker {

	private final TwoConditionPileupParameters parameters;
	private final SAMFileReader[] readers1;
	private final SAMFileReader[] readers2;

	private final Variant variant;
	
	public MpileupWorker(
			MpileupWorkerDispatcher workerDispatcher,
			int threadId,
			TwoConditionPileupParameters parameters) {
		super(
				workerDispatcher, 
				threadId,
				parameters.getMaxThreads()
		);
		this.parameters = parameters;

		readers1 = initReaders(parameters.getCondition1().getPathnames());
		readers2 = initReaders(parameters.getCondition2().getPathnames());
		
		variant = new AllParallelPileup();
	}

	@Override
	protected Result processParallelPileup(ParallelPileup parallelPileup, final Location location, final AbstractWindowIterator parallelPileupIterator) {
		Result result = new Result();
		result.setParellelPileup(parallelPileup);

		if (parameters.getFilterConfig().hasFiters()) {
			// apply each filter
			for (AbstractFilterFactory<?> filterFactory : parameters.getFilterConfig().getFactories()) {
				AbstractStorageFilter<?> storageFilter = filterFactory.createStorageFilter();
				storageFilter.applyFilter(result, location, parallelPileupIterator);
			}
		}

		return result;
	}

	@Override
	protected AbstractTwoConditionIterator buildIterator(Coordinate coordinate) {
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