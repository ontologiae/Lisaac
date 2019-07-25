package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;

public class ITMReadArg1 extends ITMRead {

	protected ICode arg;
	
	public ITMReadArg1(String name, ICode arg) {
		super(name);
		this.arg = arg;
	}
	
	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		arg.refactorRenamePrototype(oldName, newName, edits);
	}
}
