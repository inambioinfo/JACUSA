package jacusa.pileup.worker;

import jacusa.cli.parameters.ReadCoverageParameters;

import jacusa.cli.parameters.SampleParameters;
import jacusa.pileup.dispatcher.rcoverage.ReadCoverageWorkerDispatcher;
import jacusa.pileup.iterator.AbstractWindowIterator;
import jacusa.pileup.iterator.ReadCoverageSampleIterator;
import jacusa.pileup.iterator.variant.ReadCoverageVariantParallelPileup;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

// TODO change name
public class ReadCoverageWorker extends AbstractReadCoverageWorker {

	private SAMFileReader[] readers1;
	private SAMFileReader[] readers2;
	private ReadCoverageParameters parameters;
	
	private final Variant variant;
	
	public ReadCoverageWorker(
			final ReadCoverageWorkerDispatcher threadDispatcher,
			final int threadId,
			final ReadCoverageParameters parameters) {
		super(
				threadDispatcher, 
				threadId,
				parameters.getStatisticParameters(), 
				parameters
		);

		this.parameters = parameters;
		readers1 = initReaders(parameters.getSample1().getPathnames());
		readers2 = initReaders(parameters.getSample2().getPathnames());

		variant = new ReadCoverageVariantParallelPileup();
	}

	@Override
	protected AbstractWindowIterator buildIterator(final Coordinate coordinate) {
		SampleParameters sample1 = parameters.getSample1();
		SampleParameters sample2 = parameters.getSample2();
		
		return new ReadCoverageSampleIterator(coordinate, variant, readers1, readers2, sample1, sample2, parameters);
	}

	@Override
	protected void close() {
		close(readers1);
		close(readers2);
	}

}