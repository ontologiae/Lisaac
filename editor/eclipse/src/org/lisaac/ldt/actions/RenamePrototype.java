package org.lisaac.ldt.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.editors.AbstractLisaacEditor;
import org.lisaac.ldt.editors.LisaacScanner;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.refactor.RenamePrototypeRefactor;
import org.lisaac.ldt.refactor.RenamePrototypeWizard;

public class RenamePrototype implements IWorkbenchWindowActionDelegate {

	private String prototypeName;

	private LisaacModel model;

	private IWorkbenchWindow fWindow = null;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		if (fWindow != null && prototypeName != null && model != null) {
			RenamePrototypeRefactor refactoring = new RenamePrototypeRefactor(
					prototypeName, model);
			String name = LisaacMessages.getString("RenamePrototype.0"); //$NON-NLS-1$
			run(new RenamePrototypeWizard(refactoring, prototypeName, name),
					fWindow.getShell(), name);
		}
	}

	public void run(RefactoringWizard wizard, Shell parent, String dialogTitle) {
		try {
			RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(
					wizard);
			operation.run(parent, dialogTitle);
		} catch (InterruptedException exception) {
			// Do nothing
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		prototypeName = null;

		if (selection instanceof ITextSelection) {
			String text = ((ITextSelection) selection).getText();
			if (/* text.length() == 0 && */fWindow != null) {
				// get surrounding word
				IWorkbenchPart part = fWindow.getPartService().getActivePart();
				if (part instanceof AbstractLisaacEditor) {
					IDocument document = ((AbstractLisaacEditor) part)
							.getDocument();
					IProject project = ((AbstractLisaacEditor) part)
							.getProject();
					model = LisaacModel.getModel(project);

					try {
						text = selectWord(document,
								((ITextSelection) selection).getOffset());
					} catch (BadLocationException e) {
						action.setEnabled(false);
						return;
					}
				}
			}
			if (LisaacScanner.isPrototypeIdentifier(text)) {
				prototypeName = text;
			}
		}
		action.setEnabled(prototypeName != null);
	}

	protected String selectWord(IDocument doc, int caretPos)
			throws BadLocationException {
		int startPos, endPos;

		int pos = caretPos;
		char c;

		while (pos >= 0) {
			c = doc.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			--pos;
		}
		startPos = pos + 1;
		pos = caretPos;
		int length = doc.getLength();

		while (pos < length) {
			c = doc.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			++pos;
		}
		endPos = pos;
		return doc.get(startPos, endPos - startPos);
	}
}
