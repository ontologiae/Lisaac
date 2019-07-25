package org.lisaac.ldt.model.items;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.types.IType;

public class ITMListIdf implements ICode {

	ArrayList<String> list;
	

	public ITMListIdf(ArrayList<String> list) {
		this.list = list;
	}

	public IType getType(Slot slot, Prototype prototype) {
		// TODO Auto-generated method stub
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		// TODO Auto-generated method stub

	}
}
