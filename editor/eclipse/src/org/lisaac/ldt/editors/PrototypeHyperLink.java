package org.lisaac.ldt.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.items.IVariable;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.items.Slot;



public class PrototypeHyperLink implements IHyperlink {

	private String fPrototypeString;
	private IRegion fRegion;

	/**
	 * Creates a new Prototype hyperlink.
	 * @param region
	 * @param urlString
	 */
	public PrototypeHyperLink(IRegion region, String string) {
		fRegion= region;
		fPrototypeString= string;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkRegion()
	 */
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#open()
	 */
	public void open() {
		if (fPrototypeString != null) {
			IProject project = null;
			final IWorkbenchWindow w = LisaacPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPart part = w.getPartService().getActivePart();
			if (part instanceof LisaacEditor) {
				project = ((LisaacEditor)part).getProject();
			}
			if (part instanceof LipEditor) {
				return;
			}

			if (project != null) {// open 'prototype' in editor, select 'slot' at 'position'
				Prototype prototype = null;
				Slot slot = null;
				Position position = null;

				LisaacModel model = LisaacModel.getModel(project);

				if (LisaacScanner.isPrototypeIdentifier(fPrototypeString)) {
					// prototype hyperlink
					try {
						prototype = model.getPrototype(fPrototypeString);
					} catch (CoreException e) {
						return;
					}
				} else {

					// Slot Hyperlink
					try {
						prototype = LisaacModel.getCurrentPrototype();
						slot = prototype.getSlotFromKeyword(fPrototypeString, prototype.openParser(), fRegion.getOffset());

					} catch (CoreException e) {
						return;
					}
					if (slot != null) {
						// slot hyperlink
						prototype = slot.getPrototype();
						position = slot.getPosition();
					} else {

						// variable hyperlink
						IVariable variable = null;
						slot = prototype.getSlot(fRegion.getOffset());
						if (slot != null) {
							variable = slot.getVariable(fPrototypeString, fRegion.getOffset());
						}
						if (variable != null) {
							Position p = variable.getPosition();
							int len = fPrototypeString.length();
							if (p.length > 0) {
								len = p.length;
							}
							position = new Position(0, 0, p.offset-len, len);
						} else {
							// is 'text' a slot-call argument?
							slot = prototype.lookupSlot(fPrototypeString);
							if (slot != null) {
								prototype = slot.getPrototype();
								position = slot.getPosition();
							} else {
								prototype = null;
							}
						}					
					}
				}
				if (prototype != null) {
					final IProject p = project;
					final String filename = prototype.getFileName();
					final String prototypePath = prototype.getModel().getPathManager().getFullPath(prototype.getName());
					final Position selectPosition = position;

					part.getSite().getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IWorkbenchPage page = w.getActivePage();
							if (page == null) {
								return;
							}
														
							if (prototypePath != null) {
								IPath location = new Path(prototypePath);
								IPath projectLocation = p.getLocation();
								IFile file = null;

								if (projectLocation.isPrefixOf(location)) {
									// the file is inside the workspace
									location = location.removeFirstSegments(projectLocation.segmentCount());
									file = p.getFile(location);
								} else {
									// file is outside workspace : search in /lib
									IContainer lib = p.getFolder("lib");
									if (lib == null) {
										lib = p;
									}
									file = lib.getFile(new Path(filename));
								}
								
								try {
									IDE.openEditor(page, file);
									if (selectPosition != null) {
										IWorkbenchPart part = w.getPartService().getActivePart();
										if (part instanceof LisaacEditor) {
											((LisaacEditor)part).selectAndReveal(selectPosition.offset, selectPosition.length);
										}
									}
								} catch (CoreException e) {
									// TODO open editor error
									e.printStackTrace();
								}
							}					
						}
					});
				}
			}
			fPrototypeString = null;
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		return fPrototypeString;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		return null;
	}
}
