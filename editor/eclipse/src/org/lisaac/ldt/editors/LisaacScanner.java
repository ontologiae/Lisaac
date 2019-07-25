package org.lisaac.ldt.editors;

import org.eclipse.jface.text.rules.*;
import org.lisaac.ldt.model.ILisaacModel;

class LisaacWhitespaceDetector implements IWhitespaceDetector {
	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
		// return c.isWhitespace();
	}
}

class LisaacPrototypeDetector implements IWordDetector {
	public boolean isWordPart(char c) {
		return (Character.isLetter(c) && Character.isUpperCase(c))
		|| Character.isDigit(c) || c == '_';
	}

	public boolean isWordStart(char c) {
		return (Character.isLetter(c) && Character.isUpperCase(c)) || c == '_';
	}
}

class LisaacNumberDetector implements IWordDetector {
	public boolean isWordPart(char c) {
		return Character.isLetterOrDigit(c);
	}

	public boolean isWordStart(char c) {
		return Character.isDigit(c);
	}
}

class LisaacKeywordDetector implements IWordDetector {
	public boolean isWordPart(char c) {
		return Character.isLetter(c) && Character.isLowerCase(c);
	}

	public boolean isWordStart(char c) {
		return Character.isLetter(c) && Character.isUpperCase(c);
	}
}

class LisaacWordDetector implements IWordDetector {
	public boolean isWordPart(char c) {
		return (Character.isLetter(c) && Character.isLowerCase(c))
		|| Character.isDigit(c) || c == '_';
	}

	public boolean isWordStart(char c) {
		return (Character.isLetter(c) && Character.isLowerCase(c)) || c == '_';
	}
}

/**
 * Lisaac code scanner.<br>
 * Scan a range of a document into tokens, the scanner is used by the repairer
 * to create the text presentation.
 */
public class LisaacScanner extends RuleBasedScanner {

	// Lisaac tokens
	private IToken stringToken;
	private IToken characterToken;
	private IToken numberToken;
	private IToken prototypeToken;
	private IToken prototypeStyleToken;
	private IToken keywordToken;
	private IToken localVariableToken;
	private IToken operatorToken;
	private IToken externalToken;
	private IToken undefinedToken;

	/**
	 * Creates a new Lisaac scanner.
	 */
	public LisaacScanner(ColorManager manager) {
		/*
		 * Create lisaac tokens.
		 */

		stringToken = manager.getToken(ILisaacColor.PREF_STRING, ILisaacColor.STYLE_STRING);
		characterToken = manager.getToken(ILisaacColor.PREF_CHARACTER, ILisaacColor.STYLE_CHARACTER);
		numberToken = manager.getToken(ILisaacColor.PREF_NUMBER, ILisaacColor.STYLE_NUMBER);
		prototypeToken = manager.getToken(ILisaacColor.PREF_PROTOTYPE, ILisaacColor.STYLE_PROTOTYPE);
		prototypeStyleToken = manager.getToken(ILisaacColor.PREF_PROTOTYPE_STYLE, ILisaacColor.STYLE_PROTOTYPE_STYLE);
		keywordToken = manager.getToken(ILisaacColor.PREF_KEYWORD, ILisaacColor.STYLE_KEYWORD);
		localVariableToken = manager.getToken(ILisaacColor.PREF_LOCAL_SLOT, ILisaacColor.STYLE_LOCAL_SLOT);
		operatorToken = manager.getToken(ILisaacColor.PREF_OPERATOR, ILisaacColor.STYLE_OPERATOR);
		externalToken = manager.getToken2(ILisaacColor.PREF_EXTERNAL, ILisaacColor.PREF_LOCAL_SLOT);
		
		undefinedToken = manager.getToken(ILisaacColor.PREF_SLOT, ILisaacColor.STYLE_SLOT);
			//new Token(getAttribute(ILisaacColor.UNDEFINED));

		/*
		 * Create basic lisaac rules.
		 */
		IRule[] rules = new IRule[5];

		// Add rule for processing strings 
	//	rules[0] = new LisaacPatternRule("\"", "\"", stringToken, '\\', false, true, true);// double
	//	rules[0] = new PatternRule("'", "'", characterToken, '\\', true, true, true);// simple
  
		// Add generic whitespace rule.
		rules[0] = new WhitespaceRule(new LisaacWhitespaceDetector());

		// keywords rule
		WordRule wr = new WordRule(new LisaacKeywordDetector(), Token.UNDEFINED);
		String[] keywords = ILisaacModel.keywords;
		for (int i = 0; i < keywords.length; i++) {
			wr.addWord(keywords[i], keywordToken);
		}
		rules[1] = wr;

		// prototype rule
		rules[2] = new WordRule(new LisaacPrototypeDetector(), prototypeToken);

		// simple lisaac word rule
		//rules[5] = new WordRule(new LisaacWordDetector(), undefinedToken);
		rules[3] = new LisaacWordRule(new LisaacWordDetector(), undefinedToken, localVariableToken);

		// lisaac external
		//rules[5] = new SingleLineRule("`", "`", externalToken, '\0', true);// back
		// quotes

		// number rule
		rules[4] = new WordRule(new LisaacNumberDetector(), numberToken);

		// add basic rules
		setRules(rules);
	}

	/*
	 * @see ITokenScanner#nextToken()
	 */
	public IToken nextToken() {

		fTokenOffset = fOffset;
		fColumn = UNDEFINED;

		//
		// Lisaac scan
		//

		// start processing basic rules first
		if (fRules != null) {
			for (int i = 0; i < fRules.length; i++) {
				IToken token = (fRules[i].evaluate(this));
				if (!token.isUndefined())
					return token;
			}
		}

		// none of the basic rules fired
		char c = (char) read();
		if (c != ICharacterScanner.EOF) {
			if (c == '+' || c == '-') {
				if (getColumn() == 3) {// slot style
					return prototypeStyleToken;
				}
				if (detectLocalSlot()) { // local slot style
					return prototypeStyleToken;
				}
				return operatorToken;// arithmetic + or -
			}
			if (c == '<') {// list affect
				c = (char) read();
				if (c == '-') {
					return fDefaultReturnToken;
				} else {
					unread();
					return operatorToken;
				}
			}
			if (c == ':') {// slot affect
				c = (char) read();
				if (c != '=') {
					unread();
					if (detectBlockType()) {
						return prototypeToken;
					}
				}
				return fDefaultReturnToken;
			}
			if (c == '?') {// ?= affect
				c = (char) read();
				if (c == '=') {
					return fDefaultReturnToken;
				}
				unread();
				return operatorToken;
			}
			if (c == '*' || c == '/' || c == '&' || c == '$' || c == '|'
				|| c == '>' || c == '=' || c == '!' || c == '~' || c == '@'
					|| c == '#' || c == '^') {
				return operatorToken;
			}
			if (c == '{' || c == '}') {
				return operatorToken;
			}
		}

		unread();
		//
		// End of Lisaac scan
		//

		if (read() == EOF)
			return Token.EOF;
		return fDefaultReturnToken;
	}

	private boolean readIndentifier() {
		char c;
		int i = 0;

		do {
			c = (char) read();
			i++;
		} while (c != ICharacterScanner.EOF
				&& (Character.isLetterOrDigit(c) || c == '_'));
		unread();

		return i > 1;
	}

	private void readSpace() {
		char c;

		do {
			c = (char) read();
		} while (c != ICharacterScanner.EOF && Character.isWhitespace(c));
		unread();
	}

	private boolean detectLocalSlot() {
		int oldOffset = fOffset;
		boolean result = false;

		readSpace();
		while (readIndentifier()) {
			readSpace();
			char c = (char) read();
			if (c == ICharacterScanner.EOF)
				break;

			if (c == ':') {
				result = true;
				break;
			}
			if (c != ',')
				break;

			readSpace();
		}
		fOffset = oldOffset;// unread all
		fColumn = UNDEFINED;
		return result;
	}

	private boolean detectBlockType() {
		int oldOffset = fOffset;
		boolean result = false;

		readSpace();
		char c = (char) read();
		if (c != ICharacterScanner.EOF && c == '{') {
			int level = 1;
			do {
				c = (char) read();
				if (c != ICharacterScanner.EOF) {
					if (c == '{') {
						level++;
					} else if (c == '}') {
						level--;

					} else if (c == '\n' || c == '\r') {
						break; // no multiline type

					} else if (((int)c) == 65535) {
						break;  // bug!
					}
				}
			} while (c != ICharacterScanner.EOF && level != 0);

			if (level == 0) {
				result = true;
			}
		} 
		if (! result) {
			fOffset = oldOffset;// unread all
			fColumn = UNDEFINED;
		}
		return result;
	}

	public int getOffset() {
		return fOffset;
	}

	public static boolean isPrototypeIdentifier(String word) {
		return detectKeyword(word, new LisaacPrototypeDetector());
	}
	
	public static boolean isIdentifier(String word) {
		return detectKeyword(word, new LisaacWordDetector());
	}

	public static boolean isKeywordIdentifier(String word) {
		return detectKeyword(word, new LisaacKeywordDetector());
	}
	
	private static boolean detectKeyword(String word, IWordDetector detector) {
		int i = 0;
		char c;
		boolean b = true;
		
		if (word.length() > 0 && detector.isWordStart(word.charAt(0))) {
			if (word.length() == 1) {
				return true;
			}
			i = 1;
			do {
				c = word.charAt(i);
				b = detector.isWordPart(c);
				i++;
			} while (i < word.length() && b);
			if (! b) {
				return false;
			}
		}
		return i == word.length();
	}
}
