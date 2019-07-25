package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;

public class ITMReadArgs extends ITMRead {
	protected ICode[] args;

	public ITMReadArgs(String name, ICode[] args) {
		super(name);
		this.args = args;
	}
	
	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		for (int i=0; i<args.length; i++) {
			args[i].refactorRenamePrototype(oldName, newName, edits);
		}
	}
}
