package org.lisaac.ldt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.LisaacEditor;

public class GenerateConstructor implements IWorkbenchWindowActionDelegate {


	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPart part = w.getPartService().getActivePart();
		if (part instanceof LisaacEditor) {
			IDocument document = ((LisaacEditor)part).getDocument();
			//
			int caret = ((LisaacEditor)part).getViewer().getTextWidget().getCaretOffset();
			String constructor = getConstructor();
			try {
				document.replace(caret, 0, constructor);
			} catch (BadLocationException e) {
			}
			//
		}
	}
	
	public static String getConstructor() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n  //\n  // Creation.\n  //\n"); //$NON-NLS-1$
		buffer.append("\n  - create:SELF <-"); //$NON-NLS-1$
		buffer.append("\n  ( + result:SELF;"); //$NON-NLS-1$
		buffer.append("\n    result := clone;"); //$NON-NLS-1$
		buffer.append("\n    result.make;"); //$NON-NLS-1$
		buffer.append("\n    result"); //$NON-NLS-1$
		buffer.append("\n  );"); //$NON-NLS-1$
		buffer.append("\n"); //$NON-NLS-1$
		buffer.append("\n  - make <-"); //$NON-NLS-1$
		buffer.append("\n  ("); //$NON-NLS-1$
		buffer.append("\n  );\n\n"); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}
	
	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}
}
