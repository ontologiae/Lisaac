package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public class ITMOperator implements ICode {
	protected String name;

	public ITMOperator(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public IType getType(Slot slot, Prototype prototype) {
		// TODO Auto-generated method stub
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
	}
}
