package jacusa.io.format;

import jacusa.pileup.Data;
import jacusa.pileup.Result;

public abstract class AbstractOutputFormat<T extends Data<T>> {

	private char c;
	private String desc;
	
	public AbstractOutputFormat(char c, String desc) {
		this.c = c;
		this.desc = desc;
	}

	public final char getC() {
		return c;
	}

	public final String getDesc() {
		return desc;
	}

	// Header is empty by default
	public String getHeader(String[][] pathnames) {
		return null;
	}

	public abstract String convert2String(Result<T> result);

}