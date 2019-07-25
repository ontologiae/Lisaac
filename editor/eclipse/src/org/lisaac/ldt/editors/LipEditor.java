package org.lisaac.ldt.editors;

import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Main class for the Lip editor
 * @author Damien Bouvarel
 */
public class LipEditor extends AbstractLisaacEditor {

	public LipEditor() {
		super();
	}
	
	/**
     * @see AbstractTextEditor#getAdapter(java.lang.Class)
     */
	public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            return null; // no outline
         } else {
             return super.getAdapter(required);
         }
    }
}
