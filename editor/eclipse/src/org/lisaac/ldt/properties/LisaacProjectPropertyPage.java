package org.lisaac.ldt.properties;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.PropertyPage;
import org.lisaac.ldt.model.LisaacModel;

public class LisaacProjectPropertyPage extends PropertyPage {

	private List pathValueList;
	
	/**
	 * Constructor for SamplePropertyPage.
	 */
	public LisaacProjectPropertyPage() {
		super();
	}

	private void addSection(Composite parent) {	
		Composite composite = createDefaultComposite(parent);
		Label label = new Label(composite, SWT.NONE);
		label.setText("Project Path");

		pathValueList = new List(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		pathValueList.setLayoutData(gd);

		Button refreshPathButton = new Button(parent, SWT.PUSH);
		refreshPathButton.setText("Refresh Lisaac Path");
		refreshPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshList();
			}
		});
		refreshList();
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addSection(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	public void refreshList() {
		pathValueList.removeAll();
		
		// Populate list
		IProject project = (IProject) getElement();
		LisaacModel model = LisaacModel.getModel(project);
		if (model != null) {
			model.getPathManager().refreshPath(project);
			
			Iterator<String> it = model.getPathManager().getPathIterator();
			while (it.hasNext()) {
				pathValueList.add(it.next());
			}
		}
	}
	
	protected void performDefaults() {
	}
	
	public boolean performOk() {
		return true;
	}
}