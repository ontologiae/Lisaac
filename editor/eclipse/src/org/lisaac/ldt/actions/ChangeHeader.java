package org.lisaac.ldt.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lisaac.ldt.editors.AbstractLisaacEditor;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.refactor.ChangeHeaderRefactor;
import org.lisaac.ldt.refactor.ChangeHeaderWizard;

public class ChangeHeader implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow = null;

	private LisaacModel model;

	public void dispose() { 
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		if (fWindow != null&& model != null) {
			ChangeHeaderRefactor refactoring = new ChangeHeaderRefactor(model);
			String name = "Change Project Headers";
			run(new ChangeHeaderWizard(refactoring, name),
					fWindow.getShell(), name);
		}
	}
	
	public void run(RefactoringWizard wizard, Shell parent, String dialogTitle) {
		try {
			RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
			operation.run(parent, dialogTitle);
		} catch (InterruptedException exception) {
			// Do nothing
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		model = null;
		
		if (selection instanceof ITextSelection) {
			if (fWindow != null) {
				// get surrounding word
				IWorkbenchPart part = fWindow.getPartService().getActivePart();
				if (part instanceof AbstractLisaacEditor) {
					IProject project = ((AbstractLisaacEditor) part)
							.getProject();
					model = LisaacModel.getModel(project);
				}
			}
		} else if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).getFirstElement() instanceof IResource) {
				IResource res = (IResource) ((IStructuredSelection) selection).getFirstElement();
				if (res.getProject() != null) {
					model = LisaacModel.getModel(res.getProject());
				}
			}
		}
		action.setEnabled(model != null);
	}
}
