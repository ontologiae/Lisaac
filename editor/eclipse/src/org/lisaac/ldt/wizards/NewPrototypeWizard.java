package org.lisaac.ldt.wizards;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.actions.GenerateConstructor;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.preferences.PreferenceConstants;
import org.eclipse.jface.dialogs.InputDialog;

import java.io.*;

/**
 * Lisaac source file creator.
 */
public class NewPrototypeWizard extends AbstractNewFileWizard {

	public NewPrototypeWizard() {
		super();
		setWindowTitle(LisaacMessages.getString("NewPrototypeWizard_0"));
	}

	/**
	 * Create wizard page
	 */
	protected AbstractNewFileWizardPage createPage() {
		return new NewPrototypeWizardPage(getSelection());
	}
}

/**
 * Represent the main page in "new lisaac prototype" wizard.
 */
class NewPrototypeWizardPage extends AbstractNewFileWizardPage  {
	final private String INITIAL_PROTOTYPE_NAME = "prototype.li"; //$NON-NLS-1$

	protected Text commentField;

	protected Button styleNone;
	protected Button styleExpanded;
	protected Button styleStrict;

	protected Table tableInherit;

	protected Button generateConstructor, generateMain;


	protected NewPrototypeWizardPage(IStructuredSelection selection) {
		super(NewPrototypeWizardPage.class.getName(), selection);

		setTitle(LisaacMessages.getString("NewPrototypeWizard_2"));
		setDescription(LisaacMessages.getString("NewPrototypeWizard_3"));
		setFileExtension(".li"); //$NON-NLS-1$
		
	}

	public String getPrototypeDescription() {
		return commentField.getText();
	}

	/**
	 * Return the initial content of new file.
	 */
	protected InputStream getInitialContents(String filename) {
		try {
			int index = filename.lastIndexOf('.');
			if (index != -1) {
				filename = filename.substring(0, index);
			}
			return new ByteArrayInputStream(getPrototypeStream(filename));
		} catch (IOException e) {
			return null; // ignore and create empty comments
		}
	}

	public byte[] getPrototypeStream(String filename) throws IOException {
		boolean isExpanded = styleExpanded.getSelection();
		boolean isStrict = styleExpanded.getSelection();

	/*	//IProject project = (IProject) ();
		try {
			String license = project.getPersistentProperty(new QualifiedName("",LICENSE_PROPERTY));
			licenseText.setText(license);
		} catch (CoreException e) {
		}*/
		
		String contents = "\nSection Header\n\n"; //$NON-NLS-1$
		contents +=	"  + name    := "; //$NON-NLS-1$
		if (isExpanded) {
			contents += "Expanded "; //$NON-NLS-1$
		} else if (isStrict) {
			contents += "Strict "; //$NON-NLS-1$
		}
		contents +=	filename.toUpperCase()+";\n"; //$NON-NLS-1$

		String descr = getPrototypeDescription();
		if (descr != null && descr.length() > 0) {
			contents +=	"  - comment := \""+descr+"\";\n"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		String username = LisaacPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_LISAAC_USER);
		if (username != null && username.length() > 0) {
			contents += "\n  - author := \""+username+"\";\n"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (! isExpanded) {
			contents +=	"\nSection Inherit\n\n"; //$NON-NLS-1$
		} else {
			contents +=	"\nSection Insert\n\n"; //$NON-NLS-1$
		}
		int n = tableInherit.getItemCount();
		if (n > 0) {
			for (int i=0; i<n; i++) {
				TableItem item = tableInherit.getItem(i);
				String type = item.getText(1);
				if (type.equals(ILisaacModel.inherit_shared) ||
						type.equals(ILisaacModel.inherit_shared_expanded)) {
					contents += "  - "; //$NON-NLS-1$
				} else {
					contents += "  + "; //$NON-NLS-1$
				}
				contents += "parent_"; //$NON-NLS-1$
				contents += item.getText(0).toLowerCase() + ":"; //$NON-NLS-1$
				if (type.equals(ILisaacModel.inherit_shared_expanded) ||
						type.equals(ILisaacModel.inherit_nonshared_expanded)) {
					contents +=  "Expanded " + item.getText(0); //$NON-NLS-1$
				} else {
					contents += item.getText(0) + " := " + item.getText(0); //$NON-NLS-1$
				}
				contents += ";\n"; //$NON-NLS-1$
			}
		} else {
			contents += "  - parent_object:OBJECT := OBJECT;\n"; //$NON-NLS-1$
		}
		contents +=	"\nSection Public\n\n";  //$NON-NLS-1$
		
		if (generateConstructor.getSelection()) {
			contents +=	GenerateConstructor.getConstructor();
		}
		if (generateMain.getSelection()) {
			contents += "  - main <- \n"; //$NON-NLS-1$
			contents += "  // Main entry point.\n"; //$NON-NLS-1$
			contents += "  (\n\n"; //$NON-NLS-1$
			contents += "    \n"; //$NON-NLS-1$
			contents += "  );\n"; //$NON-NLS-1$
		}
		return contents.getBytes();
	}

	/**
	 * Additional wizard information
	 */
	public void createAdvancedControls(Composite parent) {

		// -- Style --
		Label label = new Label(parent, SWT.NONE);
		label.setText(LisaacMessages.getString("NewPrototypeWizard_25"));
		label.setFont(parent.getFont());

		Composite radioGroup = new Composite(parent, SWT.NONE);
		radioGroup.setLayout(new RowLayout());
		styleNone = new Button(radioGroup, SWT.RADIO);
		styleNone.setFont(parent.getFont());
		styleNone.setSelection(true);
		styleNone.setText(LisaacMessages.getString("NewPrototypeWizard_26"));
		styleExpanded = new Button(radioGroup, SWT.RADIO);
		styleExpanded.setFont(parent.getFont());
		styleExpanded.setText(LisaacMessages.getString("NewPrototypeWizard_27"));
		styleStrict = new Button(radioGroup, SWT.RADIO);
		styleStrict.setFont(parent.getFont());
		styleStrict.setText(LisaacMessages.getString("NewPrototypeWizard_28"));
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		radioGroup.setLayoutData(gridData);
		//

		// -- Description --
		label = new Label(parent, SWT.NONE);
		label.setText(LisaacMessages.getString("NewPrototypeWizard_29"));
		label.setFont(parent.getFont());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		label.setLayoutData(gridData);

		commentField = new Text(parent, SWT.BORDER);
		commentField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		commentField.setLayoutData(gridData);
		//

		// -- inherits --
		label = new Label(parent, SWT.NONE);
		label.setText(LisaacMessages.getString("NewPrototypeWizard_30"));
		label.setFont(parent.getFont());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		label.setLayoutData(gridData);

		Composite tableGroup = new Composite(parent, SWT.NONE);
		tableGroup.setLayout(new RowLayout());

		final Table table = new Table(tableGroup, SWT.BORDER | SWT.SINGLE |
				SWT.H_SCROLL | SWT.FULL_SELECTION);
		final TableColumn c1  = new TableColumn(table, SWT.LEFT);
		c1.setText("Prototype"); //$NON-NLS-1$
		c1.setWidth(150);
		final TableColumn c2  = new TableColumn(table, SWT.LEFT);
		c2.setText(LisaacMessages.getString("NewPrototypeWizard_32"));
		c2.setWidth(100);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final TableItem itemDefault = new TableItem(table, SWT.NONE);
		itemDefault.setText(new String[] {"OBJECT", ILisaacModel.inherit_shared}); //$NON-NLS-1$

		Composite buttonGroup = new Composite(tableGroup, SWT.NONE);
		buttonGroup.setLayout(new GridLayout());
		Button tableAdd = new Button(buttonGroup, SWT.PUSH);
		tableAdd.setFont(parent.getFont());
		tableAdd.setText(LisaacMessages.getString("NewPrototypeWizard_34"));
		tableAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InputParentDialog input = new InputParentDialog(getShell(), "New Parent...");
				if (input.open() == InputDialog.OK) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(new String[] {input.getParentName(), input.getParentType()});		
				}
			}
		});
		Button tableRemove= new Button(buttonGroup, SWT.PUSH);
		tableRemove.setFont(parent.getFont());
		tableRemove.setText(LisaacMessages.getString("NewPrototypeWizard_35"));
		tableRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int[] indices = table.getSelectionIndices();
				if (indices != null && indices.length > 0) {
					int index = indices[0];
					table.remove(index);
				}
			}
		});
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		tableGroup.setLayoutData(gridData);

		tableInherit = table;
		//

		label = new Label(parent, SWT.NONE);
		label.setText(LisaacMessages.getString("NewPrototypeWizard_36"));
		label.setFont(parent.getFont());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = SWT.CENTER;
		generateMain = new Button(parent, SWT.CHECK);
		generateMain.setText(LisaacMessages.getString("NewPrototypeWizard_37"));
		generateMain.setLayoutData(gridData);
		generateConstructor = new Button(parent, SWT.CHECK);
		generateConstructor.setText(LisaacMessages.getString("NewPrototypeWizard_38"));
		generateConstructor.setLayoutData(gridData);
	}

	protected String getInitialFileName() {
		return INITIAL_PROTOTYPE_NAME;
	}
}
