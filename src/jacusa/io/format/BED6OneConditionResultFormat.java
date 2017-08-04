package jacusa.io.format;

import jacusa.filter.FilterConfig;

import jacusa.pileup.BaseConfig;

public class BED6OneConditionResultFormat extends BED6callFormat {

	public BED6OneConditionResultFormat(
			final BaseConfig baseConfig, 
			final FilterConfig filterConfig) {
		super('B', "Default", baseConfig, filterConfig, true);
	}

}
