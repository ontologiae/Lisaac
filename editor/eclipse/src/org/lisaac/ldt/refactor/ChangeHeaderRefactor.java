package org.lisaac.ldt.refactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.lisaac.ldt.model.AbstractLisaacParser;
import org.lisaac.ldt.model.ILisaacFileVisitor;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.Prototype;

public class ChangeHeaderRefactor extends Refactoring {

	private String author, bibliography, copyright, license;

	private LisaacModel model;


	public ChangeHeaderRefactor(LisaacModel model) {
		this.model = model;
	}

	public RefactoringStatus setLicense(String license) {
		this.license = license;
		return isValidLicense(license);
	} 

	public void setAuthor(String author) {
		this.author = author;
	} 

	public void setBibliography(String bibliography) {
		this.bibliography = bibliography;
	} 

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	} 

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
	throws CoreException, OperationCanceledException {
		RefactoringStatus status = isValidLicense(license);
		return status;
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
	throws CoreException, OperationCanceledException {
		RefactoringStatus status = isValidLicense(license);
		return status;
	}

	public Change createChange(final IProgressMonitor pm) throws CoreException,
	OperationCanceledException {
		final CompositeChange result = new CompositeChange(getName());

		int work = model.getPathManager().getSize();
		pm.beginTask("Updating headers...", work);

		model.accept(new ILisaacFileVisitor() {
			public void visit(Prototype prototype) {
				if (prototype != null) {
					Change change = prototype.refactorHeader(author, bibliography, copyright, license);
					if (change != null) {
						result.add(change);
					}
				}
				pm.worked(1);
			}
		});
		pm.done();
		return result;
	}

	private RefactoringStatus isValidLicense(String source) {
		RefactoringStatus status = new RefactoringStatus();
		if (source != null) {
			AbstractLisaacParser parser = new AbstractLisaacParser(source);
			parser.readSpace();
			int offset = parser.getOffset();
			
			if (offset < source.length()) {
				status.addFatalError("The license must be inside Lisaac comments");
			}
		}
		return status;
	}

	public String getName() {
		return "Change Project Headers...";
	}
}
