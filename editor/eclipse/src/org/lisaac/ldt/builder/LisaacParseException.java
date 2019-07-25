package org.lisaac.ldt.builder;

public class LisaacParseException extends Exception {
	
	int line;
	
	LisaacParseException(String msg, int line) {
		super(msg);
	}

	public int getLineNumber() {
		return line;
	}
}
