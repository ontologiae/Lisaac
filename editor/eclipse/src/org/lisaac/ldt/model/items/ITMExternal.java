package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public class ITMExternal implements ICode {
	protected String extern;

	public ITMExternal(String extern) {
		this.extern = extern;
	}

	public IType getType(Slot slot, Prototype prototype) {
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName, List<TextEdit> edits) {
	}
}
