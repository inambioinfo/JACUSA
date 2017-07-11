package jacusa.pileup.dispatcher.rtarrest;

import jacusa.io.Output;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.AbstractReverseTranscriptionArrestWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public abstract class AbstractReverseTranscriptionArrestWorkerDispatcher<T extends AbstractReverseTranscriptionArrestWorker> extends AbstractWorkerDispatcher<T> {
	
	public AbstractReverseTranscriptionArrestWorkerDispatcher(
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