package jacusa.pileup.worker;

import jacusa.cli.parameters.ReverseTranscriptionArrestParameters;

import jacusa.cli.parameters.SampleParameters;
import jacusa.pileup.dispatcher.rtarrest.ReverseTranscriptionArrestWorkerDispatcher;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.pileup.iterator.ReverseTranscriptionArrestSampleIterator;
import jacusa.pileup.iterator.variant.ReverseTranscriptionArrestVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

public class ReverseTranscriptionArrestWorker extends AbstractReverseTranscriptionArrestWorker {

	private SAMFileReader[] readers1;
	private SAMFileReader[] readers2;
	private ReverseTranscriptionArrestParameters parameters;
	
	private final Variant variant;
	
	public ReverseTranscriptionArrestWorker(
			final ReverseTranscriptionArrestWorkerDispatcher threadDispatcher,
			final int threadId,
			final ReverseTranscriptionArrestParameters parameters) {
		super(
				threadDispatcher, 
				threadId,
				parameters.getStatisticParameters(), 
				parameters
		);

		this.parameters = parameters;
		readers1 = initReaders(parameters.getSample1().getPathnames());
		readers2 = initReaders(parameters.getSample2().getPathnames());

		variant = new ReverseTranscriptionArrestVariantParallelPileup();
	}

	@Override
	protected AbstractWindowIterator buildIterator(final Coordinate coordinate) {
		SampleParameters sample1 = parameters.getSample1();
		SampleParameters sample2 = parameters.getSample2();
		
		return new ReverseTranscriptionArrestSampleIterator(coordinate, variant, readers1, readers2, sample1, sample2, parameters);
	}

	@Override
	protected void close() {
		close(readers1);
		close(readers2);
	}

}