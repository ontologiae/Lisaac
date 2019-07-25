package org.lisaac.ldt.wizards;

import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.lisaac.ldt.LisaacMessages;


public abstract class AbstractNewFileWizardPage extends WizardPage {

	/** initial source folder selection */
	private IStructuredSelection selection;

	/** source folder path */
	private Text containerText;

	/** file name */
	private Text fileText;
	
	/** file extension */
	private String fileExtension = ""; //$NON-NLS-1$

	
	protected AbstractNewFileWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName);
		this.selection = selection;
		setPageComplete(false);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	/**
	 * set the extension of files created with this wizard.
	 */
	protected void setFileExtension(String ext) {
		fileExtension = ext;
	}
	
	/**
	 * Content File Initialisation.
	 */
	abstract protected InputStream getInitialContents(String filename);
	
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(LisaacMessages.getString("AbstractNewFileWizardPage_1"));

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		Button button = new Button(container, SWT.PUSH);
		button.setText(LisaacMessages.getString("AbstractNewFileWizardPage_2"));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText(LisaacMessages.getString("AbstractNewFileWizardPage_3"));

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		//
		createAdvancedControls(container);
		//
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	protected void createAdvancedControls(Composite parent) {
		// should be override
	}
	
	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
				fileText.setFocus();
			}
		}
		fileText.setText(getInitialFileName());
	}

	protected abstract String getInitialFileName();
	
	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				LisaacMessages.getString("AbstractNewFileWizardPage_4"));
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] results = dialog.getResult();
			if (results.length == 1) {
				Object result = results[0];
				if (result instanceof IPath) {
					IPath ipath = (IPath) result;
					containerText.setText(ipath.toString());
				}
			}
		}
	}
	
	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
		String container = getContainerName();
		String fileName = getFileName();

		if (container.length() == 0) {
			updateStatus(LisaacMessages.getString("AbstractNewFileWizardPage_5"));
			return;
		}
		if (fileName.length() == 0) {
			updateStatus(LisaacMessages.getString("AbstractNewFileWizardPage_6"));
			return;
		}
		// TODO check container -> source folder !
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	/**
	 * @see WizardPage#isPageComplete()
	 */
	public boolean isPageComplete() {
		return !checkFolderForExistingFile() && super.isPageComplete();
	}

	/**
	 * Finds the current directory where the file should be created
	 */
	protected boolean checkFolderForExistingFile() {
		IContainer container = getFileContainer();
		if (container != null) {
			IResource file = container.getFile(new Path(fileText.getText()
					.trim()));
			if (file != null && file.exists()) {
				this.setErrorMessage(LisaacMessages.getString("AbstractNewFileWizardPage_7"));
				return true;
			}
		}
		return false;
	}
	
	private IContainer getFileContainer() {
		if (containerText.getText() != null) {
			IPath containerPath = new Path(containerText.getText().trim());
			IContainer container = null;
			if (containerPath.segmentCount() > 1) {
				container = ResourcesPlugin.getWorkspace().getRoot().getFolder(
						containerPath);
			} else {
				if (containerPath.segmentCount() == 1) {
					// this is a project
					container = ResourcesPlugin.getWorkspace().getRoot()
							.getProject(containerText.getText().trim());
				}
			}
			if (container != null && container.exists()) {
				return container;
			}
		}
		return null;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			String fileName = fileText.getText().trim();
			if (getFileContainer() != null
					&& fileName.equalsIgnoreCase(getInitialFileName())) {
				fileText.setFocus();
				fileText.setText(fileName);
				fileText.setSelection(0, fileName.length()
						- (new Path(getInitialFileName())).getFileExtension()
								.length() - 1);
			}
		}
	}
}
