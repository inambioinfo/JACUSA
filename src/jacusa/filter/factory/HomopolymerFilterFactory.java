package jacusa.filter.factory;

import java.util.HashSet;
import java.util.Set;
import net.sf.samtools.CigarOperator;
import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.HomopolymerStorageFilter;
import jacusa.filter.storage.HomopolymerFilterStorage;
import jacusa.util.WindowCoordinates;

public class HomopolymerFilterFactory<T extends BaseQualData> 
extends AbstractFilterFactory<T> {

	private static int LENGTH = 7;
	private int length;
	private AbstractParameters<T> parameters;
	
	private static Set<CigarOperator> cigarOperator = new HashSet<CigarOperator>();
	static {
		cigarOperator.add(CigarOperator.M);
	}
	
	public HomopolymerFilterFactory(final AbstractParameters<T> parameters) {
		super('Y', "Filter wrong variant calls within homopolymers. " +
				"Default: " + LENGTH + " (Y:length)", cigarOperator);
		this.parameters = parameters;
		length = LENGTH;
	}
	
	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if(line.length() == 1) {
			throw new IllegalArgumentException("Invalid argument " + line);
		}

		String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));
		// format Y:length
		for (int i = 1; i < s.length; ++i) {
			int value = Integer.valueOf(s[i]);

			switch(i) {
			case 1:
				setLength(value);
				break;

			default:
				throw new IllegalArgumentException("Invalid argument " + length);
			}
		}
	}

	@Override
	public HomopolymerFilterStorage createFilterStorage(
			final WindowCoordinates windowCoordinates, 
			final ConditionParameters<T> condition) {
		return new HomopolymerFilterStorage(getC(), 
				length, windowCoordinates, condition, parameters.getWindowSize(), 
				parameters.getBaseConfig());
	}

	@Override
	public HomopolymerStorageFilter<T> createStorageFilter() {
		return new HomopolymerStorageFilter<T>(getC(), parameters);
	}

	public final void setLength(int length) {
		this.length = length;
	}

	public final int getLength() {
		return length;
	}

}