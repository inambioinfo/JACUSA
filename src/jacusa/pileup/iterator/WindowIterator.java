package jacusa.pileup.iterator;

import jacusa.JACUSA;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.data.ParallelPileupData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.AbstractPileupBuilder;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.iterator.location.LocationAdvancer;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.Location;
import jacusa.util.WindowCoordinates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class WindowIterator<T extends AbstractData> 
implements Iterator<Location> {

	protected final Coordinate coordinate;
	protected Variant<T> filter;
	
	protected ParallelPileupData<T> parallelData;
	
	protected final AbstractPileupBuilder<T>[][] pileupBuilders;
	
	protected LocationAdvancer locationAdvancer;
	
	protected AbstractParameters<T> parameters;
	
	public WindowIterator(
			final Coordinate coordinate, 
			final Variant<T> filter,
			final AbstractPileupBuilder<T>[][] pileupBuilders,
			final SAMFileReader[][] readers,
			final AbstractParameters<T> parameters) {
		this.coordinate = coordinate;

		this.filter	= filter;
		this.parallelData = new ParallelPileupData<T>();

		final int conditions = parameters.getConditions();
		
		this.pileupBuilders = parameters.getMethodFactory().
		
		final boolean[] isStranded = new boolean[conditions];
		final Location[] locactions = new Location[conditions];
		
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			ConditionParameters<T> condition = parameters.getConditionParameters(conditionIndex);
			
			pileupBuilders[conditionIndex] = createPileupBuilders(
					condition.getPileupBuilderFactory(), 
					coordinate, readers[conditionIndex], parameters);
			
			isStranded[conditionIndex] = condition.getPileupBuilderFactory().isStranded();
			locactions[conditionIndex] = initLocation(coordinate, condition.getPileupBuilderFactory().isStranded(), pileupBuilders[conditionIndex]);
		}

		locationAdvancer = new LocationAdvancer(isStranded, locactions);

		this.parameters = parameters;
	}

	protected Location initLocation(Coordinate coordinate, 
			final boolean isDirectional, 
			final AbstractPileupBuilder<T>[] pileupBuilders) {
		parallelData.setContig(coordinate.getSequenceName());

		// Default value for: not within coordinate
		Location location = new Location(coordinate.getSequenceName(), Integer.MAX_VALUE, STRAND.UNKNOWN);
		if (isDirectional) {
			location.strand = STRAND.FORWARD;
		}
		
		final SAMRecord record = getNextValidRecord(coordinate.getStart(), pileupBuilders);
		if (record == null) {
			return location;
		}

		// find genomicPosition within coordinate.getStart() coordinate.getEnd();
		int genomicPosition = Math.max(coordinate.getStart(), record.getAlignmentStart());
		if (genomicPosition > coordinate.getEnd()) {
			return location;
		}

		genomicPosition = Math.min(genomicPosition, coordinate.getEnd());
		for (AbstractPileupBuilder<T> pileupBuilder : pileupBuilders) {
			pileupBuilder.adjustWindowStart(genomicPosition);
		}

		location.genomicPosition = genomicPosition;
		return location;
	}

	protected boolean hasNext(final int conditionIndex) {
		return hasNext(locationAdvancer.getLocation(conditionIndex), pileupBuilders[conditionIndex]);
	}
	
	@Override
	public boolean hasNext() {
		final Location[] locs = locationAdvancer.getLocations();
		
		final int conditions = locs.length;
		
		while (true) {
			// check that all conditions have coverage...
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				if (! hasNext(conditionIndex)) {
					return false;
				}
			}
			
			int check = 0;
			for (int conditionIndex = 1; conditionIndex < conditions; conditionIndex++) {
				final int compare = new Integer(locs[0].genomicPosition).compareTo(locs[conditionIndex].genomicPosition);
	
				switch (compare) {
	
				case -1:
					// adjust actualPosition; instead of iterating jump to specific position
					locationAdvancer.setLocation(0, locs[conditionIndex]);
					adjustCurrentGenomicPosition(locs[conditionIndex], pileupBuilders[0]);
					
					break;
	
				case 0:
					check++;
					break;
	
				case 1:
					// adjust actualPosition; instead of iterating jump to specific position
					locationAdvancer.setLocation(conditionIndex, locs[0]);
					adjustCurrentGenomicPosition(locs[0], pileupBuilders[conditionIndex]);
	
					break;
				}
			}
			if (check == conditions) {
				if (! locationAdvancer.checkStrand()) {
					for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
						locs[conditionIndex].strand = STRAND.REVERSE;
					}
					
					for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
						if (! isCovered(locs[conditionIndex], pileupBuilders[conditionIndex])) {
							locationAdvancer.advance();
							break;
						}
					}
				}
				final Location location = locationAdvancer.getLocation();
				
				parallelData.setContig(coordinate.getSequenceName());
				parallelData.setStart(location.genomicPosition);
				parallelData.setEnd(parallelData.getStart());
				// parallelData.setStrand(location.strand);

				for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
					locs[conditionIndex].strand = STRAND.REVERSE;
					parallelData.setData(conditionIndex, getData(location, pileupBuilders[conditionIndex]));
				}

				if (filter.isValid(parallelData)) {
					return true;
				} else {
					// reset
					//parallelData.setPileups1(new Pileup[0]);
					//parallelData.setPileups2(new Pileup[0]);
					parallelData.reset();
					for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
						locs[conditionIndex].strand = STRAND.REVERSE;
					}

					locationAdvancer.advance();
				}				
			}
			
			return false;
		}
	}

	
	public Location next() {
		Location current = new Location(locationAdvancer.getLocation());;

		// advance to the next position
		locationAdvancer.advance();

		return current;
	}
	
	public List<FilterContainer<T>> getFilterContainers(int conditionIndex, Location location) {
		return getFilterCaches(location, pileupBuilders[conditionIndex]);
	}
	
	/**
	 * 
	 * @param pileupBuilderFactory
	 * @param coordinate
	 * @param readers
	 * @param parameters
	 * @return
	 */
	protected AbstractPileupBuilder<T>[] createPileupBuilders(
			final AbstractDataBuilderFactory<T> pileupBuilderFactory,
			final Coordinate coordinate, 
			final SAMFileReader[] readers, 
			final AbstractParameters<T> parameters) {

		@SuppressWarnings("unchecked")
		final AbstractPileupBuilder<T>[] dataBuilders = (AbstractPileupBuilder<T>[]) new Object[readers.length];
		
		for(int i = 0; i < readers.length; ++i) {
			final int sequenceLength = readers[i].getFileHeader().getSequence(coordinate.getSequenceName()).getSequenceLength();
			if (coordinate.getEnd() > sequenceLength) {
				Coordinate samHeader = new Coordinate(coordinate.getSequenceName(), 1, sequenceLength);
				JACUSA.printWarning("Coordinates in BED file (" + coordinate.toString() + ") exceed SAM sequence header (" + samHeader.toString()+ ").");
			}
			final WindowCoordinates windowCoordinates = new WindowCoordinates(
					coordinate.getSequenceName(), 
					coordinate.getStart(), 
					parameters.getWindowSize(), 
					coordinate.getEnd());
			
			dataBuilders[i] = pileupBuilderFactory.newInstance(
					windowCoordinates, readers[i], 
					parameters.getConditionParameters(i), parameters);
		}

		return dataBuilders;
	}

	protected SAMRecord getNextValidRecord(final int targetGenomicPosition, 
			final AbstractPileupBuilder<T>[] pileupBuilders) {
		return pileupBuilders[0].getNextValidRecord(targetGenomicPosition);
	}

	// Change here for more quantitative evaluation
	protected boolean isCovered(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		int windowPosition = pileupBuilders[0]
				.getWindowCoordinates()
				.convert2WindowPosition(location.genomicPosition);
		if (windowPosition < 0) {
			return false;
		}

		for (AbstractPileupBuilder<T> pileupBuilder : pileupBuilders) {
			if (! pileupBuilder.isCovered(windowPosition, location.strand)) {
				return false;
			}
		}

		return true;
	}
	
	protected T[] getData(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		int n = pileupBuilders.length;

		T[] data = parameters.getMethodFactory().createDataContainer(pileupBuilders.length);
		int windowPosition = pileupBuilders[0].getWindowCoordinates().convert2WindowPosition(location.genomicPosition);
		for(int i = 0; i < n; ++i) {
			data[i] = pileupBuilders[i].getData(windowPosition, location.strand);
		}

		return data;
	}
	
	public int getAlleleCount(Location location) {
		Set<Integer> alleles = new HashSet<Integer>(4);
		
		for (int conditionIndex = 0; conditionIndex < pileupBuilders.length; conditionIndex++) {
			alleles.addAll(getAlleles(location, pileupBuilders[conditionIndex]));
		}

		return alleles.size();
	}
	
	public int getAlleleCount(final int conditionIndex, final Location location) {
		return getAlleleCount(location, pileupBuilders[conditionIndex]);
	}
	
	protected int getAlleleCount(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		return getAlleles(location, pileupBuilders).size();		
	}
	
	protected Set<Integer> getAlleles(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		Set<Integer> alleles = new HashSet<Integer>(4);
		int replicates = pileupBuilders.length;
		int windowPosition = pileupBuilders[0].getWindowCoordinates().convert2WindowPosition(location.genomicPosition);
		for (int i = 0; i < replicates; ++i) {
			for (int baseI : pileupBuilders[i].getWindowCache(location.strand).getAlleles(windowPosition)) {
				alleles.add(baseI);
			}
		}
		return alleles;
	}

	protected List<FilterContainer<T>> getFilterCaches(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		int replicates = pileupBuilders.length;

		List<FilterContainer<T>> filterContainers = new ArrayList<FilterContainer<T>>(replicates);
		int windowPosition = pileupBuilders[0].getWindowCoordinates().convert2WindowPosition(location.genomicPosition);
		for (int i = 0; i < replicates; ++i) {
			filterContainers.add(pileupBuilders[i].getFilterContainer(windowPosition, location.strand));
		}

		return filterContainers;
	}

	protected boolean adjustCurrentGenomicPosition(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		if (! pileupBuilders[0].getWindowCoordinates().isContainedInWindow(location.genomicPosition)) {
			return adjustWindowStart(location, pileupBuilders);
		}

		return true;
	}

	protected boolean adjustWindowStart(Location location, AbstractPileupBuilder<T>[] pileupBuilders) {
		if (! pileupBuilders[0].adjustWindowStart(location.genomicPosition)) {
			SAMRecord record = getNextValidRecord(pileupBuilders[0].getWindowCoordinates().getGenomicWindowEnd(), pileupBuilders);
			if (record == null) {
				return false;
			}
			location.genomicPosition = record.getAlignmentStart();

			return adjustWindowStart(location, pileupBuilders);
		}
		location.genomicPosition = pileupBuilders[0]
				.getWindowCoordinates()
				.getGenomicWindowStart();
		for (int i = 1; i < pileupBuilders.length; ++i) {
			pileupBuilders[i].adjustWindowStart(location.genomicPosition);
		}

		return true;
	}

	protected boolean hasNext(Location location, final AbstractPileupBuilder<T>[] pileupBuilders) {
		// within
		while (location.genomicPosition <= coordinate.getEnd()) {
			if (pileupBuilders[0]
					.getWindowCoordinates()
					.isContainedInWindow(location.genomicPosition)) {
				if (isCovered(location, pileupBuilders)) {
					return true;
				} else {
					// move along the window
					locationAdvancer.advanceLocation(location);
				}
			} else {
				if (! adjustWindowStart(location, pileupBuilders)) {
					return false;
				}
			}
		}

		return false;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public ParallelPileupData<T> getParallelData() {
		return parallelData;
	}
		
	@Override
	public void remove() {
		// not needed
	}
	
}
