package jacusa.cli.options.pileupbuilder;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualReadInfoData;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LibraryType;

public class OneConditionBaseQualReadInfoDataBuilderOption<T extends BaseQualReadInfoData>
extends OneConditionBaseQualDataBuilderOption<T> {

	public OneConditionBaseQualReadInfoDataBuilderOption(final ConditionParameters<T> condition) {
		super(condition);
	}

	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final AbstractMethodFactory<T> abstractMethodFactory,
			final LibraryType libraryType) {
		return new RTArrestPileupBuilderFactory<T>(super.buildPileupBuilderFactory(libraryType));
	}
	
}