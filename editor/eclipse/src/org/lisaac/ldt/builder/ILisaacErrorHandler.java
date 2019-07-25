package org.lisaac.ldt.builder;

import org.lisaac.ldt.model.Position;

public interface ILisaacErrorHandler {

	void syntaxError(String msg, Position position);
	
	void fatalError(String msg, Position position);

	void warning(String msg, Position position);

	void semanticError(String msg, Position position);

	void enableErrorReport(boolean enable);
}
