package jacusa.cli.parameters;

import jacusa.data.BaseQualData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class PileupParameters<T extends BaseQualData>
extends AbstractParameters<T> {

	public PileupParameters(int conditions) {
		super(conditions);
	}

}
