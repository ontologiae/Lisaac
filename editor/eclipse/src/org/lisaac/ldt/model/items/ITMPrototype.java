package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.ITypeMono;

public class ITMPrototype implements ICode {
	protected ITypeMono type;

	protected Position position;
	
	public ITMPrototype(ITypeMono type, Position position) {
		this.type = type;
		this.position = position;
	}

	public IType getType(Slot slot, Prototype prototype) {
		return type;
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		
		if (type.toString().compareTo(oldName) == 0) {
			edits.add(new DeleteEdit(position.offset, oldName.length()));
			edits.add(new InsertEdit(position.offset, newName));
		}
	}
}
