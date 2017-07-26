package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.TwoConditionCallParameters;
import jacusa.pileup.worker.TwoConditionCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class TwoConditionCallWorkerDispatcher extends AbstractCallWorkerDispatcher<TwoConditionCallWorker> {

	private TwoConditionCallParameters parameters;
	
	public TwoConditionCallWorkerDispatcher(
			String[] pathnames1, 
			String[] pathnames2,
			final CoordinateProvider coordinateProvider,
			final TwoConditionCallParameters parameters) throws IOException {
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
	protected TwoConditionCallWorker buildNextWorker() {
		return new TwoConditionCallWorker(
				this, 
				this.getWorkerContainer().size(),
				parameters);
	}

}