package org.lisaac.ldt.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.preferences.PreferenceConstants;


public class LisaacAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {

	/**
	 * @see DefaultIndentLineAutoEditStrategy#customizeDocumentCommand(IDocument, DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		editDocumentCommand(d, c);
	}

	/**
	 * Customizes the given document command to edit the given document. 
	 * @param document the document
	 * @param command the command
	 * @see DefaultIndentLineAutoEditStrategy#customizeDocumentCommand(IDocument, DocumentCommand)
	 */
	protected void editDocumentCommand(IDocument document, DocumentCommand command) {
		String textCommand = command.text;

		if (textCommand != null) {
			
			boolean enableIndent = LisaacPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_LISAAC_INDENT);
			if (! enableIndent) {
				return;
			}
			String[] lineDelimiters = document.getLegalLineDelimiters();
			int endOfLineIndex = TextUtilities.endsWith(lineDelimiters, textCommand);

			if (endOfLineIndex > -1) {
				// this is an end of line
				indentOnNewLine(document, command);
			} else if (textCommand.equals("\t")) {
				// this is a tab
				indentOnTab(document, command);
			} else {
				// this is another character or string
				indentOnSpecificChar(document, command);
			}
		}
	}

	/**
	 * Indent One line.
	 * @param indentLine line to be indented
	 * @param document 
	 * @param command
	 * @return last indentation for the next line
	 */
	private static void doIndentLine(int indentLine, IDocument document, DocumentCommand command) {
		try {
			//
			// find last line indent
			//

			int lastIndent = getIndentWithPreviousLine(indentLine, document);

			//
			// current line indent
			//
			IRegion currentLineInfo = document.getLineInformation(indentLine);

			int lineStart = currentLineInfo.getOffset();
			int lineEnd = currentLineInfo.getOffset() + currentLineInfo.getLength();

			IRegion originalBlankRegion = getBlankAfterOffset(document, lineStart);
			int currentIndent = originalBlankRegion.getLength();

			// special case
			if (lineEnd - originalBlankRegion.getOffset()+currentIndent > 8) {
				String instr = document.get(originalBlankRegion.getOffset()+currentIndent,8);
				if (instr.startsWith("Section ")) {
					lastIndent = 2;

					// insertion in current line
					if (command != null) {
						command.text = "";
						command.offset = lineStart;
						command.length = currentIndent;
					} else {
						document.replace(lineStart, currentIndent, "");
					}
					return;
				}
			}
			int i = lineEnd-1;
			int indent2 = 0;
			while (i >= lineStart) {
				char c = document.getChar(i);
				switch (c) {
				case '{':
				case '(':
				case '[':
					if (indent2 != 0) {
						indent2 -= 2;
					}
					break;
				case '}':
				case ')':
				case ']':
					indent2 += 2;
					break;
				case '\"'://  string " "
					do {
						i--;
						if (i >= lineStart) {
							c = document.getChar(i);
						}
					} while (i >= lineStart && c != '\"');
					break;
				case '\'':// string ' '
					do {
						i--;
						if (i >= lineStart) {
							c = document.getChar(i);
						}
					} while (i >= lineStart && c != '\'');
					break;
				}
				i--;
			}
			//
			// insertion in current line
			//
			lastIndent -= indent2;

			if (command != null) {
				command.text = createString(lastIndent);
				command.offset = lineStart;
				command.length = currentIndent;
			} else {
				document.replace(lineStart, currentIndent, createString(lastIndent));
			}

		} catch (BadLocationException excp) {
			// stop work
		}
	}

	/**
	 * Get line indentation using previous line.
	 */
	private static int getIndentWithPreviousLine(int line, IDocument document) {
		int result = 0;
		try {
			//
			// find last line indent
			//
			while (line > 0) {
				line--;
				IRegion lineRegion = document.getLineInformation(line);

				int lineStart = lineRegion.getOffset();
				int lineEnd = lineRegion.getOffset() + lineRegion.getLength();

				IRegion originalBlankRegion = getBlankAfterOffset(document, document.getLineOffset(line));
				result = originalBlankRegion.getLength();

				// special case
				if (lineEnd - originalBlankRegion.getOffset()+result > 8) {
					String instr = document.get(originalBlankRegion.getOffset()+result,8);
					if (instr.startsWith("Section ")) {
						result = 2;
						break;
					}
				}
				int i = lineStart;
				int deltaIndent = 0;
				while (i < lineEnd) {
					char c = document.getChar(i);
					switch (c) {
					case '{':
					case '(':
					case '[':
						deltaIndent += 2;
						break;
					case '}':
					case ')':
					case ']':
						if (deltaIndent != 0) {
							deltaIndent -= 2;
						}
						break;
					case '\"'://  string " "
						do {
							i++;
							if (i < lineEnd) {
								c = document.getChar(i);
							}
						} while (i < lineEnd && c != '\"');
						break;
					case '\'':// string ' '
						do {
							i++;
							if (i < lineEnd) {
								c = document.getChar(i);
							}
						} while (i < lineEnd && c != '\'');
						break;
					}
					i++;
				}
				result += deltaIndent;

				if (getBlankEnd(document,lineStart) != lineEnd) {
					// not empty line
					break;
				} 
			}
		} catch (BadLocationException excp) {
			// stop work
		}
		return result;
	}

	/**
	 * Get the blank region of given line after offset
	 */
	private static int getBlankEnd(IDocument document, int offset) throws BadLocationException {
		IRegion lineRegion = document.getLineInformationOfOffset(offset);
		int blankEnd = offset;
		int maxBlankEnd = lineRegion.getOffset() + lineRegion.getLength();

		while (blankEnd < maxBlankEnd) {
			char c = document.getChar(blankEnd);
			if (c != ' ' && c != '\t') {
				break;
			}
			blankEnd++;
		}
		return blankEnd;
	}

	/**
	 * Customizes the given command to edit the given document when a newline is pressed.
	 * @param document the document
	 * @param command the command
	 */
	protected void indentOnNewLine(IDocument document, DocumentCommand command) {
		try {
			int p = (command.offset == document.getLength() ? command.offset  - 1 : command.offset);
			int line = document.getLineOfOffset(p);

			// indent previous line
			doIndentLine(line, document, command);

			// get indent for new line
			int indent = getIndentWithPreviousLine(line+1, document);

			//
			// indent new line
			//
			//IRegion info = document.getLineInformation(line);
			command.addCommand(p/*info.getOffset() + info.getLength()*/, 0, "\n"+createString(indent), null);
			command.shiftsCaret = true;
			command.caretOffset = p /*info.getOffset() + info.getLength()*/;

		} catch (BadLocationException e) {
			// stop work
		}
	}

	/**
	 * Get the blank region of given line after offset
	 */
	private static IRegion getBlankAfterOffset(IDocument document, int offset) throws BadLocationException {
		IRegion lineRegion = document.getLineInformationOfOffset(offset);
		int blankEnd = offset;
		int maxBlankEnd = lineRegion.getOffset() + lineRegion.getLength();

		while (blankEnd < maxBlankEnd) {
			char c = document.getChar(blankEnd);
			if (c != ' ' && c != '\t') {
				break;
			}
			blankEnd++;
		}
		return new Region(offset, blankEnd - offset);
	}

	/**
	 * Returns a blank string of the given length.
	 * @param length the length of the string to create
	 * @return a blank string of the given length
	 */
	public static String createString(int length) {
		StringBuffer buffer = new StringBuffer(length);

		for (int index = 0 ; index < length ; index++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

	/**
	 * Customizes the given command to edit the given document when a tabulation is pressed.
	 * @param document the document
	 * @param command the command
	 */
	protected void indentOnTab(IDocument document, DocumentCommand command) {

		//fullIndentDocument(document);

		try {
			int p = (command.offset == document.getLength() ? command.offset  - 1 : command.offset);
			int line = document.getLineOfOffset(p);

			doIndentLine(line, document, command);

		} catch (BadLocationException excp) {
			// stop work
		}
	}

	/**
	 * Customizes the given command to edit the given document when a specific character is pressed.
	 * @param document the document
	 * @param command the command
	 */
	protected void indentOnSpecificChar(IDocument document, DocumentCommand command) {
		// TODO code templates!!!
	}

	/** 
	 * Indent correctly the whole document
	 * @param document the document
	 */
	public static void fullIndentDocument(IDocument document) {
		int line = 0;
		int maxLine = document.getNumberOfLines();

		while (line < maxLine) {
			doIndentLine(line, document, null);
			line++;
		}
	}
	
	public static void indentLine(int line, IDocument document) {
		doIndentLine(line, document, null);
	}
}
