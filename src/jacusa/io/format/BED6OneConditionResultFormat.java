package jacusa.io.format;

import jacusa.data.BaseConfig;
import jacusa.data.BaseQualData;
import jacusa.filter.FilterConfig;

public class BED6OneConditionResultFormat extends BED6call {

	public BED6OneConditionResultFormat(
			final BaseConfig baseConfig, 
			final FilterConfig<BaseQualData> filterConfig) {
		super('B', "Default", baseConfig, filterConfig, true);
	}

}
