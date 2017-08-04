package jacusa.pileup.worker;

import jacusa.JACUSA;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.pileup.Data;
import jacusa.pileup.ParallelData;
import jacusa.pileup.Result;
import jacusa.pileup.hasBaseCount;
import jacusa.pileup.hasCoordinate;
import jacusa.pileup.hasRefBase;
import jacusa.pileup.dispatcher.AbstractWorkerDispatcher;
import jacusa.pileup.iterator.WindowIterator;
import jacusa.util.Coordinate;
import jacusa.util.Location;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import net.sf.samtools.SAMFileReader;

public abstract class AbstractWorker<T extends Data<T> & hasBaseCount & hasCoordinate & hasRefBase> extends Thread {

	public static enum STATUS {INIT, READY, FINISHED, BUSY};

	private Coordinate coordinate;
	protected WindowIterator<T> parallelDataIterator;

	protected AbstractWorkerDispatcher<T> workerDispatcher;

	private final int threadId;

	private STATUS status;
	protected int comparisons;

	private GZIPOutputStream zip;
	
	protected SAMFileReader[][] readers;
	
	private AbstractParameters<T> parameters;
	
	public AbstractWorker(final AbstractWorkerDispatcher<T> workerDispatcher,
			final int threadId,
			final AbstractParameters<T> parameters) {
		this.workerDispatcher 		= workerDispatcher;
		this.threadId				= threadId;

		readers = new SAMFileReader[parameters.getConditions()][];
		for (int conditionIndex = 0; conditionIndex < readers.length; conditionIndex++) {
			readers[conditionIndex] = initReaders(parameters.getConditionParameters(conditionIndex).getPathnames());
		}
		this.parameters				= parameters;
		
		status 						= STATUS.INIT;
		comparisons 				= 0;
		
		String filename = parameters.getOutput().getInfo() + "_" + threadId + "_tmp.gz";
		try {
			zip = new GZIPOutputStream(new FileOutputStream(filename), 10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public final void run() {
		while (status != STATUS.FINISHED) {
			switch (status) {

			case READY:
				synchronized (this) {
					
					status = STATUS.BUSY;
					parallelDataIterator = buildIterator(coordinate);
					processParallelDataIterator(parallelDataIterator);
					status = STATUS.INIT;
					
					synchronized (workerDispatcher) {
						if (workerDispatcher.hasNext()) {
							if (parameters.getMaxThreads() > 0 && workerDispatcher.getThreadIds().size() > 0) {
								try {
									zip.write("##\n".getBytes());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			break;
				
			case INIT:
				Coordinate coordinate = null;
				synchronized (workerDispatcher) {
					if (workerDispatcher.hasNext()) {
						workerDispatcher.getThreadIds().add(getThreadId());
						coordinate = workerDispatcher.next();
					}
				}
				synchronized (this) {
					if (coordinate == null) {
						setStatus(STATUS.FINISHED);
					} else {
						setCoordinate(coordinate);
						setStatus(STATUS.READY);
					}
				}
				break;

			
			default:
				break;
			}
		}

		try {
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		synchronized (workerDispatcher) {
			workerDispatcher.notify();
		}

	}

	public int getThreadId() {
		return threadId;
	}

	/**
	 * 
	 * @param pathnames
	 * @return
	 */
	protected SAMFileReader[] initReaders(String[] pathnames) {
		SAMFileReader[] readers = new SAMFileReader[pathnames.length];
		for(int i = 0; i < pathnames.length; ++i) {
			readers[i] = initReader(pathnames[i]);
		}
		return readers;
	}

	/**
	 * 
	 * @param pathname
	 * @return
	 */
	protected SAMFileReader initReader(final String pathname) {
		SAMFileReader reader = new SAMFileReader(new File(pathname));
		// be silent
		reader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
		// disable memory mapping
		reader.enableIndexMemoryMapping(false);
		return reader;
	}

	protected abstract Result<T> processParallelData(final ParallelData<T> parallelData, 
			final Location location, 
			final WindowIterator<T> parallelPileupIterator);
	
	/**
	 * 
	 * @param parallelDataIterator
	 */
	protected synchronized void processParallelDataIterator(final WindowIterator<T> parallelDataIterator) {
		// print informative log
		JACUSA.printLog("Started screening contig " + 
				parallelDataIterator.getCoordinate().getSequenceName() + 
				":" + 
				parallelDataIterator.getCoordinate().getStart() + 
				"-" + 
				parallelDataIterator.getCoordinate().getEnd());

		// iterate over parallel pileups
		while (parallelDataIterator.hasNext()) {
			final Location location = parallelDataIterator.next();
			final ParallelData<T> parallelPileup = parallelDataIterator.getParallelData().copy();
			final Result<T> result = processParallelData(parallelPileup, location, parallelDataIterator);

			// considered comparisons

			comparisons++;

			if (result == null) {
				continue;
			}

			final String line = parameters.getFormat().convert2String(result);
			try {
				char c = 'F';
				if (! result.getFilterInfo().isEmpty()) {
					c = 'T';
				}
				final String s = new String(line + c + "\n"); 
				zip.write(s.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void close() {
		for (int conditionIndex = 0; conditionIndex < readers.length; conditionIndex++) {
			for (final SAMFileReader reader : readers[conditionIndex]) {
				reader.close();
			}
			readers[conditionIndex] = new SAMFileReader[0];
		}
	}

	/**
	 * 
	 * @param coordinate
	 * @param parameters
	 * @return
	 */
	protected abstract WindowIterator<T> buildIterator(Coordinate coordinate);
	
	public final int getComparisons() {
		return comparisons;
	}

	public STATUS getStatus() {
		return status;
	}
	
	public void setStatus(STATUS status) {
		this.status = status;
	}

	public AbstractParameters<T> getParameters() {
		return parameters;
	}
	
}