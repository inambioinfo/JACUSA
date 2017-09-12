package jacusa.pileup.iterator;

import jacusa.JACUSA;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.data.ParallelPileupData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.DataBuilder;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.pileup.iterator.location.StrandedCoordinateAdvancer;
import jacusa.pileup.iterator.location.UnstrandedCoordinateAdvancer;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;
import jacusa.util.WindowCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class WindowIterator<T extends AbstractData> 
implements Iterator<Coordinate> {

	public static final int CONDITON_INDEX = 0;
	public static final int REPLICATE_INDEX = 0;
	
	private final Coordinate coordinate;
	private Variant<T> filter;
	
	private ParallelPileupData<T> parallelData;
	
	private final Map<Integer, List<DataBuilder<T>>> dataBuilders;
	
	private CoordinateAdvancer coordinateAdvancer;
	
	private AbstractParameters<T> parameters;
	
	public WindowIterator(
			final Coordinate coordinate, 
			final Variant<T> filter,
			final SAMFileReader[][] readers,
			final AbstractParameters<T> parameters) {
		this.coordinate = coordinate;

		this.filter	= filter;
		this.parallelData = new ParallelPileupData<T>(parameters.getMethodFactory());
		
		final int conditions = parameters.getConditions();

		this.dataBuilders = new HashMap<Integer, List<DataBuilder<T>>>(parameters.getConditions());

		final boolean[] isStranded = new boolean[conditions];
		final Coordinate[] coordinates = new Coordinate[conditions];
		
		boolean unStranded = true;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			ConditionParameters<T> condition = parameters.getConditionParameters(conditionIndex);
			
			List<DataBuilder<T>> conditionDataBuilders = createDataBuilders(
					condition.getDataBuilderFactory(), 
					coordinate, readers[conditionIndex], 
					parameters.getConditionParameters(conditionIndex), parameters);
			dataBuilders.put(conditionIndex, conditionDataBuilders);
			
			isStranded[conditionIndex] = condition.getDataBuilderFactory().isStranded();
			coordinates[conditionIndex] = 
					initCoordinates(coordinate, condition.getDataBuilderFactory().isStranded(), conditionDataBuilders);
			
			if (isStranded[conditionIndex]) {
				unStranded = false;
			}
		}

		if (unStranded) {
			coordinateAdvancer = new UnstrandedCoordinateAdvancer(coordinates);
		} else {
			coordinateAdvancer = new StrandedCoordinateAdvancer(isStranded, coordinates);
		}

		this.parameters = parameters;
	}

	protected Coordinate initCoordinates(Coordinate coordinate, 
			final boolean isStranded, 
			final List<DataBuilder<T>> dataBuilders) {
		parallelData.getCoordinate().setSequenceName(coordinate.getSequenceName());

		// Default value for: not within coordinate
		Coordinate newCoordinate = new Coordinate(coordinate.getSequenceName(), Integer.MAX_VALUE, STRAND.UNKNOWN);
		if (isStranded) {
			newCoordinate.setStrand(STRAND.FORWARD);
		}
		
		final SAMRecord record = getNextValidRecord(coordinate.getStart(), dataBuilders);
		if (record == null) {
			return newCoordinate;
		}

		// find genomicPosition within coordinate.getStart() coordinate.getEnd();
		int genomicPosition = Math.max(coordinate.getStart(), record.getAlignmentStart());
		if (genomicPosition > coordinate.getEnd()) {
			return newCoordinate;
		}

		genomicPosition = Math.min(genomicPosition, coordinate.getEnd());
		for (DataBuilder<T> pileupBuilder : dataBuilders) {
			pileupBuilder.adjustWindowStart(genomicPosition);
		}

		newCoordinate.setPosition(genomicPosition);
		return newCoordinate;
	}

	protected boolean hasNext(final int conditionIndex) {
		return hasNext(conditionIndex, dataBuilders.get(conditionIndex));
	}
	
	@Override
	public boolean hasNext() {
		final int conditions = parameters.getConditions();
		
		while (true) {

			// check that all conditions have coverage...
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				if (! hasNext(conditionIndex)) {
					return false;
				}
			}

			int check = 0;
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				final int compare = new Integer(coordinateAdvancer.get(CONDITON_INDEX).getPosition())
					.compareTo(coordinateAdvancer.get(conditionIndex).getPosition());
	
				switch (compare) {
	
				case -1:
					// adjust actualPosition; instead of iterating jump to specific position
					coordinateAdvancer.set(CONDITON_INDEX, coordinateAdvancer.get(conditionIndex));
					adjustCurrentGenomicPosition(conditionIndex, dataBuilders.get(CONDITON_INDEX));
					
					break;
	
				case 0:
					check++;
					break;
	
				case 1:
					// adjust actualPosition; instead of iterating jump to specific position
					coordinateAdvancer.set(conditionIndex, coordinateAdvancer.get(CONDITON_INDEX));
					adjustCurrentGenomicPosition(CONDITON_INDEX, dataBuilders.get(conditionIndex));

					break;
				}
			}

			if (check != conditions) {
				return false;
			}

			parallelData.setCoordinate(new Coordinate(coordinateAdvancer.get(CONDITON_INDEX)));
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				parallelData.setData(conditionIndex, getData(parallelData.getCoordinate(), dataBuilders.get(conditionIndex)));
			}

			if (filter.isValid(parallelData)) {
				return true;
			} else {
				coordinateAdvancer.advance();
				parallelData.reset();
			}				
		}
	}
	
	public Coordinate next() {
		Coordinate current = new Coordinate(coordinateAdvancer.get(CONDITON_INDEX));;

		// advance to the next position
		coordinateAdvancer.advance();

		return current;
	}
	
	public List<FilterContainer<T>> getFilterContainers(int conditionIndex, Coordinate coordinate) {
		return getFilterCaches(coordinate, dataBuilders.get(conditionIndex));
	}
	
	/**
	 * 
	 * @param pileupBuilderFactory
	 * @param coordinate
	 * @param readers
	 * @param parameters
	 * @return
	 */
	protected List<DataBuilder<T>> createDataBuilders(
			final AbstractDataBuilderFactory<T> pileupBuilderFactory,
			final Coordinate coordinate, 
			final SAMFileReader[] readers,
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {

		final List<DataBuilder<T>> dataBuilders = new ArrayList<DataBuilder<T>>(readers.length);

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

			dataBuilders.add(pileupBuilderFactory.newInstance(windowCoordinates, readers[i], 
					condition, parameters));
		}

		return dataBuilders;
	}

	protected SAMRecord getNextValidRecord(final int targetGenomicPosition, 
			final List<DataBuilder<T>> dataBuilders) {
		return dataBuilders.get(0).getNextValidRecord(targetGenomicPosition);
	}

	// Change here for more quantitative evaluation
	protected boolean isCovered(Coordinate coordinate, List<DataBuilder<T>> dataBuilders) {
		int windowPosition = dataBuilders.get(0)
				.getWindowCoordinates()
				.convert2WindowPosition(coordinate.getPosition());
		if (windowPosition < 0) {
			return false;
		}

		for (DataBuilder<T> dataBuilder : dataBuilders) {
			if (! dataBuilder.isCovered(windowPosition, coordinate.getStrand())) {
				return false;
			}
		}

		return true;
	}
	
	protected T[] getData(Coordinate coordinate, List<DataBuilder<T>> dataBuilders) {
		int n = dataBuilders.size();

		T[] data = parameters.getMethodFactory().createReplicateData(dataBuilders.size());
		int windowPosition = dataBuilders.get(0).getWindowCoordinates().convert2WindowPosition(coordinate.getPosition());
		for(int i = 0; i < n; ++i) {
			data[i] = dataBuilders.get(i).getData(windowPosition, coordinate.getStrand());
		}

		return data;
	}
	
	public int getAlleleCount(Coordinate coordinate) {
		Set<Integer> alleles = new HashSet<Integer>(4);
		
		for (int conditionIndex = 0; conditionIndex < dataBuilders.size(); conditionIndex++) {
			alleles.addAll(getAlleles(coordinate, dataBuilders.get(conditionIndex)));
		}

		return alleles.size();
	}
	
	public int getAlleleCount(final int conditionIndex, final Coordinate coordinate) {
		return getAlleleCount(coordinate, dataBuilders.get(conditionIndex));
	}
	
	protected int getAlleleCount(Coordinate coordinate, List<DataBuilder<T>> dataBuilders) {
		return getAlleles(coordinate, dataBuilders).size();		
	}
	
	protected Set<Integer> getAlleles(Coordinate coordinate, List<DataBuilder<T>> dataBuilders) {
		Set<Integer> alleles = new HashSet<Integer>(4);
		int replicates = dataBuilders.size();
		int windowPosition = dataBuilders.get(0).getWindowCoordinates().convert2WindowPosition(coordinate.getPosition());
		for (int i = 0; i < replicates; ++i) {
			for (int baseI : dataBuilders.get(i).getWindowCache(coordinate.getStrand()).getAlleles(windowPosition)) {
				alleles.add(baseI);
			}
		}
		return alleles;
	}

	protected List<FilterContainer<T>> getFilterCaches(Coordinate coordinate, List<DataBuilder<T>> pileupBuilders) {
		int replicates = pileupBuilders.size();

		List<FilterContainer<T>> filterContainers = new ArrayList<FilterContainer<T>>(replicates);
		int windowPosition = pileupBuilders.get(REPLICATE_INDEX).getWindowCoordinates().convert2WindowPosition(coordinate.getStart());
		for (int i = 0; i < replicates; ++i) {
			filterContainers.add(pileupBuilders.get(i).getFilterContainer(windowPosition, coordinate.getStrand()));
		}

		return filterContainers;
	}

	protected boolean adjustCurrentGenomicPosition(final int conditionIndex, final List<DataBuilder<T>> dataBuilders) {
		if (! dataBuilders.get(REPLICATE_INDEX).getWindowCoordinates().isContainedInWindow(
				coordinateAdvancer.get(conditionIndex).getStart())) {
			return adjustWindowStart(conditionIndex, dataBuilders);
		}

		return true;
	}
	
	protected boolean adjustWindowStart(final int conditionIndex, List<DataBuilder<T>> dataBuilders) {
		if (! dataBuilders.get(REPLICATE_INDEX).adjustWindowStart(coordinateAdvancer.get(conditionIndex).getStart())) {
			SAMRecord record = getNextValidRecord(dataBuilders.get(REPLICATE_INDEX).getWindowCoordinates().getGenomicWindowEnd(), dataBuilders);
			if (record == null) {
				return false;
			}
			coordinateAdvancer.get(conditionIndex).setPosition(record.getAlignmentStart());

			return adjustWindowStart(conditionIndex, dataBuilders);
		}

		coordinateAdvancer.get(conditionIndex).setPosition(
				dataBuilders.get(REPLICATE_INDEX).getWindowCoordinates().getGenomicWindowStart());
		for (int i = 1; i < dataBuilders.size(); ++i) {
			dataBuilders.get(i).adjustWindowStart(coordinateAdvancer.get(conditionIndex).getStart());
		}

		return true;
	}

	protected boolean hasNext(final int conditionIndex, final List<DataBuilder<T>> dataBuilders) {
		// within
		while (coordinateAdvancer.get(conditionIndex).getPosition() <= coordinate.getEnd()) {
			if (dataBuilders.get(0)
					.getWindowCoordinates()
					.isContainedInWindow(coordinateAdvancer.get(conditionIndex).getPosition())) {
				if (isCovered(coordinateAdvancer.get(conditionIndex), dataBuilders)) {
					return true;
				} else {
					// move along the window
					coordinateAdvancer.advance(conditionIndex);
				}
			} else {
				if (! adjustWindowStart(conditionIndex, dataBuilders)) {
					return false;
				}
			}
		}

		return false;
	}

	public AbstractParameters<T> getParameters() {
		return parameters;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public ParallelPileupData<T> getParallelData() {
		return parallelData;
	}
	
	protected void setParallelData(final ParallelPileupData<T> parallelData) {
		this.parallelData = parallelData;
	}
	
	@Override
	public void remove() {
		// not needed
	}
	
}
