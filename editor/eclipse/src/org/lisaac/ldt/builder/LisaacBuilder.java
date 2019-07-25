package org.lisaac.ldt.builder;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.Position;

public class LisaacBuilder extends IncrementalProjectBuilder {

	private LisaacModel model;


	class LisaacDeltaVisitor implements IResourceDeltaVisitor {
		
		IProgressMonitor monitor;
		
		LisaacDeltaVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				buildLisaacFile(resource, monitor);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				removeLisaacFile(resource);
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				buildLisaacFile(resource, monitor);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class LisaacResourceVisitor implements IResourceVisitor {
		IProgressMonitor monitor;
		
		LisaacResourceVisitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		public boolean visit(IResource resource) {
			buildLisaacFile(resource, monitor);
			//return true to continue visiting children.
			return true;
		}
	}

	class LisaacErrorHandler implements ILisaacErrorHandler {

		private IFile file;
		private boolean doReport;
		
		public LisaacErrorHandler(IFile file) {
			this.file = file;
			this.doReport = true;
		}

		public void syntaxError(String msg, Position position) {
			if (doReport)
				addMarker(file, msg, position, IMarker.SEVERITY_ERROR);
		}

		public void semanticError(String msg, Position position) {
			if (doReport)
				addMarker(file, msg, position, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(String msg, Position position) {
			if (doReport)
				addMarker(file, msg, position, IMarker.SEVERITY_ERROR);
		}

		public void warning(String msg, Position position) {
			if (doReport)
				addMarker(file, msg, position, IMarker.SEVERITY_WARNING);
		}

		public void enableErrorReport(boolean enable) {
			doReport = enable;
		}
	}

	public static final String BUILDER_ID = "org.lisaac.ldt.builder"; //$NON-NLS-1$

	private static final String MARKER_TYPE = "org.lisaac.ldt.lisaacProblem"; //$NON-NLS-1$

	
	public static void addMarker(IFile file, String msg, Position position, int severity) {
		IMarker marker = LisaacBuilder.addMarker(file, msg, position.getLine(), severity);
		if (marker != null && position.hasRange()) {
			try {
				marker.setAttribute(IMarker.CHAR_START, position.getCharStart());
				marker.setAttribute(IMarker.CHAR_END, position.getCharEnd());
		
			} catch (CoreException e) {
			}
		}
	}

	public static IMarker addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			return marker;
		} catch (CoreException e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IProject[] build(int kind, Map args, IProgressMonitor monitor)
	throws CoreException {
		
		IProject project = getProject();
		model = LisaacModel.getModel(project);
		if (model == null) {
			// create lisaac model
			model = new LisaacModel(project);
			
		}
		
		monitor.beginTask(LisaacMessages.getString("LisaacBuilder.2"), 100); //$NON-NLS-1$
		
		model.refreshPresentation();
		monitor.worked(1);
		
		if (kind == CLEAN_BUILD) {
			model.refreshPath();
			monitor.worked(10);
			
			IContainer bin = project.getFolder("lib");
			if (bin.exists()) {
				bin.delete(true, monitor);
			}
		}
		
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(model.getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	private void buildLisaacFile(IResource resource, IProgressMonitor monitor) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			
			if (file.isHidden()) {
				return;
			}
			
			//System.out.println("VISITING => "+file.getName());
			
			if (resource.getName().endsWith(".li")) { //$NON-NLS-1$
				deleteMarkers(file);
				try {
					LisaacErrorHandler reporter = new LisaacErrorHandler(file);
					model.parsePrototype(file, file.getContents(), reporter);
				} catch (Exception e) {
				}
			} else if (resource.getName().endsWith(".lip")) { //$NON-NLS-1$
				deleteMarkers(file);
				try {
					LisaacErrorHandler reporter = new LisaacErrorHandler(file);
					model.parseLip(file.getName(), file.getContents(), reporter);
				} catch (Exception e) {
				}
			}
			monitor.worked(1);
		}
	}

	private void removeLisaacFile(IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (resource.getName().endsWith(".li")) {				 //$NON-NLS-1$
				deleteMarkers(file);
				try {
					model.removePrototype(file);
				} catch (Exception e) {
				}
			} else if (resource.getName().endsWith(".lip")) { //$NON-NLS-1$
				deleteMarkers(file);
				try {
					model.removeLip(file);
				} catch (Exception e) {
				}
			}
		}  
	}

	public static void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
	throws CoreException {
		try {
			if (model.getProject() != null) {
				model.getProject().accept(new LisaacResourceVisitor(monitor));
				monitor.done();
			}
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new LisaacDeltaVisitor(monitor));
		monitor.done();
	}
}
