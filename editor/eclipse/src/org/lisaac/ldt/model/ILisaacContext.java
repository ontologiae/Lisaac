package org.lisaac.ldt.model;

import org.lisaac.ldt.model.items.Prototype;

public interface ILisaacContext {
	/**
	 * Parse the definition of the context. Returns true if the
	 * context is correctly parsed. <br>
	 * Automatically report parse errors.
	 * @param prototype The model to fill
	 * @return false if the definition of context contains at least one error
	 */
	boolean parseDefinition(Prototype prototype);

	/**
	 * Get the following context of same category in the lisaac code.
	 * @return next context in current prototype
	 */
	ILisaacContext getNextContext();
}
