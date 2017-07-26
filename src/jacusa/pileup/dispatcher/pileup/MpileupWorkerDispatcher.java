package jacusa.pileup.dispatcher.pileup;

import jacusa.cli.parameters.TwoConditionPileupParameters;

import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.MpileupWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

public class MpileupWorkerDispatcher extends AbstractWorkerDispatcher<MpileupWorker> {

	private final TwoConditionPileupParameters parameters;
	
	public MpileupWorkerDispatcher(
			String[] pathnames1, 
			String[] pathnames2,
			final CoordinateProvider coordinateProvider, 
			final TwoConditionPileupParameters parameters) {
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
	protected MpileupWorker buildNextWorker() {
		return new MpileupWorker(
				this, 
				this.getWorkerContainer().size(), 
				parameters
		);
	}

}