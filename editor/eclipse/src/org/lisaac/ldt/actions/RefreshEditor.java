package org.lisaac.ldt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.lisaac.ldt.editors.AbstractLisaacEditor;
import org.lisaac.ldt.model.LisaacModel;

public class RefreshEditor implements IEditorActionDelegate {

	AbstractLisaacEditor targetEditor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof AbstractLisaacEditor) {
			this.targetEditor = (AbstractLisaacEditor) targetEditor;
		} else {
			this.targetEditor = null;
		}
		LisaacModel.currentEditor = this.targetEditor;
	}

	public void run(IAction action) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
