package org.lisaac.ldt.model.items;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.LisaacCompletionParser;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.TypeSimple;

public class ITMReadArg2 extends ITMRead {

	protected ICode argFirst;
	protected ICode argSecond;

	public ITMReadArg2(String name, ICode a1, ICode a2) {
		super(name);
		this.argFirst = a1;
		this.argSecond = a2;
	}

	public IType getType(Slot slot, Prototype prototype) {
		//
		// operator expression.
		//
		if (name.startsWith("__")) { // FIXME __infix __prefix __postfix??

			// Get type of left part of operator.
			IType type = argFirst.getType(slot, prototype);
			if (type != null) {
				try {
					Prototype operatorPrototype = LisaacCompletionParser.findPrototype(""+type);
					if (operatorPrototype != null) {
						Slot operatorSlot = operatorPrototype.lookupSlot(name);
						if (operatorSlot != null) {
							// return result type of operator.
							return operatorSlot.getResultType();
						} else {
							if (name.compareTo("__infix_equal") == 0) {
								// special case for '=' operator (can't be a slot)
								return TypeSimple.getTypeBoolean();
							}
						}
					}
				} catch (CoreException e) {
				}
			}
			// second chance with second argument  FIXME use __postfix?
			type = argSecond.getType(slot, prototype);
			if (type != null) {
				try {
					Prototype operatorPrototype = LisaacCompletionParser.findPrototype(""+type);
					if (operatorPrototype != null) {
						Slot operatorSlot = operatorPrototype.lookupSlot(name);
						if (operatorSlot != null) {
							// return result type of operator.
							return operatorSlot.getResultType();
						}
					}
				} catch (CoreException e) {
				}
			}
		}
		//
		// classic slot
		//
		return super.getType(slot, prototype);
	}
	
	public void refactorRenamePrototype(String oldName, String newName,
			List<TextEdit> edits) {
		
		argFirst.refactorRenamePrototype(oldName, newName, edits);
		argSecond.refactorRenamePrototype(oldName, newName, edits);
	}
}
