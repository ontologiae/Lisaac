package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public interface ICode {

	IType getType(Slot slot, Prototype prototype);

	void refactorRenamePrototype(String oldName, String newName, List<TextEdit> edits);
}
