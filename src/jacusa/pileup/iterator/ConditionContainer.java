package jacusa.pileup.iterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.samtools.SAMFileReader;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.AbstractData;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.pileup.iterator.location.CoordinateContainer;
import jacusa.pileup.iterator.location.StrandedCoordinateAdvancer;
import jacusa.pileup.iterator.location.UnstrandedCoordinateAdvancer;
import jacusa.util.Coordinate;

public class ConditionContainer<T extends AbstractData> {

	private Coordinate window;
	private CoordinateAdvancer referenceAdvancer;
	private Map<Integer, ReplicateContainer<T>> container;
	private AbstractParameters<T> parameters;
	
	public ConditionContainer(final Coordinate window, SAMFileReader[][] readers, final AbstractParameters<T> parameters) {
		this.window = window;
		this.parameters = parameters;
		container = init(window, readers, parameters);
		referenceAdvancer = createReferenceAdvancer(window, parameters.getConditions());
		init(referenceAdvancer.getCoordinate());
	}

	public ReplicateContainer<T> getReplicatContainer(final int conditionIndex) {
		return container.get(conditionIndex);
	}

	public T[][] getData(final Coordinate coordinate) {
		final int conditions = parameters.getConditions();

		final T[][] data = parameters.getMethodFactory().createContainer(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			data[conditionIndex] = getReplicatContainer(conditionIndex).getData(coordinate);
		}

		return data;
	}

	public int getAlleleCount(final Coordinate coordinate) {
		final int conditions = parameters.getConditions();
		final Set<Integer> alleles = new HashSet<Integer>(4);

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			alleles.addAll(getReplicatContainer(conditionIndex).getAlleles(coordinate));
		}

		return alleles.size();
	}
	
	public int getAlleleCount(final int conditionIndex, final Coordinate coordinate) {
		return getReplicatContainer(conditionIndex).getAlleles(coordinate).size();
	}

	public void advance() {
		referenceAdvancer.advance();
		if (checkReferenceWithinWindow()) {
			for (int conditionIndex = 0; conditionIndex < container.size(); conditionIndex++) {
				getCoordinateContainer(conditionIndex).advance(referenceAdvancer.getCoordinate());
			}
		}
	}
	
	public boolean hasNext() {
		while (checkReferenceWithinWindow()) {
			if (! isCovered(referenceAdvancer.getCoordinate())) {
				advance();
			} else {
				return true;
			}
		}
		
		return false;
	}

	public CoordinateAdvancer getReferenceAdvancer() {
		return referenceAdvancer;
	}

	public boolean checkReferenceWithinWindow() {
		return referenceAdvancer.getCoordinate().getPosition() >= window.getStart() &&
				referenceAdvancer.getCoordinate().getPosition() <= window.getEnd(); 		
	}
	
	private void init(final Coordinate target) {
		for (final ReplicateContainer<T> replicateContainer : container.values()) {
			replicateContainer.adjustBuilder(target);
		}
	}
	
	private boolean isCovered(final Coordinate coordinate) {
		for (final ReplicateContainer<T> replicateContainer : container.values()) {
			if (! replicateContainer.isCovered(coordinate)) {
				return false;
			}
		}

		return true;
	}
	
	private Map<Integer, ReplicateContainer<T>> init(final Coordinate window, 
			final SAMFileReader[][] readers, 
			final AbstractParameters<T> parameters) {
		final Map<Integer, ReplicateContainer<T>> res = new HashMap<Integer, ReplicateContainer<T>>(parameters.getConditions());

		for (int conditionIndex = 0; conditionIndex < parameters.getConditions(); conditionIndex++) {
			final ReplicateContainer<T> replicateContainer = new ReplicateContainer<T>(window, conditionIndex, readers[conditionIndex], parameters);
			res.put(conditionIndex, replicateContainer);
		}

		return res;
	}

	private CoordinateAdvancer createReferenceAdvancer(final Coordinate window, final int conditions) {
		final Coordinate coordinate = new Coordinate(window.getContig(), window.getPosition(), window.getStrand());
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			if (getReplicatContainer(conditionIndex).isStranded()) {
				return new StrandedCoordinateAdvancer(coordinate);
			}
		}
		
		return new UnstrandedCoordinateAdvancer(coordinate);
	}
	
	private CoordinateContainer getCoordinateContainer(final int conditionIndex) {
		return container.get(conditionIndex).getCoordinateContainer();
	}
	
	/*
	 * 			// check that all conditions have some coverage position
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				if (! hasNext(conditionIndex)) {
					return false;
				}
			}

			int check = 0;
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				final int refPos = coordinateContainer.getCoordinate(CONDITON_INDEX).getStart();
				final int condPos = coordinateContainer.getCoordinate(conditionIndex).getStart();
	
				int compare = 0;
				if (refPos < condPos) {
					compare = -1;
				} else if (refPos > condPos) {
					compare = 1;
				}

				switch (compare) {

				case -1:
					// adjust actualPosition; instead of iterating jump to specific position
					if (! adjustCurrentGenomicPosition(CONDITON_INDEX, condPos)) {
						return false;
					}
					
					break;
	
				case 0:
					if (isCovered(coordinateContainer.getCoordinate(conditionIndex), conditionDataBuilders.get(conditionIndex))) {
						check++;
					}
					
					break;
	
				case 1:
					// adjust actualPosition; instead of iterating jump to specific position
					if (! adjustCurrentGenomicPosition(conditionIndex, refPos)) {
						return false;					
					}

					break;
				}
			}
			
			*/
	
}