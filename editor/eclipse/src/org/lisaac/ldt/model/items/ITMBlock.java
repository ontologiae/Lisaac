package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.LisaacParser;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.TypeBlock;

public class ITMBlock implements ICode {
	protected ITMList list;
	protected IArgument argument;

	public ITMBlock(ITMList list, IArgument argument, Slot slot) {
		this.list = list;
		this.argument = argument;

		if (slot != null) {
			slot.addSubList(this);
		}
	}

	public IType getType(Slot slot, Prototype prototype) {
		return TypeBlock.get(null, null); // FIXME empty block
	}

	public void refactorRenamePrototype(String oldName, String newName, List<TextEdit> edits) {
		if (argument != null && list != null) {
			IType type = argument.getType();
			Position p = argument.getPosition();

			if (p != null && type.toString().compareTo(oldName) == 0) {
				LisaacParser parser = list.getOwner().getPrototype().openParser();
				parser.setPosition(p.offset+p.length);
				parser.readCharacter(':');
				parser.readSpace();

				int startOffset = parser.getOffset();

				edits.add(new DeleteEdit(startOffset, oldName.length()));
				edits.add(new InsertEdit(startOffset, newName));
			}
		}
		if (list != null) {
			list.refactorRenamePrototype(oldName, newName, edits);
		}
	}
}
