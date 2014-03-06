package accusa2.process.parallelpileup.dispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMFileReader;
import accusa2.cli.Parameters;
import accusa2.io.output.TmpOutputWriter;
import accusa2.process.parallelpileup.worker.AbstractParallelPileupWorker;
import accusa2.util.AnnotatedCoordinate;
import accusa2.util.CoordinateProvider;

public abstract class AbstractParallelPileupWorkerDispatcher<T extends AbstractParallelPileupWorker> {

	protected CoordinateProvider coordinateProvider;
	protected final Parameters parameters;

	protected final List<T> threadContainer;
	protected TmpOutputWriter[] tmpOutputs;

	protected Integer comparisons;

	public AbstractParallelPileupWorkerDispatcher(CoordinateProvider coordinateProvider, Parameters parameters) {
		this.coordinateProvider = coordinateProvider;
		this.parameters = parameters;

		tmpOutputs 		= new TmpOutputWriter[parameters.getMaxThreads()];
		threadContainer = new ArrayList<T>(parameters.getMaxThreads());

		comparisons 	= 0;
	}

	/**
	 * 
	 * @param pathname
	 * @return
	 */
	private SAMFileReader initReader(String pathname) {
		SAMFileReader reader = new SAMFileReader(new File(pathname));
		// be silent
		reader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
		// disable memory mapping
		reader.enableIndexMemoryMapping(false);
		return reader;
	}

	public final int run() {
		synchronized (this) {

			while(hasNext() || !threadContainer.isEmpty()) {
				// clean finished threads
				for(int i = 0; i < threadContainer.size(); ++i) {
					T processParallelPileupThread = threadContainer.get(i);

					if(processParallelPileupThread.isFinished()) {
						processFinishedWorker(processParallelPileupThread);

						// remove from container and "kill"
						threadContainer.remove(processParallelPileupThread);
						processParallelPileupThread = null;
					}
				}

				// fill thread container
				while(threadContainer.size() < parameters.getMaxThreads() && hasNext()) {
					T processParallelPileupThread = buildNextParallelPileupWorker();
						threadContainer.add(processParallelPileupThread);
						processParallelPileupThread.start();
				}

				// 
				if(!hasNext() && threadContainer.isEmpty()) {
					break;
				}

				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// finally write the output and cleanup
		writeOuptut();
		return comparisons.intValue();		
	}

	abstract protected T buildNextParallelPileupWorker();
	abstract protected void processFinishedWorker(T processParallelPileupThread);

	abstract protected void writeOuptut();

	/**
	 * 
	 * @return
	 */
	public synchronized boolean hasNext() {
		return coordinateProvider.hasNext();
	}

	/**
	 * 
	 * @return
	 */
	public AnnotatedCoordinate next() {
		if(hasNext()) {
			return coordinateProvider.next();
		}
		
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public SAMFileReader[] createBAMFileReaders1() {
		return initReaders(parameters.getPathnames1());
	}

	/**
	 * 
	 * @return
	 */
	public SAMFileReader[] createBAMFileReaders2() {
		return initReaders(parameters.getPathnames2());
	}

	/**
	 * 
	 * @param pathnames
	 * @return
	 */
	private SAMFileReader[] initReaders(String[] pathnames) {
		SAMFileReader[] readers = new SAMFileReader[pathnames.length];
		for(int i = 0; i < pathnames.length; ++i) {
			readers[i] = initReader(pathnames[i]);
		}
		return readers;
	}

}
