package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public class ITMWrite implements ICode {

	protected ICode assign;
	protected ICode value;
	
	protected char type;
	
	public ITMWrite(ICode assign, ICode value, char type) {
		super();
		this.assign = assign;
		this.value = value;
		this.type = type;
	}
	
	public IType getType(Slot slot, Prototype prototype) {
		// TODO Auto-generated method stub
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		assign.refactorRenamePrototype(oldName, newName, edits);
		value.refactorRenamePrototype(oldName, newName, edits);
	}

}
