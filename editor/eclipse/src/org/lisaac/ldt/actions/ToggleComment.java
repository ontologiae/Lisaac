package org.lisaac.ldt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.AbstractLisaacEditor;

public class ToggleComment implements IWorkbenchWindowActionDelegate {

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
			ITextSelection selection = (ITextSelection) ((AbstractLisaacEditor)part).getSelectionProvider().getSelection();

			try {
				if (selection.getStartLine() == selection.getEndLine()) {
					// single line comment
					
					int startPos = document.getLineOffset(selection.getStartLine());
					int pos = startPos;
					char c;
					do {
						c = document.getChar(pos);
						if (c != ICharacterScanner.EOF) {
							if (c == '/') {
								pos++;
								c = document.getChar(pos);
								if (c != ICharacterScanner.EOF && c == '/') {
									document.replace(pos-1, 2, "");// delete comment //$NON-NLS-1$
									return;
								}
							}
						}
						pos++;
					} while (c != ICharacterScanner.EOF && pos <= startPos+selection.getLength());
					
					// add comment
					document.replace(startPos, 0, "//"); //$NON-NLS-1$
					
				} else {
					// multiline comment
					int startPos = document.getLineOffset(selection.getStartLine());
					int pos = startPos;
					boolean deleteComment=false;
					char c;
					do {
						c = document.getChar(pos);
						if (c != ICharacterScanner.EOF) {
							if (c == '/') {
								pos++;
								c = document.getChar(pos);
								if (c != ICharacterScanner.EOF && c == '*') {
									document.replace(pos-1, 2, "");// delete comment //$NON-NLS-1$
									deleteComment = true;
									pos++;
								}
							}
							if (c == '*') {
								pos++;
								c = document.getChar(pos);
								if (c != ICharacterScanner.EOF && c == '/') {
									if (deleteComment) {
										document.replace(pos-1, 2, "");// delete comment //$NON-NLS-1$
									}
								}
							}
						}
						pos++;
					} while (c != ICharacterScanner.EOF && pos <= startPos+selection.getLength());
					
					// add comment
					if (! deleteComment) {
						document.replace(startPos, 0, "/*"); //$NON-NLS-1$
						int ofs = document.getLineOffset(selection.getEndLine());
						ofs += document.getLineLength(selection.getEndLine());
						document.replace(ofs, 0, "*/"); //$NON-NLS-1$
					}
				}
				
			} catch(BadLocationException e) {
			}
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
