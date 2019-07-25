package org.lisaac.ldt.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.LisaacPlugin;

/**
 * Lisaac Syntax coloring preference page.
 */
public class LisaacEditorPage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	public LisaacEditorPage() {
		super(GRID);
		setPreferenceStore(LisaacPlugin.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {   
		Composite g = getFieldEditorParent();
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		g.setLayoutData(gd);
	

		addField(new BooleanFieldEditor(PreferenceConstants.P_LISAAC_INDENT,
				LisaacMessages.getString("LisaacEditorPage.0"), g)); //$NON-NLS-1$
		addField(new BooleanFieldEditor(PreferenceConstants.P_LISAAC_HOVER,
					LisaacMessages.getString("LisaacEditorPage.1"), g)); //$NON-NLS-1$
		addField(new BooleanFieldEditor(PreferenceConstants.P_LISAAC_FOLD,
				LisaacMessages.getString("LisaacEditorPage.5"), g)); //$NON-NLS-1$
		
		Group completionGroup = new Group(g,SWT.SHADOW_ETCHED_IN);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 2;
		completionGroup.setLayoutData(gd);
		completionGroup.setText(LisaacMessages.getString("LisaacEditorPage.2")); //$NON-NLS-1$

		addField(new BooleanFieldEditor(PreferenceConstants.P_LISAAC_COMPLETION,
				LisaacMessages.getString("LisaacEditorPage.3"), completionGroup)); //$NON-NLS-1$
		
		addField(new StringFieldEditor(PreferenceConstants.P_LISAAC_COMPLETION_DELAY, 
				LisaacMessages.getString("LisaacEditorPage.4"), completionGroup)); //$NON-NLS-1$
	}
	
	public void init(IWorkbench workbench) {
	}
}