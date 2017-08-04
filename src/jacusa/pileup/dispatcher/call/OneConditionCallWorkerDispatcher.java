package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.CallParameters;
import jacusa.pileup.BasePileup;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.OneConditionCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class OneConditionCallWorkerDispatcher extends AbstractWorkerDispatcher<BasePileup> {

	public OneConditionCallWorkerDispatcher(CoordinateProvider coordinateProvider, 
			CallParameters parameters) throws IOException {
		super(coordinateProvider,
				parameters);
	}

	@Override
	protected OneConditionCallWorker buildNextWorker() {
		return new OneConditionCallWorker(this, 
				this.getWorkerContainer().size(),
				getParameters());
	}

	public CallParameters getParameters() {
		return (CallParameters) super.getParameters();
	}
	
}