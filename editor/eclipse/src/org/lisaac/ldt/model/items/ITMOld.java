package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public class ITMOld implements ICode {
	protected ICode value;

	public ITMOld(ICode value) {
		this.value = value;
	}

	public IType getType(Slot slot, Prototype prototype) {
		if (value != null) {
			return value.getType(slot, prototype);
		}
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
	}
}
