package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.TypeSimple;

public class ITMRead implements ICode {
	protected String name;

	public ITMRead(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public IType getType(Slot slot, Prototype prototype) {
		
		if (slot != null) {
			if (name.equals(ILisaacModel.variable_self)) {// type simple?
				return TypeSimple.get(slot.getPrototype().getName());
			}
			IArgument arg = slot.getArgument(name);
			if (arg != null) {
				if (arg instanceof ITMArgs) {
					return ((ITMArgs) arg).getArgType(name);
				}
				return arg.getType();
			}
			if (slot.getValue() instanceof ITMList) {
				ITMList list = (ITMList) slot.getValue();
				ITMLocal local = list.getLocal(name);
				if (local != null) {
					return local.getType();
				}
			}
		}
		if (prototype != null) {
			Slot s = prototype.lookupSlot(name);
			if (s != null) {
				return s.getResultType();
			}
		}
		return null;
	}

	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		// TODO Auto-generated method stub
	}
}
