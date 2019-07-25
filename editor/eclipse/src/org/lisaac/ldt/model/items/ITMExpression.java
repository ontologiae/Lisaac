package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

/**
 * operator list message
 */
public class ITMExpression implements ICode {
	protected ICode[] valueList;
	
	public ITMExpression(ICode[] list) {
		valueList = list;
	}

	public IType getType(Slot slot, Prototype prototype) {
		if (valueList != null && valueList.length > 0) {
			return valueList[valueList.length-1].getType(slot, prototype); // FIXME expr type
		}
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName, List<TextEdit> edits) {	
		for (int i=0; i<valueList.length; i++) {
			valueList[i].refactorRenamePrototype(oldName, newName, edits);
		}
	}
}
