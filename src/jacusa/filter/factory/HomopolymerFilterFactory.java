package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.filter.HomopolymerFilter;

public class HomopolymerFilterFactory<T extends BaseQualData> 
extends AbstractFilterFactory<T> {

	private static final int LENGTH = 7;
	private int length;
	private AbstractParameters<T> parameters;
		
	public HomopolymerFilterFactory(final AbstractParameters<T> parameters) {
		super('Y', "Filter wrong variant calls within homopolymers. Default: " + LENGTH + " (Y:length)");
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
	public HomopolymerFilter<T> createFilter() {
		return new HomopolymerFilter<T>(getC(), length, parameters);
	}

	public final void setLength(int length) {
		this.length = length;
	}

	public final int getLength() {
		return length;
	}

}