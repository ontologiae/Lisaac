package org.lisaac.ldt.refactor;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


class RenamePrototypeInputPage extends UserInputWizardPage {

	Text fNameField;

	String prototypeToRename;
	
	public RenamePrototypeInputPage(String prototypeToRename) {
		super("RenamePrototypeInputPage");
		this.prototypeToRename = prototypeToRename;
	}

	public void createControl(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);

		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

		Label label= new Label(result, SWT.NONE);
		label.setText("&New name:");

		fNameField= createNameField(result);

		final Button referenceButton= new Button(result, SWT.CHECK);
		referenceButton.setText("&Update references");
		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan= 2;
		data.verticalIndent= 2;
		referenceButton.setLayoutData(data);

		fNameField.setText(prototypeToRename);

		fNameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				handleInputChanged();
			}
		});

		referenceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				RenamePrototypeRefactor refactoring = (RenamePrototypeRefactor) getRefactoring();
				refactoring.setUpdateReferences(referenceButton.getSelection());
			}
		});

		referenceButton.setSelection(true);

		fNameField.setFocus();
		fNameField.selectAll();
		handleInputChanged();
	}

	private Text createNameField(Composite result) {
		Text field= new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return field;
	}

	void handleInputChanged() {
		RefactoringStatus status= new RefactoringStatus();
	
		RenamePrototypeRefactor refactoring = (RenamePrototypeRefactor) getRefactoring();
		status.merge(refactoring.setNewPrototypeName(fNameField.getText()));
		
		setPageComplete(!status.hasError());
		int severity= status.getSeverity();
		String message= status.getMessageMatchingSeverity(severity);
		if (severity >= RefactoringStatus.INFO) {
			setMessage(message, severity);
		} else {
			setMessage("", NONE); //$NON-NLS-1$
		}
	}

}

public class RenamePrototypeWizard extends RefactoringWizard {
	
	RenamePrototypeInputPage page;
	
	public RenamePrototypeWizard(Refactoring refactoring, String prototypeToRename, String title) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(title);
		
		page = new RenamePrototypeInputPage(prototypeToRename);
	}

	protected void addUserInputPages() {
		addPage(page);
	}
}
