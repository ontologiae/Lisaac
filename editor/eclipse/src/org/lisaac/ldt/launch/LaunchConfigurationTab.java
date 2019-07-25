package org.lisaac.ldt.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.lip.LIP;
import org.lisaac.ldt.model.lip.LIPSlotCode;


public class LaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	public static final String LISAAC_LAUNCH_PROJECT = "launchProject"; //$NON-NLS-1$

	public static final String LISAAC_LAUNCH_PROTOTYPE = "mainPrototype"; //$NON-NLS-1$
	public static final String LISAAC_LAUNCH_COMPILER = "launchCompiler"; //$NON-NLS-1$

	public static final String LISAAC_LAUNCH_PROGRAM = "launchProgram"; //$NON-NLS-1$
	public static final String LISAAC_LAUNCH_ARGUMENTS = "programArguments"; //$NON-NLS-1$

	public static final String LISAAC_LAUNCH_OPTION = "lipOption"; //$NON-NLS-1$
	public static final String LISAAC_LAUNCH_OPTION_ARG = "lipOptionARG"; //$NON-NLS-1$

	// Project UI widget.
	private Text projectText;
	private Text mainPrototypeText;
	private Table lipTable;

	// associated lip
	private LIP lipCode;

	private Button doLaunchCompiler;

	private Button doLaunchProgram;
	private Text argumentsText;

	/**
	 * @see ILaunchConfigurationTab#createControl(Composite)
	 */
	public void createControl(Composite parent) {		
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);		
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 1;
		comp.setLayout(topLayout);		

		// Project Options
		Group projComp = new Group(comp, SWT.SHADOW_IN);
		projComp.setText(LisaacMessages.getString("LaunchConfigurationTab.6")); //$NON-NLS-1$
		GridLayout projLayout = new GridLayout();
		projLayout.numColumns = 3;
		projLayout.marginHeight = 0;
		projLayout.marginWidth = 0;
		projComp.setLayout(projLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projComp.setLayoutData(gd);
		projComp.setFont(font);

		Label fProjLabel = new Label(projComp, SWT.NONE);
		fProjLabel.setText(LisaacMessages.getString("LaunchConfigurationTab.7")); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 1;
		fProjLabel.setLayoutData(gd);
		fProjLabel.setFont(font);

		projectText = new Text(projComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		projectText.setLayoutData(gd);
		projectText.setFont(font);
		this.projectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
		Button browseButton = new Button(projComp, SWT.PUSH);
		browseButton.setText(LisaacMessages.getString("LaunchConfigurationTab.9")); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 1;
		browseButton.setLayoutData(gd);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseContainer(projectText);
			}
		});

		Label label = new Label(projComp, SWT.NONE);
		label.setText(LisaacMessages.getString("LaunchConfigurationTab.8")); //$NON-NLS-1$
		gd = new GridData();
		label.setLayoutData(gd);
		label.setFont(font);

		mainPrototypeText = new Text(projComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		mainPrototypeText.setLayoutData(gd);
		mainPrototypeText.setFont(font);
		mainPrototypeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
		browseButton = new Button(projComp, SWT.PUSH);
		browseButton.setText(LisaacMessages.getString("LaunchConfigurationTab.9")); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		browseButton.setLayoutData(gd);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseFile(mainPrototypeText);
			}
		});

		Label separator = new Label (comp, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// compiler options
		Group compilerOptions = new Group(comp, SWT.SHADOW_IN);
		compilerOptions.setText(LisaacMessages.getString("LaunchConfigurationTab.12")); //$NON-NLS-1$
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		compilerOptions.setLayout(gl);
		gd = new GridData(GridData.FILL_BOTH);
		compilerOptions.setLayoutData(gd);

		doLaunchCompiler = new Button(compilerOptions, SWT.CHECK);
		doLaunchCompiler.setText("Always Compile (cannot run program)");
		doLaunchCompiler.setSelection(false);
		gd = new GridData();
		gd.horizontalSpan = 3;
		doLaunchCompiler.setLayoutData(gd);
		doLaunchCompiler.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				doLaunchProgram.setEnabled(! doLaunchCompiler.getSelection());
				argumentsText.setEnabled(! doLaunchCompiler.getSelection());
				updateLaunchConfigurationDialog();
			}
		});

		// lip options
		Composite tableGroup = new Composite(compilerOptions, SWT.NONE);
		tableGroup.setLayout(new GridLayout(1, false));
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 3;
		tableGroup.setLayoutData(gd);

		lipTable = new Table(tableGroup, SWT.BORDER | SWT.SINGLE |
				SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK);
		final TableColumn c1  = new TableColumn(lipTable, SWT.LEFT);
		c1.setText(LisaacMessages.getString("LaunchConfigurationTab.15")); //$NON-NLS-1$
		c1.setWidth(130);
		final TableColumn c2  = new TableColumn(lipTable, SWT.LEFT);
		c2.setText(LisaacMessages.getString("LaunchConfigurationTab.16")); //$NON-NLS-1$
		c2.setWidth(80);
		final TableColumn c3  = new TableColumn(lipTable, SWT.LEFT);
		c3.setText(LisaacMessages.getString("LaunchConfigurationTab.17")); //$NON-NLS-1$
		c3.setWidth(100);
		lipTable.setHeaderVisible(true);
		lipTable.setLinesVisible(true);
		lipTable.setItemCount(4);
		lipTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		final TableEditor editor = new TableEditor(lipTable);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		lipTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the
				// Table
				Text newEditor = new Text(lipTable, SWT.NONE);
				newEditor.setText(item.getText(1));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(1, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, 1);
				//
				updateLaunchConfigurationDialog();
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		tableGroup.setLayoutData(gridData);

		separator = new Label (comp, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// program options
		Group programOptions = new Group(comp, SWT.SHADOW_IN);
		programOptions.setText("Program Options"); //$NON-NLS-1$
		gl = new GridLayout();
		gl.numColumns = 2;
		programOptions.setLayout(gl);
		programOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		doLaunchProgram = new Button(programOptions, SWT.CHECK);
		doLaunchProgram.setText("Run the program");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		doLaunchProgram.setLayoutData(gd);
		doLaunchProgram.setSelection(true);
		doLaunchProgram.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (argumentsText != null) {
					argumentsText.setEnabled(doLaunchProgram.getSelection());
				}
				updateLaunchConfigurationDialog();
			}
		});

		label = new Label(programOptions, SWT.NONE);
		label.setText("Command-Line:");
		label.setLayoutData(new GridData(GridData.BEGINNING));

		argumentsText = new Text(programOptions, SWT.SINGLE | SWT.BORDER);
		argumentsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		argumentsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return LisaacMessages.getString("LaunchConfigurationTab.18"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String projectName = configuration.getAttribute(LISAAC_LAUNCH_PROJECT, ""); //$NON-NLS-1$
			projectText.setText(projectName);	

			String mainPrototypeName = configuration.getAttribute(LISAAC_LAUNCH_PROTOTYPE, ""); //$NON-NLS-1$
			mainPrototypeText.setText(mainPrototypeName);	

			boolean check = configuration.getAttribute(LISAAC_LAUNCH_COMPILER, false);
			doLaunchCompiler.setSelection(check);	

			check = configuration.getAttribute(LISAAC_LAUNCH_PROGRAM, true);
			doLaunchProgram.setSelection(check);	

			String commandLine = configuration.getAttribute(LISAAC_LAUNCH_ARGUMENTS, ""); //$NON-NLS-1$
			argumentsText.setText(commandLine);	

			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			LisaacModel model = LisaacModel.getModel(project);
			if (model != null) {
				lipCode = model.getLipCode();
				if (lipCode != null) {
					// update table
					lipTable.setItemCount(0);
					for (int i=0; i<lipCode.getMethodCount(); i++) {
						LIPSlotCode method = lipCode.getMethod(i);
						if (method.isPublic()) {	
							String methodComment = method.getComment();
							if (methodComment == null) {
								methodComment = LisaacMessages.getString("LaunchConfigurationTab.23"); //$NON-NLS-1$
							}
							methodComment = methodComment.replaceAll("\t", ""); //$NON-NLS-1$ //$NON-NLS-2$
							methodComment = methodComment.replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$

							if (! lipTable.isDisposed()) {
								TableItem item = new TableItem (lipTable, SWT.NONE);
								item.setText(0, method.getName());
								item.setText(2, methodComment);

								if (method.getName().compareTo(ILisaacModel.slot_debug_mode) == 0) {
									item.setChecked(true);
									// TODO read-only -> changed in launch()
									item.setText(1, getLaunchConfigurationDialog().getMode());
								} 
							}
						}
					}
				}
			}
		} catch (CoreException ce) {
			// Log the error to the Eclipse log.
			IStatus status = new Status(IStatus.ERROR,
					LisaacPlugin.PLUGIN_ID, 0,
					"Error in Lisaac launch: " + //$NON-NLS-1$
					ce.getMessage(), ce);
			LisaacPlugin.log(status);	   
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LISAAC_LAUNCH_PROJECT, projectText.getText());
		configuration.setAttribute(LISAAC_LAUNCH_PROTOTYPE, mainPrototypeText.getText());

		configuration.setAttribute(LISAAC_LAUNCH_COMPILER, doLaunchCompiler.getSelection());
		configuration.setAttribute(LISAAC_LAUNCH_PROGRAM, doLaunchProgram.getSelection());
		configuration.setAttribute(LISAAC_LAUNCH_ARGUMENTS, argumentsText.getText());

		TableItem[] options = lipTable.getItems();
		if (options != null && options.length > 0) {
			for (int i=0; i<options.length; i++) {
				configuration.removeAttribute(LISAAC_LAUNCH_OPTION+i);// remove options
				configuration.removeAttribute(LISAAC_LAUNCH_OPTION_ARG+i);// remove options
			}			
			int count = 0;
			for (int i=0; i<options.length; i++) {
				TableItem item = options[i];
				if (item.getChecked()) {
					configuration.setAttribute(LISAAC_LAUNCH_OPTION+count, item.getText(0));
					configuration.setAttribute(LISAAC_LAUNCH_OPTION_ARG+count, item.getText(1));
					count++;
				}
			}
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IProject project = getSelectedProject();	
		String projectName = "";	 //$NON-NLS-1$
		String mainPrototypeName = ""; //$NON-NLS-1$

		if (project != null) {			
			projectName = project.getName();
			mainPrototypeName = projectName + ".li"; //$NON-NLS-1$
		}
		configuration.setAttribute(LISAAC_LAUNCH_PROJECT, projectName);
		configuration.setAttribute(LISAAC_LAUNCH_PROTOTYPE, mainPrototypeName);

		configuration.setAttribute(LISAAC_LAUNCH_COMPILER, false);
		configuration.setAttribute(LISAAC_LAUNCH_PROGRAM, true);
		configuration.setAttribute(LISAAC_LAUNCH_ARGUMENTS, ""); //$NON-NLS-1$

		if (lipTable != null) {
			TableItem[] options = lipTable.getItems();
			if (options != null && options.length > 0) {
				for (int i=0; i<options.length; i++) {
					configuration.removeAttribute(LISAAC_LAUNCH_OPTION+i);// remove options
					configuration.removeAttribute(LISAAC_LAUNCH_OPTION_ARG+i);// remove options
				}
			}
		}
	}

	/** 
	 * Gets the current project selected in the workbench.
	 * @return the current project selected in the workbench.
	 */
	private IProject getSelectedProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				ISelection selection = page.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection)selection;
					if (!ss.isEmpty()) {
						Object obj = ss.getFirstElement();
						if (obj instanceof IResource) {
							IResource i = (IResource) obj;
							IProject pro = i.getProject();
							return pro;														
						}
					}
				}
				// If the editor has the focus...
				IEditorPart part = page.getActiveEditor();
				if (part != null) {
					IEditorInput input = part.getEditorInput();
					IFile file = (IFile) input.getAdapter(IFile.class);
					return file.getProject();
				}				
			}
		}
		return null;							
	}


	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	private void handleBrowseContainer(Text text) {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), null, false,
		"Select Folder"); //$NON-NLS-1$
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] results = dialog.getResult();
			if (results.length == 1) {
				Object result = results[0];
				if (result instanceof IPath) {
					IPath ipath = (IPath) result;
					text.setText(ipath.toString());
				}
			}
		}
	}

	private void handleBrowseFile(Text text) {
		ResourceSelectionDialog dialog = new ResourceSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), "Select File"); //$NON-NLS-1$
		dialog.setInitialSelections(new Object[0]);
		if (dialog.open() == ResourceSelectionDialog.OK) {
			Object[] results = dialog.getResult();
			if (results.length == 1) {
				Object result = results[0];
				if (result instanceof IResource) {
					text.setText(((IResource) result).getName());
				}
			}
		}
	}

}
