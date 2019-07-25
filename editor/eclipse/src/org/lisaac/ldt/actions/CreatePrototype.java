package org.lisaac.ldt.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.wizards.NewPrototypeWizard;

public class CreatePrototype extends Action implements IWorkbenchWindowActionDelegate {

	private Shell fShell;
	private IStructuredSelection fSelection;
	
	
	public CreatePrototype() {
	}
	
	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		Shell shell= getShell();

		try {
			INewWizard wizard= createWizard();
			wizard.init(PlatformUI.getWorkbench(), getSelection());
			
			WizardDialog dialog= new WizardDialog(shell, wizard);
			dialog.create();
			int res= dialog.open();
			if (res != Window.OK) {
				// TODO log error
			}
		} catch (CoreException e) {
			// TODO log error
		}
	}
	
	/**
	 * Returns the configured selection. If no selection has been configured using {@link #setSelection(IStructuredSelection)},
	 * the currently selected element of the active workbench is returned.
	 * @return the configured selection
	 */
	protected IStructuredSelection getSelection() {
		if (fSelection == null) {
			return evaluateCurrentSelection();
		}
		return fSelection;
	}
	
	private IStructuredSelection evaluateCurrentSelection() {
		IWorkbenchWindow window = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
			ISelection selection= window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection) selection;
			}
		}
		return StructuredSelection.EMPTY;
	}
	
	protected Shell getShell() {
		return fShell;
	}
	
	// make it an abstract method if more wizard shortcuts are created
	protected final INewWizard createWizard() throws CoreException {
		return new NewPrototypeWizard();
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
