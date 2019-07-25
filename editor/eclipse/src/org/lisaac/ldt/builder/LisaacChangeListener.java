package org.lisaac.ldt.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.lisaac.ldt.model.LisaacModel;

public class LisaacChangeListener implements IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();

		if (delta != null) {
			IResource resource = delta.getResource(); 
			if (resource != null) {
				if (resource instanceof IWorkspaceRoot) {
					delta = (IResourceDelta) (delta.getAffectedChildren())[0];// TODO go deeper in the delta
					resource = delta.getResource(); 
				}
				if (resource != null) {
					IProject project = (IProject) resource.getProject();
					LisaacModel model = LisaacModel.getModel(project);

					if (model != null) {
						model.setCompiled(false);
					}
				}
			}
		}
	}
}
