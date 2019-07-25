package org.lisaac.ldt.editors;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public class LisaacDamagerRepairer extends DefaultDamagerRepairer{

	public LisaacDamagerRepairer(ITokenScanner scanner) {
		super(scanner);
	}
	
	/*
	 * @see IPresentationRepairer#createPresentation(TextPresentation, ITypedRegion)
	 */
	public void createPresentation(TextPresentation presentation, ITypedRegion region) {

		if (fScanner == null) {
			// will be removed if deprecated constructor will be removed
			addRange(presentation, region.getOffset(), region.getLength(), fDefaultTextAttribute);
			return;
		}

		int lastStart= region.getOffset();
		int length= 0;
		boolean firstToken= true;
		IToken lastToken= Token.UNDEFINED;
		TextAttribute lastAttribute= getTokenTextAttribute(lastToken);

		fScanner.setRange(fDocument, lastStart, region.getLength());

		while (true) {
			IToken token= fScanner.nextToken();
			if (token.isEOF())
				break;

			// define text attribute for this token
			TextAttribute attribute= getTokenTextAttribute(token);
			if (lastAttribute != null && lastAttribute.equals(attribute)) {
				length += fScanner.getTokenLength();
				firstToken= false;
			} else {
				if (!firstToken)
					addRange(presentation, lastStart, length, lastAttribute);
				firstToken= false;
				lastToken= token;
				lastAttribute= attribute;
				lastStart= fScanner.getTokenOffset();
				length= fScanner.getTokenLength();
			}			
		}
		
		// process last token
		addRange(presentation, lastStart, length, lastAttribute);
	}
}
