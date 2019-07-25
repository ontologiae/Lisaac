package org.lisaac.ldt.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.items.Slot;

public class LisaacWordRule extends WordRule {

	private IToken localVariableToken;
	
	private StringBuffer fBuffer= new StringBuffer();
	
	
	public LisaacWordRule(IWordDetector detector, IToken defaultToken, IToken localVariableToken) {
		super(detector, defaultToken);
		this.localVariableToken = localVariableToken;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		IToken result = doEvaluate(scanner);
		
		if (result == fDefaultToken) {	
			//
			Prototype prototype = null;
			try {
				prototype = LisaacModel.getCurrentPrototype();
			} catch (CoreException e1) {
			}
			if (prototype == null) {
				return result;
			}
			//
			int offset = ((LisaacScanner) scanner).getOffset();
			String word = fBuffer.toString();
			
			try {
				Slot slot = prototype.getSlotFromKeyword(word, prototype.openParser(), offset-word.length());
				if (slot != null) {
					return result;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Slot slot = prototype.getSlot(offset);
			if (slot != null) {
				
				// is current word a slot argument?
				if (slot.hasArgument(word)) {
					return localVariableToken;
				}
				// is current word a slot variable?
				if (slot.hasVariableDefinition(word, offset)) {
					return localVariableToken;
				}
			}
		}
		return result;
	}
	

	public IToken doEvaluate(ICharacterScanner scanner) {
		int c= scanner.read();
		if (c != ICharacterScanner.EOF && fDetector.isWordStart((char) c)) {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {

				fBuffer.setLength(0);
				do {
					fBuffer.append((char) c);
					c= scanner.read();
				} while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
				scanner.unread();

				String buffer= fBuffer.toString();
				IToken token= (IToken)fWords.get(buffer);
								
				if (token != null)
					return token;

				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);

				return fDefaultToken;
			}
		}
		scanner.unread();
		return Token.UNDEFINED;
	}
}
