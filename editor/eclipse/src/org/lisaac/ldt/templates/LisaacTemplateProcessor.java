package org.lisaac.ldt.templates;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.LisaacPlugin;

public class LisaacTemplateProcessor extends TemplateCompletionProcessor {


	/**
	 * Simply return all templates.
	 *
	 * @param contextTypeId the context type, ignored in this implementation
	 * @return all templates
	 */
	protected Template[] getTemplates(String contextTypeId) {
		return LisaacPlugin.getDefault().getTemplateStore().getTemplates();
	}

	/**
	 * Return the XML context type that is supported by this plug-in.
	 *
	 * @param viewer the viewer, ignored in this implementation
	 * @param region the region, ignored in this implementation
	 * @return the supported XML context type
	 */
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		return LisaacPlugin.getDefault().getContextTypeRegistry().getContextType(LisaacContextType.ID_CONTEXT_TYPE);
	}

	/**
	 * Always return the default image.
	 *
	 * @param template the template, ignored in this implementation
	 * @return the default template image
	 */
	protected Image getImage(Template template) {
		ImageDescriptor descr = LisaacPlugin.getImageDescriptor("icons/template.gif");
		return descr.createImage();
	}

}
