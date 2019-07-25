package org.lisaac.ldt.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.preferences.PreferenceConstants;
import org.lisaac.ldt.views.LisaacOutlineView;

/**
 * Main class for the Lisaac editor
 * @author Damien Bouvarel
 */
public class AbstractLisaacEditor extends TextEditor {

	private ColorManager colorManager;

	/** the outline view. */
	private LisaacOutlineView outlineView;

	private ProjectionSupport projectionSupport;
	
	
	public AbstractLisaacEditor() {
		super();
		colorManager = new ColorManager(LisaacPlugin.getDefault().getPreferenceStore());

		setSourceViewerConfiguration(new LisaacConfiguration(colorManager,this));
		setDocumentProvider(new LisaacDocumentProvider());

		IPreferenceStore store = LisaacPlugin.getDefault().getPreferenceStore();
		
		// wide caret
		store.setDefault(PREFERENCE_USE_CUSTOM_CARETS, true);
		store.setDefault(PREFERENCE_WIDE_CARET, true);
		
		store = getChainedPreferenceStore();
		setPreferenceStore(store);
	}
	
	 /**
     * Returns the preference store to be used by this editor.
     * @return the preference store to be used by this editor
     */
    private IPreferenceStore getChainedPreferenceStore() {
        List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>();

        stores.add(LisaacPlugin.getDefault().getPreferenceStore());
        stores.add(EditorsUI.getPreferenceStore());

        return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[0]));
    }

    public void createPartControl(Composite parent) {
       	super.createPartControl(parent);
    	
		ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
       	
        projectionSupport = new ProjectionSupport(viewer,getAnnotationAccess(),getSharedColors());
		projectionSupport.install();
		
		//turn projection mode on
		viewer.doOperation(ProjectionViewer.TOGGLE);		      
		
    	annotationModel = viewer.getProjectionAnnotationModel();  
   
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    protected ISourceViewer createSourceViewer(Composite parent,
            IVerticalRuler ruler, int styles) {
        ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);

    	// ensure decoration support has been created and configured.
    	getSourceViewerDecorationSupport(viewer);
    		
    	return viewer;
    }
    
    private Annotation[] oldAnnotations;
	private ProjectionAnnotationModel annotationModel;
	
	public void updateFoldingStructure(ArrayList positions) {
		
		if (annotationModel == null) {
			return;
		}
		
		Annotation[] annotations = new Annotation[positions.size()];
		
		//this will hold the new annotations along
		//with their corresponding positions
		HashMap newAnnotations = new HashMap();
		
		for(int i =0;i<positions.size();i++)
		{
			ProjectionAnnotation annotation = new ProjectionAnnotation();	
			newAnnotations.put(annotation,positions.get(i));		
			annotations[i] = annotation;
		}
		annotationModel.modifyAnnotations(oldAnnotations,newAnnotations,null);
		oldAnnotations=annotations;
	}
	
	public void removeFoldingStructure() {
		annotationModel.removeAllAnnotations();
	}

	public IDocument getDocument() {
		if (getDocumentProvider() == null) {
			return null;
		}
		return getDocumentProvider().getDocument(getEditorInput());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public static class MyResources extends ListResourceBundle {
		public Object[][] getContents() {
			return contents;
		}

		static final Object[][] contents = { { "CorrectionAssist", "CorrectionAssist" }, { "ContentAssistProposal", "ContentAssistProposal" }, { "TemplateProposals", "TemplateProposals" }, };
	}
		
	protected void createActions() {
		super.createActions();

		MyResources ressources = new MyResources();

		Action action = new ContentAssistAction(ressources, "ContentAssistProposal.", this); //$NON-NLS-1$
		String id = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		action.setActionDefinitionId(id);
		setAction("ContentAssistProposal", action);  //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$
	}

	public ISourceViewer getViewer() {
		return getSourceViewer();
	}

	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {	
		colorManager.handlePreferenceStoreChanged(event);
		super.handlePreferenceStoreChanged(event);
		
		String prop = event.getProperty();
		if (prop.equals(PreferenceConstants.P_LISAAC_COMPLETION_DELAY)) {
			IContentAssistant assistant = getSourceViewerConfiguration().getContentAssistant(getSourceViewer());
			
			Integer delay;
			try {
				delay = Integer.valueOf(((String)event.getNewValue()));
			} catch (Exception e) {
				delay = 500;
			}
			((ContentAssistant) assistant).setAutoActivationDelay(delay);
		}
	}

	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return super.affectsTextPresentation(event)
		|| colorManager.affectsTextPresentation(event);
	}


	/**
	 * Redraw whole text presentation of the editor
	 */
	public void refreshPresentation() {
		IDocument document = getDocument();
		if (document != null) {
			refreshPresentation(0, document.getLength());
		}
	}
	/**
	 * Redraw region of text presentation of the editor
	 * @param offset redraw region offset
	 * @param length redraw region length
	 */
	public void refreshPresentation(int offset, int length) {
		ISourceViewer viewer = getSourceViewer();
		if (viewer instanceof ITextViewerExtension2) {
			ITextViewerExtension2 ext = (ITextViewerExtension2) viewer;
			ext.invalidateTextPresentation(offset, length);
		}
	}

	/**
	 * @return the project for the file that's being edited (or null if not available)
	 */
	public IProject getProject() {
		IEditorInput editorInput = this.getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			IFile file = (IFile) ((FileEditorInput) editorInput).getAdapter(IFile.class);
			return file.getProject();
		}
		return null;
	}

	/**
	 * @return the file name for the file that's being edited (or null if not available)
	 */
	public String getFileName() {
		IEditorInput editorInput = this.getEditorInput();
		if (editorInput instanceof FileEditorInput) {
			IFile file = (IFile) ((FileEditorInput) editorInput).getAdapter(IFile.class);
			return file.getName();
		}
		return null;
	}

	/**
	 * @see AbstractTextEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {

		if (IContentOutlinePage.class.equals(required)) {
			if (outlineView == null) {
				outlineView = new LisaacOutlineView(getDocumentProvider(), this);
			}
			return outlineView;
		} else {	
			return super.getAdapter(required);
		}
	}
}
