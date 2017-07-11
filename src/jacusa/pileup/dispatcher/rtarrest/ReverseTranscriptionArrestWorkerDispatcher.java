package jacusa.pileup.dispatcher.rtarrest;

import jacusa.cli.parameters.ReverseTranscriptionArrestParameters;
import jacusa.pileup.worker.ReverseTranscriptionArrestWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class ReverseTranscriptionArrestWorkerDispatcher extends AbstractReverseTranscriptionArrestWorkerDispatcher<ReverseTranscriptionArrestWorker> {

	private ReverseTranscriptionArrestParameters parameters;
	
	public ReverseTranscriptionArrestWorkerDispatcher(
			String[] pathnames1, String[] pathnames2,
			final CoordinateProvider coordinateProvider,
			final ReverseTranscriptionArrestParameters parameters) throws IOException {
		super(
				pathnames1,	pathnames2,
				coordinateProvider, 
				parameters.getMaxThreads(),
				parameters.getOutput(),
				parameters.getFormat(),
				parameters.isSeparate()
		);

		this.parameters = parameters;
	}

	@Override
	protected ReverseTranscriptionArrestWorker buildNextWorker() {
		return new ReverseTranscriptionArrestWorker(
				this, 
				this.getWorkerContainer().size(),
				parameters);
	}

}