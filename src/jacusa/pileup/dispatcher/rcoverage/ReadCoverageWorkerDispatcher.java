package jacusa.pileup.dispatcher.rcoverage;

import jacusa.cli.parameters.ReadCoverageParameters;
import jacusa.pileup.worker.ReadCoverageWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

// TODO change name
public class ReadCoverageWorkerDispatcher extends AbstractReadCoverageWorkerDispatcher<ReadCoverageWorker> {

	private ReadCoverageParameters parameters;
	
	public ReadCoverageWorkerDispatcher(
			String[] pathnames1, 
			String[] pathnames2,
			final CoordinateProvider coordinateProvider,
			final ReadCoverageParameters parameters) throws IOException {
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