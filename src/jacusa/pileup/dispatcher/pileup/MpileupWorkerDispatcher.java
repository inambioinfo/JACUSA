package jacusa.pileup.dispatcher.pileup;

import jacusa.cli.parameters.PileupParameters;

import jacusa.pileup.BasePileup;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.worker.MpileupWorker;
import jacusa.util.coordinateprovider.CoordinateProvider;

public class MpileupWorkerDispatcher extends AbstractWorkerDispatcher<BasePileup> {
	
	public MpileupWorkerDispatcher(
			final CoordinateProvider coordinateProvider, 
			final PileupParameters parameters) {
		super(coordinateProvider, 
				parameters);
	}

	@Override
	protected MpileupWorker buildNextWorker() {
		return new MpileupWorker(
				this, 
				this.getWorkerContainer().size(), 
				getParameters());
	}

	public PileupParameters getParameters() {
		return (PileupParameters) getParameters();
	}
	
}