package jacusa.pileup.iterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

import jacusa.JACUSA;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.DataBuilder;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.pileup.iterator.location.CoordinateContainer;
import jacusa.util.Coordinate;
import jacusa.util.WindowCoordinates;
import jacusa.util.Coordinate.STRAND;

public class ReplicateContainer<T extends AbstractData> {

	private final int conditionIndex;
	private final AbstractParameters<T> parameters;
	
	private final List<DataBuilder<T>> builders;
	private CoordinateContainer coordinateContainer;

	public ReplicateContainer(final Coordinate window, 
			final int conditionIndex,
			final SAMFileReader[] readers,
			final AbstractParameters<T> parameters) {
		this.conditionIndex = conditionIndex;
		this.parameters 	= parameters;

		builders 			= createBuilders(window, conditionIndex, readers, parameters);
		coordinateContainer = new CoordinateContainer(builders.toArray(new CoordinateAdvancer[builders.size()]));
	}

	public CoordinateContainer getCoordinateContainer() {
		return coordinateContainer;
	}

	public T[] getData(final Coordinate coordinate) {
		int replicates = builders.size();

		// create new container array
		T[] data = parameters.getMethodFactory().createReplicateData(builders.size());
		for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
			final int windowPosition = builders.get(replicateIndex)
					.getWindowCoordinates()
					.convert2WindowPosition(coordinate.getStart());
			data[replicateIndex] = builders.get(replicateIndex)
					.getData(windowPosition, coordinate.getStrand());
		}

		return data;
	}

	public List<FilterContainer<T>> getFilterContainers(final Coordinate coordinate) {
		int replicates = builders.size();

		List<FilterContainer<T>> filterContainers = new ArrayList<FilterContainer<T>>(replicates);
		
		for (final DataBuilder<T> builder : builders) {
			final int windowPosition = builder.getWindowCoordinates()
					.convert2WindowPosition(coordinate.getStart());

			filterContainers.add(builder.getFilterContainer(windowPosition, coordinate.getStrand()));
		}

		return filterContainers;
	}
	
	private List<DataBuilder<T>> createBuilders(final Coordinate target, 
			final int conditionIndex, final SAMFileReader[] readers,
			final AbstractParameters<T> parameters) {
		final ConditionParameters<T> condition = parameters.getConditionParameters(conditionIndex);
		final List<DataBuilder<T>> builders = new ArrayList<DataBuilder<T>>(readers.length);

		final String contig = target.getContig();
		for (int replicateIndex = 0; replicateIndex < readers.length; ++replicateIndex) {
			final int sequenceLength = readers[replicateIndex]
					.getFileHeader()
					.getSequence(contig)
					.getSequenceLength();
	
			if (target.getEnd() > sequenceLength) {
				Coordinate samHeader = new Coordinate(target.getContig(), 1, sequenceLength);
				JACUSA.printWarning("Coordinates in BED file (" + target.toString() + 
						") exceed SAM sequence header (" + samHeader.toString()+ ").");
			}
	
			final WindowCoordinates windowCoordinates = new WindowCoordinates(
					target.getContig(), 
					target.getStart(), 
					parameters.getWindowSize(), 
					target.getEnd());

			DataBuilder<T> builder = condition.getDataBuilderFactory()
					.newInstance(windowCoordinates, readers[replicateIndex], condition, parameters);
			builders.add(builder);
		}

		return builders;
	}

	public boolean isStranded() {
		for (final DataBuilder<T> builder : builders) {
			if (builder.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * 	protected boolean adjustWindowStart(final int conditionIndex, final List<DataBuilder<T>> dataBuilders) {
		final DataBuilder<T> refDataBuilder = dataBuilders.get(REPLICATE_INDEX);
		final Coordinate coordinate = coordinateContainer.getCoordinate(conditionIndex);
		
		if (! refDataBuilder.adjustWindowStart(coordinate.getStart())) {
			// TODO should't this be maxGenomic or higher than start + windowSize
			// final int genomicWindowEnd = refDataBuilder.getWindowCoordinates().getGenomicWindowEnd();
			SAMRecord record = getNextValidRecord(window.getEnd(), dataBuilders);
			if (record == null) {
				return false;
			}
			
			coordinateContainer.adjustPosition(conditionIndex, record.getAlignmentStart());

			return adjustWindowStart(conditionIndex, dataBuilders);
		}

		// TODO threads and this does not work
		for (int replicateIndex = 1; replicateIndex < dataBuilders.size(); ++replicateIndex) {
			dataBuilders.get(replicateIndex).adjustWindowStart(coordinate.getStart());
		}
		
		return true;
	}

	protected boolean adjustCurrentGenomicPosition(final int conditionIndex, final int targetPosition) {
		coordinateContainer.adjustPosition(conditionIndex, targetPosition);
		final WindowCoordinates windowCoordinates = conditionDataBuilders.get(conditionIndex).get(REPLICATE_INDEX).getWindowCoordinates();
		if (! windowCoordinates.isContainedInWindow(coordinateContainer.getCoordinate(conditionIndex).getStart())) {
			return adjustWindowStart(conditionIndex, conditionDataBuilders.get(conditionIndex));
		}

		return true;
	}
	 */
	
	public Set<Integer> getAlleles(final Coordinate coordinate) {
		final Set<Integer> alleles = new HashSet<Integer>(4);

		for (final DataBuilder<T> builder : builders) {
			final int windowPosition = builder.getWindowCoordinates().convert2WindowPosition(coordinate.getStart());
			for (int baseIndex : builder.getWindowCache(coordinate.getStrand()).getAlleles(windowPosition)) {
				alleles.add(baseIndex);
			}
		}

		return alleles;
	}
	
	public void adjustBuilder(final Coordinate target) {
		int position = Integer.MIN_VALUE; 
		for (final DataBuilder<T> builder : builders) {
			final SAMRecord record = builder.getNextValidRecord(target.getPosition());

			if (record == null) {
				return; // TODO
			}

			// find genomicPosition within coordinate.getStart() coordinate.getEnd();
			int genomicPosition = Math.max(target.getPosition(), record.getAlignmentStart());
			if (position < 0) {
				position = genomicPosition;
			} else {
				position = Math.min(position, genomicPosition);
			}
			builder.adjustPosition(genomicPosition, STRAND.FORWARD);
		}

		if (position != Integer.MIN_VALUE) {
			target.setPosition(position);
		}
	}

	// Change here for more quantitative evaluation
	public boolean isCovered(final Coordinate coordinate) {
		for (final DataBuilder<T> builder : builders) {
			if (! isCovered(coordinate, builder)) {
				return false;
			}
		}

		return true;
	}

	private boolean isCovered(final Coordinate coordinate, final DataBuilder<T> builder) {
		final int windowPosition = builder.getWindowCoordinates().convert2WindowPosition(coordinate.getStart());

		if (windowPosition < 0) {
			return false;
		}

		return builder.getCoverage(windowPosition, coordinate.getStrand()) >= getCondition().getMinCoverage();
	}
	
	private ConditionParameters<T> getCondition() {
		return parameters.getConditionParameters(conditionIndex);
	}

}
