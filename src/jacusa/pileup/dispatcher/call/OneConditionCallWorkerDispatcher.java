package jacusa.pileup.dispatcher.call;

import jacusa.cli.parameters.OneConditionCallParameters;
import jacusa.pileup.worker.OneConditionCallWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;

public class OneConditionCallWorkerDispatcher extends AbstractCallWorkerDispatcher<OneConditionCallWorker> {

	private OneConditionCallParameters parameters;
	
	public OneConditionCallWorkerDispatcher(String[] pathnames1, String[] pathnames2, CoordinateProvider coordinateProvider, OneConditionCallParameters parameters) throws IOException {
		super(	pathnames1,
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
	protected OneConditionCallWorker buildNextWorker() {
		return new OneConditionCallWorker(
				this, 
				this.getWorkerContainer().size(),
				parameters
		);
	}

}