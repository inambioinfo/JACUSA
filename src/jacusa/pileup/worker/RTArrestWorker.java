package jacusa.pileup.worker;

import jacusa.cli.parameters.RTArrestParameters;

import jacusa.cli.parameters.SampleParameters;
import jacusa.pileup.dispatcher.rtarrest.RTArrestWorkerDispatcher;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.pileup.iterator.TwoSampleIterator;
import jacusa.pileup.iterator.variant.RTArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class RTArrestWorker extends AbstractRTArrestWorker {

	private SAMFileReader[] readers1;
	private SAMFileReader[] readers2;
	private RTArrestParameters parameters;
	
	private final Variant variant;
	
	public RTArrestWorker(
			final RTArrestWorkerDispatcher threadDispatcher,
			final int threadId,
			final RTArrestParameters parameters) {
		super(
				threadDispatcher, 
				threadId,
				parameters.getStatisticParameters(), 
				parameters
		);

		this.parameters = parameters;
		readers1 = initReaders(parameters.getSample1().getPathnames());
		readers2 = initReaders(parameters.getSample2().getPathnames());

		variant = new RTArrestVariantParallelPileup();
	}

	@Override
	protected AbstractWindowIterator buildIterator(final Coordinate coordinate) {
		SampleParameters sample1 = parameters.getSample1();
		SampleParameters sample2 = parameters.getSample2();
		
		return new TwoSampleIterator(coordinate, variant, readers1, readers2, sample1, sample2, parameters);
	}

	@Override
	protected void close() {
		close(readers1);
		close(readers2);
	}

}