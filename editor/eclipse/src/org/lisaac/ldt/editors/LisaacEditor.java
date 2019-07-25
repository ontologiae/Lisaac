package org.lisaac.ldt.editors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.preferences.PreferenceConstants;

/**
 * Main class for the Lisaac editor
 * @author Damien Bouvarel
 */
public class LisaacEditor extends AbstractLisaacEditor {

	public LisaacEditor() {
		super();
	}

	public void refreshPresentation() {
		super.refreshPresentation();

		// refresh folding
		boolean doFold = LisaacPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_LISAAC_FOLD);
		if (! doFold) {
			removeFoldingStructure();
			return;
		}
		LisaacModel model = LisaacModel.getModel(getProject());
		if (model != null) {
			Prototype p;
			try {
				p = model.getPrototype(LisaacModel.extractPrototypeName(getFileName()));
				if (p != null) {
					updateFoldingStructure(p.getPositions());
				}
			} catch (CoreException e) {
			}
		}
	}
}
