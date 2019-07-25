package org.lisaac.ldt.editors;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.lisaac.ldt.LisaacPlugin;

/**
 * Handle image decoration in Navigator View
 */
public class LisaacResourceDecorator implements ILightweightLabelDecorator {

	private static ImageDescriptor OVERLAY_ERROR = LisaacPlugin.getImageDescriptor("/icons/error_co.gif");
	private static ImageDescriptor OVERLAY_WARNING = LisaacPlugin.getImageDescriptor("/icons/warning_co.gif");

	private static ImageDescriptor OVERLAY_SOURCE_FOLDER = LisaacPlugin.getImageDescriptor("/icons/source-folder.gif");
	private static ImageDescriptor OVERLAY_LIB = LisaacPlugin.getImageDescriptor("/icons/library.gif");

	public static String SOURCE_FOLDER_PROPERTY = "source_folder";
	public static String LIB_PROPERTY = "lib";
	
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IResource) {
			int type;
			try {
				type = getTypeFromMarkers((IResource) element);
				if (type == 1) {
					decoration.addOverlay(OVERLAY_WARNING);
				} else if (type == 2) {
					decoration.addOverlay(OVERLAY_ERROR);
				}
				if (element instanceof IFolder) {
					String sourceFolder = ((IResource) element).getPersistentProperty(
							new QualifiedName("", SOURCE_FOLDER_PROPERTY));
					if (sourceFolder != null && sourceFolder != "") {
						decoration.addOverlay(OVERLAY_SOURCE_FOLDER);
					} else {
						String lib = ((IResource) element).getPersistentProperty(
								new QualifiedName("", LIB_PROPERTY));
						if (lib != null && lib != "") {
							decoration.addOverlay(OVERLAY_LIB);
						}
					}
				}
			} catch (CoreException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int getTypeFromMarkers(IResource res) throws CoreException {
		if (res == null || !res.isAccessible()) {
			return 0;
		}
		int markerType = 0;

		IMarker[] markers = res.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		if (markers != null) {
			for (int i = 0; i < markers.length && (markerType != 2); i++) {
				IMarker curr = markers[i];

				int priority = curr.getAttribute(IMarker.SEVERITY, -1);
				if (priority == IMarker.SEVERITY_WARNING) {
					markerType = 1;
				} else if (priority == IMarker.SEVERITY_ERROR) {
					markerType = 2;
				}
			}
		}
		return markerType;
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	// TODO refresh decorator on marker update
	public void addListener(ILabelProviderListener listener) {
	}

	
	public void removeListener(ILabelProviderListener listener) {		
	}
}
