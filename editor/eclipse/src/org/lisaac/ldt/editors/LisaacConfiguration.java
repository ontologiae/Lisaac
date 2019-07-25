package org.lisaac.ldt.editors;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.lisaac.ldt.LisaacMessages;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.preferences.PreferenceConstants;

/**
 * Manage the configuration of syntax coloration for lisaac documents.
 */
public class LisaacConfiguration extends SourceViewerConfiguration {
	private LisaacDoubleClickStrategy doubleClickStrategy;
	private LisaacScanner scanner;
	private ColorManager colorManager;

	private ContentAssistant contentAssistant = null;
	private ITextHover textHover = null;
	
	private AbstractLisaacEditor editor;
	
	public LisaacConfiguration(ColorManager colorManager, AbstractLisaacEditor editor) {
		this.colorManager = colorManager;
		this.editor = editor;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			LisaacPartitionScanner.LISAAC_COMMENT,
			LisaacPartitionScanner.LISAAC_STRING,
			LisaacPartitionScanner.LISAAC_CHARACTERS,
			LisaacPartitionScanner.LISAAC_EXTERNAL
		};
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new LisaacDoubleClickStrategy();
		return doubleClickStrategy;
	}

	/**
	 * Returns the content assistant ready to be used with the given source viewer.
	 * This implementation always returns <code>null</code>.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @return a content assistant or <code>null</code> if content assist should not be supported
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (contentAssistant == null) {	
			contentAssistant = new ContentAssistant();
			IContentAssistProcessor cap = new LisaacCompletionProcessor();
			contentAssistant.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
			//
			contentAssistant.setRepeatedInvocationMode(true);
			contentAssistant.setStatusLineVisible(true);
			contentAssistant.enableColoredLabels(true);
			contentAssistant.setStatusMessage(LisaacMessages.getString("LisaacConfiguration_0")); //$NON-NLS-1$
			//
			
			int delay;
			try {
				delay = LisaacPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_LISAAC_COMPLETION_DELAY);
			} catch (Exception e) {
				delay = 500;
			}
			contentAssistant.enableAutoActivation(true);
			contentAssistant.setAutoActivationDelay(delay);
			contentAssistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		}
        return contentAssistant;
	}

	
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if (textHover != null) {
			return textHover;
		}
		if (editor != null) {
			IProject project = editor.getProject();
	
			LisaacModel model = LisaacModel.getModel(project);
			textHover = new LisaacTextHover(model, editor.getFileName(), colorManager);
		}
		return textHover;
	}
	
	/**
     * @see SourceViewerConfiguration#getAutoEditStrategies(ISourceViewer, String)
     */
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
    	
        if (contentType.equals(LisaacPartitionScanner.LISAAC_COMMENT)) {
            return new IAutoEditStrategy[] { new DefaultIndentLineAutoEditStrategy() };

        } else if (contentType.equals(IDocument.DEFAULT_CONTENT_TYPE)) {
            return new IAutoEditStrategy[] { new LisaacAutoEditStrategy() };
            
        } else {
            return super.getAutoEditStrategies(sourceViewer, contentType);
        }
    }
	
    /**
     * @see SourceViewerConfiguration#getIndentPrefixes(ISourceViewer, String)
     */
    public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
        return new String[]{ new String("  "), new String() }; //$NON-NLS-1$
    }
    
    /**
	 * Returns the hyperlink detectors which be used to detect hyperlinks
	 * in the given source viewer. 
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @return an array with hyperlink detectors or <code>null</code> if no hyperlink support should be installed
	 * @since 3.1
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null)
			return null;

		return new IHyperlinkDetector[] { new LisaacHyperLinkDetector(), new URLHyperlinkDetector() };
	}
    
	protected LisaacScanner getLisaacScanner() {
		if (scanner == null) {
			scanner = new LisaacScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(ILisaacColor.DEFAULT))));
		}
		return scanner;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
			
		LisaacDamagerRepairer dr = new LisaacDamagerRepairer(getLisaacScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(colorManager.getToken(ILisaacColor.PREF_COMMENT, ILisaacColor.PREF_NORMAL));
		reconciler.setDamager(ndr, LisaacPartitionScanner.LISAAC_COMMENT);
		reconciler.setRepairer(ndr, LisaacPartitionScanner.LISAAC_COMMENT);
		 
		ndr = new NonRuleBasedDamagerRepairer(colorManager.getToken(ILisaacColor.PREF_STRING, ILisaacColor.STYLE_STRING));
		reconciler.setDamager(ndr, LisaacPartitionScanner.LISAAC_STRING);
		reconciler.setRepairer(ndr, LisaacPartitionScanner.LISAAC_STRING);
		
		ndr = new NonRuleBasedDamagerRepairer(colorManager.getToken(ILisaacColor.PREF_CHARACTER, ILisaacColor.PREF_LOCAL_SLOT));
		reconciler.setDamager(ndr, LisaacPartitionScanner.LISAAC_CHARACTERS);
		reconciler.setRepairer(ndr, LisaacPartitionScanner.LISAAC_CHARACTERS);
		
		ndr = new NonRuleBasedDamagerRepairer(colorManager.getToken2(ILisaacColor.PREF_EXTERNAL, ILisaacColor.PREF_LOCAL_SLOT));
		reconciler.setDamager(ndr, LisaacPartitionScanner.LISAAC_EXTERNAL);
		reconciler.setRepairer(ndr, LisaacPartitionScanner.LISAAC_EXTERNAL);
		
		return reconciler;
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getQuickAssistAssistant(org.eclipse.jface.text.source.ISourceViewer)
     */
  /*  public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
        // create a content assistant:
    	
    	 QuickAssistAssistant assistant = new QuickAssistAssistant();
    	 assistant.setQuickAssistProcessor(new LisaacQuickAssistProcessor());
    	 assistant.setStatusLineVisible(true);
    	 assistant.setStatusMessage("Lisaac QuickFix");
    	 
    	 
        assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

        
        return assistant;
    }
*/
	/*
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer){
        QuickAssistAssistant qaa = new QuickAssistAssistant();
        qaa.setQuickAssistProcessor(new LisaacQuickAssistProcessor());
        qaa.setStatusLineVisible(true);
        qaa.setStatusMessage("Lisaac QuickFix");
        return qaa;
    }*/
}