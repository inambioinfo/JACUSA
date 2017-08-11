package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.OneConditionCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class OneConditionCallWorkerDispatcher<T extends BaseQualData>
extends AbstractWorkerDispatcher<T> {

	public OneConditionCallWorkerDispatcher(CoordinateProvider coordinateProvider, 
			CallParameters<T> parameters) throws IOException {
		super(coordinateProvider, parameters);
	}

	@Override
	protected OneConditionCallWorker<T> buildNextWorker() {
		return new OneConditionCallWorker<T>(this, 
				this.getWorkerContainer().size(),
				getParameters());
	}

	public CallParameters<T> getParameters() {
		return (CallParameters<T>) super.getParameters();
	}
	
}