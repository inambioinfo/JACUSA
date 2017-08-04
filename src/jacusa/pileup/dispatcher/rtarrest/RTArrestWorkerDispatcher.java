package jacusa.pileup.dispatcher.rtarrest;

import jacusa.cli.parameters.RTArrestParameters;
import jacusa.pileup.BaseReadPileup;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.RTArrestWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class RTArrestWorkerDispatcher extends AbstractWorkerDispatcher<BaseReadPileup> {

	public RTArrestWorkerDispatcher(
			final CoordinateProvider coordinateProvider,
			final RTArrestParameters parameters) throws IOException {
		super(coordinateProvider, 
				parameters);
	}

	@Override
	protected RTArrestWorker buildNextWorker() {
		return new RTArrestWorker(
				this, 
				this.getWorkerContainer().size(),
				getParameters());
	}

	public RTArrestParameters getParameters() {
		return (RTArrestParameters) super.getParameters();
	}
	
}