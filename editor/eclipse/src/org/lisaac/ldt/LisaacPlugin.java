package org.lisaac.ldt;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.lisaac.ldt.builder.LisaacChangeListener;
import org.lisaac.ldt.builder.LisaacNature;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.templates.LisaacContextType;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LisaacPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.lisaac.ldt"; //$NON-NLS-1$

	// The shared instance
	private static LisaacPlugin plugin;


	/** The template store. */
	private TemplateStore fStore;
	/** The context type registry. */
	private ContextTypeRegistry fRegistry;


	private static final String TEMPLATES_KEY = "org.lisaac.ldt.templatepreferences"; //$NON-NLS-1$


	public TemplateStore getTemplateStore() {
		if (fStore == null) {
			fStore = new ContributionTemplateStore (
					getContextTypeRegistry(), LisaacPlugin
					.getDefault().getPreferenceStore(),
					TEMPLATES_KEY);
			try {
				fStore.load();
			} catch (IOException e) {
				LisaacPlugin
				.getDefault()
				.getLog()
				.log(
						new Status(
								IStatus.ERROR,
								PLUGIN_ID, IStatus.OK, "", e)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return fStore;
	}

	public ContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			// create an configure the contexts available in the template editor
			ContributionContextTypeRegistry registry= new ContributionContextTypeRegistry();
			registry.addContextType(LisaacContextType.ID_CONTEXT_TYPE);

			fRegistry= registry;
		}
		return fRegistry;
	}

	/**
	 * The constructor
	 */
	public LisaacPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		// to notify project modifications
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new LisaacChangeListener());
	
		// build all lisaac projects
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i=0; i<projects.length; i++) {
			try {
				if (projects[i] != null && projects[i].isOpen()) {
					if (projects[i].getNature(LisaacNature.NATURE_ID) != null) {
						try {
							IContainer bin = projects[i].getFolder("lib");
							if (bin.exists()) {
								bin.delete(true, null);
							}
							
							// clean all lisaac projects to get started
							projects[i].build(IncrementalProjectBuilder.FULL_BUILD, null);
							
						} catch (Exception e) {
							log(new Status(IStatus.ERROR,
									PLUGIN_ID, IStatus.OK, "Error loading "+projects[i].getName(), e)); //$NON-NLS-1$
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LisaacPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Logs the specified status with this plug-in's log.
	 *
	 * @param status status to log.
	 */
	public static void log(final IStatus status) {
		getDefault().getLog().log(status);
	}
}
