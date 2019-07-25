package org.lisaac.ldt.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.ILisaacColor;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = LisaacPlugin.getDefault().getPreferenceStore();
		//store.setDefault(PreferenceConstants.P_LISAAC_PATH, "TODO");

		store.setDefault(PreferenceConstants.P_LISAAC_INDENT, true);
		store.setDefault(PreferenceConstants.P_LISAAC_HOVER, true);
		store.setDefault(PreferenceConstants.P_LISAAC_FOLD, true);
		store.setDefault(PreferenceConstants.P_LISAAC_COMPLETION, true);
		store.setDefault(PreferenceConstants.P_LISAAC_COMPLETION_DELAY, 200);
		
		// Colors for syntax highlighting.
		store.setDefault(
				ILisaacColor.PREF_COMMENT,
				StringConverter.asString(ILisaacColor.COMMENT));
		store.setDefault(
				ILisaacColor.PREF_PROTOTYPE,
				StringConverter.asString(ILisaacColor.PROTOTYPE));
		store.setDefault(
				ILisaacColor.PREF_PROTOTYPE_STYLE,
				StringConverter.asString(ILisaacColor.PROTOTYPE_STYLE));
		store.setDefault(
				ILisaacColor.PREF_KEYWORD,
				StringConverter.asString(ILisaacColor.KEYWORD));
		store.setDefault(
				ILisaacColor.PREF_SLOT,
				StringConverter.asString(ILisaacColor.UNDEFINED));
		store.setDefault(
				ILisaacColor.PREF_LOCAL_SLOT,
				StringConverter.asString(ILisaacColor.DEFAULT));
		store.setDefault(
				ILisaacColor.PREF_CHARACTER,
				StringConverter.asString(ILisaacColor.CHARACTER));
		store.setDefault(
				ILisaacColor.PREF_STRING,
				StringConverter.asString(ILisaacColor.STRING));
		store.setDefault(
				ILisaacColor.PREF_NUMBER,
				StringConverter.asString(ILisaacColor.NUMBER));
		store.setDefault(
				ILisaacColor.PREF_OPERATOR,
				StringConverter.asString(ILisaacColor.OPERATOR));
		store.setDefault(
				ILisaacColor.PREF_EXTERNAL,
				StringConverter.asString(ILisaacColor.EXTERNAL));
		
		// styles
		store.setDefault(ILisaacColor.STYLE_CHARACTER, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_KEYWORD, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_LOCAL_SLOT, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_NUMBER, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_OPERATOR, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_PROTOTYPE, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_PROTOTYPE_STYLE, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_SLOT, ILisaacColor.PREF_NORMAL);
		store.setDefault(ILisaacColor.STYLE_STRING, ILisaacColor.PREF_NORMAL);
	}
}

