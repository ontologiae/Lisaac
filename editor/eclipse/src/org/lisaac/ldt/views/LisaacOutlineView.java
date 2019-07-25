package org.lisaac.ldt.views;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.editors.LisaacEditor;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.outline.OutlineContentProvider;
import org.lisaac.ldt.outline.OutlineImages;
import org.lisaac.ldt.outline.OutlineItem;
import org.lisaac.ldt.outline.OutlineLabelProvider;

public class LisaacOutlineView extends ContentOutlinePage implements IDocumentListener {

	/** the delay before the outline view is updated. */
	private static final long UPDATE_DELAY = 1500; 

	/** the document provider. */
	private IDocumentProvider documentProvider;

	/** the text editor. */
	private AbstractDecoratedTextEditor textEditor;

	/** the current document. */
	private IDocument document;

	/** the update timer which manages update task scheduling. */
	private Timer updateTimer;


	public LisaacOutlineView(IDocumentProvider documentProvider, AbstractDecoratedTextEditor textEditor) {
		super();
		this.documentProvider = documentProvider;
		this.textEditor = textEditor;
		createTimer();
	}

	/**
	 * @see ContentOutlinePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new OutlineContentProvider());
		viewer.setLabelProvider(new OutlineLabelProvider());
		viewer.addSelectionChangedListener(this);
		createActions();

		document = getDocument();
		if (document != null) {
			document.addDocumentListener(this);
		}
		update();
	}

	private void createActions() {

		//---- Sort by name
		Action sortByName = new Action("Sort by name", IAction.AS_CHECK_BOX) {

			ViewerSorter sortByNameSorter;

			public void setAlphaSort(boolean doSort) {
				if (sortByNameSorter == null) {
					sortByNameSorter = new ViewerSorter() {
						public int compare(Viewer viewer, Object e1, Object e2) {
							return ((Comparable) e1).compareTo(e2);
						}
					};
				}
				getTreeViewer().setSorter(doSort ? sortByNameSorter : null);
			}
			
			public void run() {
				setAlphaSort(isChecked());
			}
		};
		sortByName.run();
		
		//---- Sort by name
		Action sortBySection= new Action("Sort by Section", IAction.AS_CHECK_BOX) {
			public void run() {
				OutlineItem.showSections = isChecked();
				getTreeViewer().refresh();
				getTreeViewer().expandAll();
			}
		};
		sortBySection.setChecked(true);
		
		try {
			sortByName.setImageDescriptor(LisaacPlugin.getImageDescriptor(OutlineImages.SORT_ALPHA));
			sortBySection.setImageDescriptor(LisaacPlugin.getImageDescriptor(OutlineImages.SORT_SECTION));
		} catch (Exception e) {
		}

		// Add actions to the toolbar
		IActionBars actionBars = getSite().getActionBars();
		IToolBarManager toolbarManager = actionBars.getToolBarManager();
		toolbarManager.add(sortByName);
		toolbarManager.add(sortBySection);
	}

	/**
	 * Returns the document attached to this view.
	 * @return the document attached to this view
	 */
	public IDocument getDocument() {
		if (document == null) {
			document = documentProvider.getDocument(textEditor.getEditorInput());
		}
		return document;
	}

	/**
	 * Fired when Outline selection changed
	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection abstractSelection = event.getSelection();
		IDocument document = getDocument();

		if (document != null) {   
			if ((abstractSelection != null) && (abstractSelection instanceof TreeSelection)) {
				TreeSelection selection = (TreeSelection) abstractSelection;
				Object selectedElement = selection.getFirstElement();

				if ((selectedElement != null) && (selectedElement instanceof OutlineItem)) {
					OutlineItem item = (OutlineItem) selectedElement;
					// select current outline item in editor
					textEditor.selectAndReveal(item.startOffset(), item.length());
				}
			}
		}
	}

	/**
	 * Sends the input to the tree viewer.
	 * @param input the input
	 */
	public void setInput(Object input) {
		if (!getTreeViewer().getControl().isDisposed()) {
			getTreeViewer().setInput(input);
			getTreeViewer().expandAll();
		}
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
	}
	/**
	 * Notify document modifications.
	 */
	public void documentChanged(DocumentEvent event) {
		document = event.getDocument();
		update();
	}

	/**
	 * Get the outline data.
	 * 
	 * @param project Current project
	 * @param filename File to outline
	 * @return List of outline items
	 * @throws CoreException 
	 */
	public List<OutlineItem> getSourceOutline(IProject project, String filename) throws CoreException {
		LisaacModel model = LisaacModel.getModel(project);
		if (model != null) {
			Prototype prototype = model.getPrototype(LisaacModel.extractPrototypeName(filename));
			
			if (prototype != null) {
				return prototype.getOutlineItems();
			}
		}
		return null;
	}

	private void createTimer() {
		updateTimer = new Timer("org.lisaac.ldt.outlinetimer");
	}

	/**
	 * Updates the outline content view.
	 */
	public void update() {
		updateTimer.cancel();
		updateTimer.purge();
		createTimer();

		OutlineUpdateTask updateTask = new OutlineUpdateTask();
		updateTimer.schedule(updateTask, UPDATE_DELAY);
	}

	/**
	 * This class is in charge of updating the outline.
	 */
	class OutlineUpdateTask extends TimerTask {
		public OutlineUpdateTask() {
			super();
		}

		/**
		 * Updates the outline content view.
		 * @see TimerTask#run()
		 */
		public void run() {
			final IDocument document = getDocument();
			Display display = PlatformUI.getWorkbench().getDisplay();

			if (document != null && (textEditor instanceof LisaacEditor)) {
				IProject project = ((LisaacEditor) textEditor).getProject();
				String filename = ((LisaacEditor) textEditor).getFileName();
				
				try {
					final List<OutlineItem> items = getSourceOutline(project, filename);

					display.asyncExec(new Runnable() {
						public void run() {
							if (items != null) {
								setInput(items.toArray(new OutlineItem[items.size()]));
								//
								((LisaacEditor) textEditor).refreshPresentation();
								//
							} 
						}
					});
				} catch (CoreException e)  {
				}
			}
		}
	}
}
