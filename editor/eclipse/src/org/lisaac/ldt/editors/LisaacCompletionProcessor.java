package org.lisaac.ldt.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.model.LisaacCompletionParser;
import org.lisaac.ldt.model.LisaacParser;
import org.lisaac.ldt.preferences.PreferenceConstants;
import org.lisaac.ldt.templates.LisaacTemplateProcessor;


public class LisaacCompletionProcessor implements IContentAssistProcessor {

	private final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];
	private final char[] PROPOSAL_ACTIVATION_CHARS = new char[] { '.' };
	private ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];

	private LisaacTemplateProcessor templates;


	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		try {
			boolean enableCompletion = LisaacPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_LISAAC_COMPLETION);
			if (! enableCompletion) {
				return null;
			}

			IDocument document = viewer.getDocument();
			ArrayList<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

			//
			computeLisaacCompletion(document, offset, result);
			if (result.size() > 1) {
				Collections.sort(result, new Comparator<ICompletionProposal>() {
					public int compare(ICompletionProposal o1, ICompletionProposal o2) {
						return o1.getDisplayString().compareTo(o2.getDisplayString());
					}
				});
			}
			//

			String prefix= extractPrefix(document, offset);

			if (prefix != null && prefix.length() > 0) {
				templates = new LisaacTemplateProcessor();
				ICompletionProposal[] props = templates.computeCompletionProposals(viewer, offset);
				for (int t=0; t<props.length; t++) {
					TemplateProposal tp = (TemplateProposal) props[t];
					if (tp.getDisplayString().startsWith(prefix)) {
						result.add(props[t]);
					}
				}
			}

			return (ICompletionProposal[]) result.toArray(new ICompletionProposal[result.size()]);
		} catch (Exception e) {
			// ... log the exception ...
			return NO_COMPLETIONS;
		}

	}

	private void computeLisaacCompletion(IDocument document, int baseOffset, 
			ArrayList<ICompletionProposal> proposals) {
		int bracketLevel=0;
		//
		// Rewind to '(' '{' ';' '[' ':' '<-'
		//
		try {
			int pos = baseOffset-1;
			while (pos > 0) {
				char c = document.getChar(pos);
				if (c == ';' || c == ':') {
					break;
				}
				if (c == '-' && pos-1 > 0 && document.getChar(pos-1) == '<') {
					break;
				}
				if (LisaacParser.isOperatorSymbol(c)) {
					break;
				}
				if (c == '(' || c == '{' || c == '[') {
					if (bracketLevel == 0) {
						break;
					}
					bracketLevel--;
				}
				if (c == ')' || c == '}' || c == ']') {
					bracketLevel++;
				}
				pos--;
			}
			if (pos > 0) {
				//
				// compute lisaac expression type
				//
				String contents = document.get(pos+1, baseOffset-1 - pos);

				LisaacCompletionParser parser = new LisaacCompletionParser(contents, null);
				parser.parseCompletions(pos+1, baseOffset, proposals);
			}
		} catch (BadLocationException e) {
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String extractPrefix(IDocument document, int offset) {
		int i= offset;
		if (i > document.getLength())
			return ""; //$NON-NLS-1$

		try {
			while (i > 0) {
				char ch= document.getChar(i - 1);
				if (!Character.isJavaIdentifierPart(ch))
					break;
				i--;
			}

			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return NO_CONTEXTS;
	}


	public char[] getCompletionProposalAutoActivationCharacters() {
		return PROPOSAL_ACTIVATION_CHARS;
	}


	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return "Lisaac Completion error";
	}
}
