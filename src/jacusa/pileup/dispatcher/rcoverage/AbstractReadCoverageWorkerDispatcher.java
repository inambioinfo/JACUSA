package jacusa.pileup.dispatcher.rcoverage;

import jacusa.io.Output;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.AbstractReadCoverageWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

// TODO change name
public abstract class AbstractReadCoverageWorkerDispatcher<T extends AbstractReadCoverageWorker> extends AbstractWorkerDispatcher<T> {
	
	public AbstractReadCoverageWorkerDispatcher(
			final String[] pathnames1,
			final String[] pathnames2,
			final CoordinateProvider coordinateProvider, 
			final int maxThreads,
			final Output output,
			final AbstractOutputFormat format,
			final boolean separate) throws IOException {
		super(pathnames1, pathnames2, coordinateProvider, maxThreads, output, format, separate);
	}

}