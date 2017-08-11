package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.CallParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.TwoConditionCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class TwoConditionCallWorkerDispatcher<T extends BaseQualData> 
extends AbstractWorkerDispatcher<T> {

	public TwoConditionCallWorkerDispatcher(
			final CoordinateProvider coordinateProvider,
			final CallParameters<T> parameters) throws IOException {
		super(coordinateProvider, parameters);
	}

	@Override
	protected TwoConditionCallWorker<T> buildNextWorker() {
		return new TwoConditionCallWorker<T>(this,
				getWorkerContainer().size(),
				getParameters());
	}

	@Override
	public CallParameters<T> getParameters() {
		return (CallParameters<T>)super.getParameters();
	}
	
}
