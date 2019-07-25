package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.TypeSimple;

public class ITMNumber implements IConstant {

	public ITMNumber(long lastInteger) {
	}

	public IType getType(Slot slot, Prototype prototype) {
		return TypeSimple.get(ILisaacModel.prototype_integer);
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
	}
}
