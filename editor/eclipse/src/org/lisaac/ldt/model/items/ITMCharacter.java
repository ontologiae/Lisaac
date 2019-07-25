package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.TypeSimple;

public class ITMCharacter implements IConstant {

	public ITMCharacter(String string) {
	}

	public IType getType(Slot slot, Prototype prototype) {
		return TypeSimple.get(ILisaacModel.prototype_character);
	}

	public void refactorRenamePrototype(String oldName, String newName, List<TextEdit> edits) {
	}
}
