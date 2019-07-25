package org.lisaac.ldt.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.ILisaacColor;

/**
 * Lisaac Syntax coloring preference page.
 */
public class LisaacColoringPage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	private Group colorGroup;
	
	private String[][] comboValues;
	
	public LisaacColoringPage() {
		super(GRID);
		setPreferenceStore(LisaacPlugin.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {   		   	
		colorGroup = new Group(getFieldEditorParent(),SWT.SHADOW_ETCHED_IN);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		colorGroup.setLayoutData(gd);
		colorGroup.setLayout(new GridLayout(2, false));
		colorGroup.setText(LisaacMessages.getString("LisaacColoringPage.0")); //$NON-NLS-1$

		String bold = LisaacMessages.getString("LisaacColoringPage.20");
		String italics = LisaacMessages.getString("LisaacColoringPage.21");
		String normal = LisaacMessages.getString("LisaacColoringPage.22");
		String underline = LisaacMessages.getString("LisaacColoringPage.23");
		String[][] values = {
				{normal, ILisaacColor.PREF_NORMAL}, 
				{bold, ILisaacColor.PREF_BOLD},
				{italics, ILisaacColor.PREF_ITALICS},
				{underline, ILisaacColor.PREF_UNDERLINE}
		};
		this.comboValues = values;
		
		
		addField(new ColorFieldEditor(
				ILisaacColor.PREF_COMMENT,
				LisaacMessages.getString("LisaacColoringPage.2"), colorGroup)); //$NON-NLS-1$

		
		createColorField(ILisaacColor.PREF_PROTOTYPE, ILisaacColor.STYLE_PROTOTYPE, 
				LisaacMessages.getString("LisaacColoringPage.3")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_PROTOTYPE_STYLE, ILisaacColor.STYLE_PROTOTYPE_STYLE, 
				LisaacMessages.getString("LisaacColoringPage.4")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_KEYWORD, ILisaacColor.STYLE_KEYWORD, 
				LisaacMessages.getString("LisaacColoringPage.5")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_SLOT, ILisaacColor.STYLE_SLOT, 
				LisaacMessages.getString("LisaacColoringPage.11")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_LOCAL_SLOT, ILisaacColor.STYLE_LOCAL_SLOT, 
				LisaacMessages.getString("LisaacColoringPage.12")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_CHARACTER, ILisaacColor.STYLE_CHARACTER, 
				LisaacMessages.getString("LisaacColoringPage.6")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_STRING, ILisaacColor.STYLE_STRING, 
				LisaacMessages.getString("LisaacColoringPage.7")); //$NON-NLS-1$
		
		createColorField(ILisaacColor.PREF_NUMBER, ILisaacColor.STYLE_NUMBER, 
				LisaacMessages.getString("LisaacColoringPage.8")); //$NON-NLS-1$

		createColorField(ILisaacColor.PREF_OPERATOR, ILisaacColor.STYLE_OPERATOR, 
				LisaacMessages.getString("LisaacColoringPage.9")); //$NON-NLS-1$
		
		
		addField(new ColorFieldEditor(
				ILisaacColor.PREF_EXTERNAL,
				LisaacMessages.getString("LisaacColoringPage.10"), colorGroup));	 //$NON-NLS-1$
	}

	private void createColorField(String prefColor, String prefStyle, String tokenName) {
		Composite c = new Composite(colorGroup, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		c.setLayoutData(gd);
		
		Label label = new Label(c, SWT.NONE);
		gd = new GridData(80, 15);
		label.setLayoutData(gd);
		label.setText(tokenName);
		
		Composite c2 = new Composite(c, SWT.NONE);
		addField(new ColorFieldEditor(prefColor, "", c2)); 
		
		c2 = new Composite(c, SWT.NONE);
		addField(new ComboFieldEditor(prefStyle, "", comboValues, c2)); //$NON-NLS-1$
	}

	public void init(IWorkbench workbench) {
	}
}