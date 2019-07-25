package org.lisaac.ldt.wizards;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.lisaac.ldt.model.ILisaacModel;

public class InputParentDialog extends InputDialog {
	
	private String parentType;
	
	public InputParentDialog(Shell parentShell, String dialogTitle) {
		super(parentShell, dialogTitle, "Parent prototype name", "", new IInputValidator() {
			public String isValid(String newText) {
				if (newText.length() == 0) {
					return "Empty name";
				}
				String correct = newText.toUpperCase();
				if (newText.compareTo(correct) != 0) {
					return "Invalid prototype name";
				}
				return null;
			}
		});
	}

	 /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
    	Composite c = (Composite) super.createDialogArea(parent);
    	
    	Label label = new Label(c, SWT.NONE);
    	label.setText("Choose Parent inheritance type");
    
    	String[] values = {
    			ILisaacModel.inherit_shared,
    			ILisaacModel.inherit_nonshared,
    			ILisaacModel.inherit_shared_expanded,
    			ILisaacModel.inherit_nonshared_expanded
    	};
    	parentType = values[0];
    	
    	final Combo combo = new Combo(c, SWT.NONE);
    	combo.setItems(values);
    	combo.select(0);
    	combo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				parentType = combo.getText();
			}
    	});
    	return c;
    }
    
    public String getParentName() {
    	return getValue();
    }
    
    public String getParentType() {
    	return parentType;
    }
}
