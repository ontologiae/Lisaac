package org.lisaac.ldt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.AbstractLisaacEditor;
import org.lisaac.ldt.editors.LisaacAutoEditStrategy;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class IndentAction implements IWorkbenchWindowActionDelegate {
	/**
	 * The constructor.
	 */
	public IndentAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPart part = w.getPartService().getActivePart();
		if (part instanceof AbstractLisaacEditor) {
			IDocument document = ((AbstractLisaacEditor)part).getDocument();
			//
			LisaacAutoEditStrategy.fullIndentDocument(document);
			//
		}
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPart part = w.getPartService().getActivePart();
		if (part instanceof AbstractLisaacEditor) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}
}