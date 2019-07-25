package org.lisaac.ldt.model;

import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.builder.ILisaacErrorHandler;
import org.lisaac.ldt.editors.AbstractLisaacEditor;
import org.lisaac.ldt.editors.LisaacEditor;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.lip.LIP;

/**
 * Represents Lisaac model of a project
 * @author Damien Bouvarel
 */
public class LisaacModel implements ILisaacModel{

	/** list of all encoutered models */
	private static HashMap<IProject,LisaacModel> modelList;

	/** list of all legal prototypes path */
	private LisaacPath modelPath;

	/** list of all encountered prototypes */
	private HashMap<String,Prototype> prototypes;

	/** lip makefile of this model */
	private LIP lipCode;

	/** project associated with this model */
	private IProject project;

	/** error handler */
	private ILisaacErrorHandler reporter;

	/** lisaac parser */
	private LisaacParser parser;

	/** string aliaser */
	private AliasString aliasString;

	/** modification flag */
	private boolean isProjectCompiled;
	
	public static AbstractLisaacEditor currentEditor = null;


	public LisaacModel(IProject project) {
		this.project = project;
		prototypes = new HashMap<String,Prototype>();
		aliasString = new AliasString();

		// add this model to the model list
		if (modelList == null) {
			modelList = new HashMap<IProject,LisaacModel>();
		}
		modelList.put(project, this);

		// create lisaac path
		modelPath = new LisaacPath(project, "make.lip"); // TODO get lip from property page
		isProjectCompiled = false;
	}

	public IProject getProject() {
		return project;
	}
	public void setProject(IProject project) {
		this.project = project;
	}

	public AliasString getAliasString() {
		return aliasString;
	}
	public LisaacParser getParser() {
		return parser;
	}
	public LisaacPath getPathManager() {
		return modelPath;
	}
	public LIP getLipCode() {
		return lipCode;
	}

	public Prototype getPrototype(String name) throws CoreException {
		Prototype result=null;

		if (prototypes != null) {
			result = prototypes.get(name); // prototype is already cached

			if (result == null && modelPath != null) {
				// cache new prototype
				String prototypePath = modelPath.getFullPath(name);
				if (prototypePath != null) {
					IPath location = new Path(prototypePath);
					IPath projectLocation = project.getLocation();
					IFile file = null;

					if (projectLocation.isPrefixOf(location)) {
						// the file is inside the workspace
						location = location.removeFirstSegments(projectLocation.segmentCount());
						file = project.getFile(location);
					} else {
						// file is outside workspace : create link in /lib
						IFolder lib = project.getFolder("lib");
						if (!lib.exists()) {
							lib.create(false, true, null);
						}
						file = lib.getFile(new Path(location.lastSegment()));
						if (! file.isAccessible() && ! file.exists()) {
							file.createLink(location, IResource.NONE, null);
							//
							//ResourceAttributes attrib = new ResourceAttributes();
							//attrib.setReadOnly(true);
							//file.setResourceAttributes(attrib);
							//
						}
					}
					result = parsePrototype(file, file.getContents(), new ILisaacErrorHandler() {
						public void fatalError(String msg, Position position) {				
						}
						public void semanticError(String msg, Position position) {					
						}
						public void syntaxError(String msg, Position position) {			
						}
						public void warning(String msg, Position position) {
						}
						public void enableErrorReport(boolean enable) {
						}
					});
				}
			}
		}
		return result;
	}

	public void refreshPresentation() {		
		final IWorkbenchPart part = currentEditor;			
		Display display = PlatformUI.getWorkbench().getDisplay();

		if (currentEditor != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					((AbstractLisaacEditor)part).refreshPresentation();
				}
			});
		}
	}

	public void accept(final ILisaacFileVisitor visitor) {
		try {
			project.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					if (resource instanceof IFile) {
						if (resource.getName().endsWith(".li")) {
							String name = extractPrototypeName(resource.getName());
							visitor.visit(getPrototype(name));
						}
					}
					return true;
				}		
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** parse and create part of current model */
	public Prototype parsePrototype(IFile file, InputStream contents, ILisaacErrorHandler reporter) {
		this.reporter = reporter;

		String name = file.getName();
		String prototypeName = extractPrototypeName(name);

		modelPath.addPath(prototypeName, file.getLocation());

		parser = new LisaacParser(prototypeName, contents, this);
		Prototype prototype = new Prototype(file, prototypeName, this);

		ILisaacContext context = parser.readContext();
		while (context != null) {
			if (context.parseDefinition(prototype)) { 
				context = parser.readContext();
			} else {
				context = context.getNextContext();
			}
		}
		parser.setLastSection(null);// close last section

		// add new prototype to current model
		prototypes.put(prototype.getName(), prototype);
		return prototype;
	}

	/** remove part of current model */
	public void removePrototype(IResource resource) {
		prototypes.remove(extractPrototypeName(resource.getName()));
	}

	/** parse and create part of current model 
	 * @throws CoreException */
	public void parseLip(String name, InputStream contents, ILisaacErrorHandler reporter) throws CoreException {
		this.reporter = reporter;

		lipCode = new LIP(name);
		LipParser lipParser = new LipParser(contents, this);
		if (! lipParser.parse()) {
			reporter.syntaxError("Syntax error.", lipParser.getPosition());
			return;
		}

	/*	// parse lip parents
		for (int i=0; i<lipCode.getParentCount(); i++) {
			String parent = lipCode.getParent(i);
			IFile file=null;
			if (parent.equals("")) { // lisaac make.lip
				// TODO get lisaac directory
				return;
			} else {
				file = project.getFile(parent);
			}
			lipParser = new LipParser(file.getContents(), this);
			if (! lipParser.parse()) {
				reporter.syntaxError("Syntax error.", lipParser.getPosition());
			}
		}*/
	}

	/** remove part of current model */
	public void removeLip(IResource resource) {
		// TODO remove lip
	}

	/** get little name of prototype instead of full path */
	public static String extractPrototypeName(String s) {
		int idx = s.indexOf('.');
		if (idx != -1) {
			return (s.substring(0, idx)).toUpperCase();
		}
		return s.toUpperCase();
	}

	public ILisaacErrorHandler getReporter() {
		return reporter;
	}

	/**
	 * Get the lisaac model associated with the given project.
	 * @param p A lisaac project
	 * @return The associated lisaac model
	 */
	public static LisaacModel getModel(IProject p) {
		if (modelList != null) {
			return modelList.get(p);
		}
		return null;
	}

	public static Prototype getCurrentPrototype() throws CoreException {
		IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (w == null) {
			return null;
		}
		IPartService service = w.getPartService(); 
		if (service == null) {
			return null;
		}
		IWorkbenchPart part = service.getActivePart();
		if (part == null ||  !(part instanceof LisaacEditor)) {
			part = currentEditor; 
		}
		if (part instanceof LisaacEditor) {
			IProject project = ((LisaacEditor)part).getProject();
			String filename = ((LisaacEditor)part).getFileName();

			LisaacModel model = LisaacModel.getModel(project);
			if (model != null) {
				return model.getPrototype(extractPrototypeName(filename));
			}
		}
		return null;
	}

	public void refreshPath() {
		// create lisaac path
		modelPath = new LisaacPath(project, "make.lip"); // TODO get lip from property page
	}

	public boolean needCompilation() {
		return !isProjectCompiled;
	} 
	
	public void setCompiled(boolean done) {
		isProjectCompiled = done;
	}
}
