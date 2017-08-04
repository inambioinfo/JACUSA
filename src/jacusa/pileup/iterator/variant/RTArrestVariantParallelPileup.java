package jacusa.pileup.iterator.variant;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.pileup.BaseReadPileup;
import jacusa.pileup.ParallelData;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

public class RTArrestVariantParallelPileup implements Variant<BaseReadPileup> {
	
	final private ConditionParameters[] conditionParameters;
	
	public RTArrestVariantParallelPileup(ConditionParameters[] conditionParameters) {
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public boolean isValid(final ParallelData<BaseReadPileup> parallelData) {
		int arrest = 0;
		int through = 0;

		for (int conditionIndex = 0; conditionIndex < conditionParameters.length; conditionIndex++) {
			LibraryType libraryType = conditionParameters[conditionIndex].getLibraryType();
			switch (libraryType) {
			case UNSTRANDED:
				arrest 	+= parallelData.getPooledData(conditionIndex).getReadInfoCount().getStart();
				arrest 	+= parallelData.getPooledData(conditionIndex).getReadInfoCount().getEnd();
				through += parallelData.getPooledData(conditionIndex).getReadInfoCount().getInner();
				break;

			case FR_FIRSTSTRAND:
				arrest 	+= parallelData.getPooledData(conditionIndex).getReadInfoCount().getEnd();
				through += parallelData.getPooledData(conditionIndex).getReadInfoCount().getInner();
				break;

			case FR_SECONDSTRAND:
				arrest 	+= parallelData.getPooledData(conditionIndex).getReadInfoCount().getStart();
				through += parallelData.getPooledData(conditionIndex).getReadInfoCount().getInner();
				break;				
			}
		}

		return arrest > 0 && through > 0; 
	}

}