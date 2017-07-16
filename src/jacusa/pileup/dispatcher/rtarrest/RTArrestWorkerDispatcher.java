package jacusa.pileup.dispatcher.rtarrest;

import jacusa.cli.parameters.RTArrestParameters;
import jacusa.pileup.worker.RTArrestWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class RTArrestWorkerDispatcher extends AbstractRTArrestWorkerDispatcher<RTArrestWorker> {

	private RTArrestParameters parameters;
	
	public RTArrestWorkerDispatcher(
			String[] pathnames1, String[] pathnames2,
			final CoordinateProvider coordinateProvider,
			final RTArrestParameters parameters) throws IOException {
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
	protected RTArrestWorker buildNextWorker() {
		return new RTArrestWorker(
				this, 
				this.getWorkerContainer().size(),
				parameters);
	}

}