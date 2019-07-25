package org.lisaac.ldt.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;


public class LisaacHyperLinkDetector extends AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {

		if (region == null || textViewer == null)
			return null;

		IDocument document= textViewer.getDocument();
		if (document == null)
			return null;

		try {
			int offset= region.getOffset();
			
			IRegion wordRegion = selectWord(document, offset);
			String prototypeString = document.get(wordRegion.getOffset(), wordRegion.getLength());
			
			return new IHyperlink[] {new PrototypeHyperLink(wordRegion, prototypeString)};

		} catch (BadLocationException e) {
			return null;
		}
	}

	protected IRegion selectWord(IDocument doc, int caretPos) throws BadLocationException {
		int startPos, endPos;

		int pos = caretPos;
		char c;

		while (pos >= 0) {
			c = doc.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			--pos;
		}
		startPos = pos+1;
		pos = caretPos;
		int length = doc.getLength();
		
		while (pos < length) {
			c = doc.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			++pos;
		}
		endPos = pos;
		return new Region(startPos, endPos - startPos);
	}
}
