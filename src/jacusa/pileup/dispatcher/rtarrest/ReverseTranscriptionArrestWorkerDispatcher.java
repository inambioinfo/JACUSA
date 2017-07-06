package jacusa.pileup.dispatcher.rtarrest;

import jacusa.cli.parameters.ReverseTranscriptionArrestParameters;
import jacusa.pileup.worker.ReadCoverageWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

// TODO change name
public class ReverseTranscriptionArrestWorkerDispatcher extends AbstractReverseTranscriptionArrestWorkerDispatcher<ReadCoverageWorker> {

	private ReverseTranscriptionArrestParameters parameters;
	
	public ReverseTranscriptionArrestWorkerDispatcher(
			String[] pathnames1, 
			String[] pathnames2,
			final CoordinateProvider coordinateProvider,
			final ReverseTranscriptionArrestParameters parameters) throws IOException {
		super(
				pathnames1,
				pathnames2,
				coordinateProvider, 
				parameters.getMaxThreads(),
				parameters.getOutput(),
				parameters.getFormat(),
				parameters.isSeparate()
		);
		
		this.parameters = parameters;
	}

	@Override
	protected ReadCoverageWorker buildNextWorker() {
		return new ReadCoverageWorker(
				this, 
				this.getWorkerContainer().size(),
				parameters);
	}

}