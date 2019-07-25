package org.lisaac.ldt.refactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.lisaac.ldt.editors.LisaacScanner;
import org.lisaac.ldt.model.ILisaacFileVisitor;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.Prototype;

public class RenamePrototypeRefactor extends Refactoring {

	private String oldName, newName;

	private LisaacModel model;
	private IPath prototypePath;

	private boolean updateReferences;

	public RenamePrototypeRefactor(String oldName, LisaacModel model) {
		this.oldName = oldName;
		this.model = model;
		updateReferences = true;
	}

	public RefactoringStatus setNewPrototypeName(String newName) {
		this.newName = newName;

		return isValidPrototypeName(newName);
	}

	public void setUpdateReferences(boolean update) {
		updateReferences = update;
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
	throws CoreException, OperationCanceledException {

		RefactoringStatus status = isValidPrototypeName(newName);
		// ...
		return status;
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
	throws CoreException, OperationCanceledException {
		RefactoringStatus status = isValidPrototypeName(oldName);

		Prototype prototype = model.getPrototype(oldName);
		if (prototype != null) {
			prototypePath = prototype.getWorkspacePath();
		} else {
			status.addFatalError("The prototype do not exist in project");
		}
		return status;
	}

	public Change createChange(final IProgressMonitor pm) throws CoreException,
	OperationCanceledException {
		final CompositeChange result = new CompositeChange(getName());

		// 1. rename the prototype in section header
		Prototype prototype = model.getPrototype(oldName);
		result.add(prototype.refactorRenameSelf(newName));

		// 2. rename occurences in the project program
		if (updateReferences) {
			int work = model.getPathManager().getSize();
			pm.beginTask("Collecting occurences...", work);
			
			model.accept(new ILisaacFileVisitor() {
				public void visit(Prototype prototype) {
					if (prototype != null) {
						Change change = prototype.refactorRenamePrototype(oldName, newName);
						if (change != null) {
							result.add(change);
						}
					}
					pm.worked(1);
				}
			});
		}
		// 3. rename the prototype file
		result.add(new RenameResourceChange(prototypePath, newName.toLowerCase()+".li"));
		pm.done();
		
		return result;
	}

	private RefactoringStatus isValidPrototypeName(String name) {
		RefactoringStatus status = new RefactoringStatus();

		if (name.length() == 0) {
			status.addError("Empty name");
		} else if (! LisaacScanner.isPrototypeIdentifier(name)) {
			status.addError("Invalid prototype name");	
		}
		return status;
	}

	public String getName() {
		return "Rename Prototype...";
	}
}
