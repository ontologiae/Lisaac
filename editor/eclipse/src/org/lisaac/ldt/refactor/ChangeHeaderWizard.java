package org.lisaac.ldt.refactor;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


class ChangeHeaderInputPage extends UserInputWizardPage {

	private Text authorField;
	private Text bibliographyField;
	private Text copyrightField;
	private Text licenseField;
	
	public ChangeHeaderInputPage() {
		super("ChangeHeaderInputPage");
	}

	public void createControl(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);

		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

		Label label= new Label(result, SWT.NONE);
		label.setText("&Author :");
		authorField = createNameField(result);
		
		label= new Label(result, SWT.NONE);
		label.setText("&Bibliography :");
		bibliographyField = createNameField(result);
		
		label= new Label(result, SWT.NONE);
		label.setText("&Copyright :");
		copyrightField = createNameField(result);

		Group group = new Group(result, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 100;
		gd.verticalSpan = 2;
		group.setLayoutData(gd);
		group.setLayout(new GridLayout());
		group.setText("&License :");
		
		licenseField = new Text(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		licenseField.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		authorField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				handleInputChanged(authorField);
			}
		});
		bibliographyField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				handleInputChanged(bibliographyField);
			}
		});
		copyrightField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				handleInputChanged(copyrightField);
			}
		});
		licenseField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				handleInputChanged(licenseField);
			}
		});
		authorField.setFocus();
	}

	private Text createNameField(Composite result) {
		Text field= new Text(result, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return field;
	}

	void handleInputChanged(Text field) {
		RefactoringStatus status= new RefactoringStatus();
	
		ChangeHeaderRefactor refactoring = (ChangeHeaderRefactor) getRefactoring();
		if (field == authorField) {
			refactoring.setAuthor(field.getText());
		} else if (field == bibliographyField) {
			refactoring.setBibliography(field.getText());
		} else if (field == copyrightField) {
			refactoring.setCopyright(field.getText());
		} else {
			status.merge(refactoring.setLicense(field.getText()));
		}
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

public class ChangeHeaderWizard extends RefactoringWizard {
	
	ChangeHeaderInputPage page;
	
	public ChangeHeaderWizard(Refactoring refactoring, String title) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(title);
		
		page = new ChangeHeaderInputPage();
	}

	protected void addUserInputPages() {
		addPage(page);
	}
}
