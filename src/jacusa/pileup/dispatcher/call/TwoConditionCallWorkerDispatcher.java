package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.CallParameters;
import jacusa.pileup.BasePileup;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.TwoConditionCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class TwoConditionCallWorkerDispatcher extends AbstractWorkerDispatcher<BasePileup> {

	public TwoConditionCallWorkerDispatcher(
			final CoordinateProvider coordinateProvider,
			final CallParameters parameters) throws IOException {
		super(coordinateProvider, parameters);
	}

	@Override
	protected TwoConditionCallWorker buildNextWorker() {
		return new TwoConditionCallWorker(this,
				getWorkerContainer().size(),
				getParameters());
	}

	public CallParameters getParameters() {
		return (CallParameters)super.getParameters();
	}
	
}