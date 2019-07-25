package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public class ITMLDots implements ICode {

	public IType getType(Slot slot, Prototype prototype) {
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName, List<TextEdit> edits) {
	}
}
