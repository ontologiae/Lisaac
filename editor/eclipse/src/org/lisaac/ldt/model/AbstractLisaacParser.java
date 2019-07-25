package org.lisaac.ldt.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.lisaac.ldt.builder.ILisaacErrorHandler;


public class AbstractLisaacParser {
	
	protected ILisaacErrorHandler reporter;

	protected ILisaacModel model;
	
	protected String source;

	protected int position;

	protected int pos_cur, pos_line, pos_col;
	protected int begin_position;

	protected String string_tmp="";

	
	public Position getPosition() {
		return getPosition(0);
	}
	
	public Position getPosition(int len) {
		Position result=null;

		if (position > source.length()) { // outline thread bug
			return new Position(pos_line, pos_col, pos_cur, len);
		}
		
		while (pos_cur < position) {
			if (source.charAt(pos_cur) == '\n') {
				pos_col = 0;
				pos_line++;
			} else {
				pos_col++;
			}
			pos_cur++;
		}
		if (pos_line > 32767) {
			result = new Position(32767, pos_col, pos_cur);
			reporter.syntaxError ("Line counter overflow.",result);
		}
		if (pos_col > 255) {
			result = new Position(pos_line, 255, pos_cur);
			reporter.syntaxError ("Column counter overflow (line too long).",result);
		};
		result = new Position(pos_line, pos_col, pos_cur, len);
		return result;
	}
	
	public Position getLine() {
		Position result=null;
		int startLine = pos_cur;
		int endLine = position+1;
		
		while (pos_cur < position && !isEOF()) {
			if (source.charAt(pos_cur) == '\n') {
				pos_col = 0;
				pos_line++;
				startLine = pos_cur+1;
			} else {
				pos_col++;
			}
			pos_cur++;
		}
		if (pos_line > 32767) {
			result = new Position(32767, pos_col, pos_cur);
			reporter.syntaxError ("Line counter overflow.",result);
		}
		if (pos_col > 255) {
			result = new Position(pos_line, 255, pos_cur);
			reporter.syntaxError ("Column counter overflow (line too long).",result);
		};
		while (endLine <= source.length()-1) {
			if (source.charAt(endLine) == '\n') {
				break;
			}
			endLine++;
		}
		result = new Position(pos_line, pos_col, endLine, endLine - startLine);
		return result;
	}
	
	public void updateLine() {
		while (pos_cur < position) {
			if (source.charAt(pos_cur) == '\n') {
				pos_col = 0;
				pos_line++;
			} else {
				pos_col++;
			}
			pos_cur++;
		}
	}
	
	public int getOffset() {
		return position;
	}
	
	public String getSource() {
		return source;
	}

	public boolean isEOF() {
		return position > source.length()-1;
	}

	public void setPosition(int pos) {
		initialize();
		position = pos;
	}

	protected long lastInteger;
	protected String lastReal;
	protected String lastString;

	protected static String lastComment;
	protected boolean isCatchComment;
	
	protected boolean isParameterType;
	
	protected void setCatchComment() {
		isCatchComment = true;
		lastComment = "";
	}
	protected void setCatchCommentOff() {
		isCatchComment = false;
	}
	
	public String getLastString() {
		return lastString;
	}
	
	public char lastCharacter() {
		if (position >= source.length()) {
			return 0;
		} 
		return source.charAt(position);
	}

	//
	// AMBIGU Manager.
	//

	protected int old_position;
	protected int old_pos_cur;
	protected int old_pos_line;
	protected int old_pos_col;

	protected void saveContext() {
		old_position = position;
		old_pos_cur  = pos_cur;
		old_pos_line = pos_line;
		old_pos_col  = pos_col;
	}
	protected void restoreContext() {
		position = old_position;
		pos_cur  = old_pos_cur;
		pos_line = old_pos_line;
		pos_col  = old_pos_col;
	}
	
	public AbstractLisaacParser(InputStream contents, ILisaacModel model) {
		this.model = model;
		this.reporter = model.getReporter();
		
		// convert the input stream into string
		try {
			InputStreamReader reader = new InputStreamReader (contents);
			BufferedReader buffer = new BufferedReader(reader);

			StringWriter writer = new StringWriter();

			String line="";
			do {
				line = buffer.readLine();
				if (line != null) {
					writer.write(line+"\n"); 
				}
			} while (line != null);
			//
			source = writer.toString();
			//
		} catch (IOException e) {
			return; // ERROR
		}
		initialize();
	}
	
	public AbstractLisaacParser(String contents) {
		// null reporter
		this.reporter = new ILisaacErrorHandler() {
			public void fatalError(String msg, Position position) {
			}
			public void semanticError(String msg, Position position) {
			}
			public void syntaxError(String msg, Position position) {
			}
			public void warning(String msg, Position position) {
			}
			public void enableErrorReport(boolean enable) {	
			}
		};
		source = contents;
		initialize();
	}
	
	public void initialize() {
		position = 0;
		pos_cur = 0;
		pos_line = 1;
		pos_col = 0;
	}
	
	//
	// Lisaac Parser
	//

	public boolean readSpace() {
		int pos,posold;
		int level_comment = 0;

		pos = position;
		posold = -1;
		while (posold != position) {
			posold = position;

			// skip spaces
			while ((lastCharacter() != 0) && (lastCharacter() <= ' ')) {
				position++;
			}
			if (position < source.length()-1) {
				// Skip C++ comment style :
				if (lastCharacter() == '/' && source.charAt(position+1) == '/') {
					position += 2;

					if (isCatchComment) 
						lastComment += "\t";

					while ((lastCharacter() != 0) && (lastCharacter() != '\n')) {
						if (isCatchComment) 
							lastComment += lastCharacter();

						lastCharacter();
						position++;
					}
					if (isCatchComment) 
						lastComment += "\n";
				}
			}
			if (position < source.length()-1) {
				// Skip C comment style :
				if (lastCharacter() == '/' && source.charAt(position+1) == '*') {
					position += 2;
					level_comment++;

					while (lastCharacter() != 0 && level_comment != 0) {
						if (lastCharacter() == '/' && source.charAt(position+1) == '*') {
							position += 2;
							level_comment++;
						} else if (lastCharacter() == '*' && source.charAt(position+1) == '/') {
							position += 2;
							level_comment--;
						} else {
							position++;
						}
					}
					if (level_comment != 0) {
						reporter.syntaxError("End of comment not found !", getPosition());
					}
				}
			}
		}
		// FALSE : Last character.
		begin_position = position;
		return (position != pos) || (lastCharacter() != 0);
	}

	public boolean readSymbol(String st) {
		int posold,j;
		boolean result=false;

		if (! readSpace()) {
			result = false;
		} else {
			posold = position;
			j = 0;
			while (lastCharacter() != 0 && (j <= st.length()-1 && lastCharacter() == st.charAt(j))) {
				position++;
				j++;
			}
			if (j > st.length()-1) {
				result = true;
				lastString = st;
			} else {
				position = posold;
				result = false;
			}
		}
		return result;
	}

	public boolean readCharacter (char ch) {
		boolean result=false;

		if (! readSpace()) {
			result = false;
		} else {
			if (lastCharacter() == ch) {
				position++;
				result = true;
			}
		}
		return result;
	}

	//-- affect -> ":=" | "<-" | "?="
	public boolean readAffect() {
		return readSymbol(ILisaacModel.symbol_affect_immediate) ||
		readSymbol(ILisaacModel.symbol_affect_cast) ||
		readSymbol(ILisaacModel.symbol_affect_code);
	}

	//-- style         -> '-' | '+'
	public char readStyle() {
		char result;

		if (readCharacter('-')) {
			result = '-';
		} else if (readCharacter('+')) {
			result = '+';
		} else {
			result = ' ';
		}
		return result;
	}

	//-- identifier    -> 'a'-'z' {'a'-'z' | '0'-'9' | '_'}
	public boolean readIdentifier() {
		boolean result=false;
		int posold,idx;

		if (! readSpace() || !Character.isLowerCase(lastCharacter())) {
			result = false;
		} else {
			posold = position;
			string_tmp = "";

			while (lastCharacter() != 0 && 
					(Character.isLowerCase(lastCharacter()) ||
							Character.isDigit(lastCharacter()) ||
							lastCharacter() == '_')) {
				string_tmp += lastCharacter();
				position++;
			}
			if (string_tmp.length() > 0) {
				idx = string_tmp.lastIndexOf("__");
				if (idx != -1) {
					position = posold+idx;
					reporter.syntaxError("Identifier is incorrect.", getPosition());
				}
				lastString = getString(string_tmp);
				result = true;
			}
		}
		return result;
	}

	public boolean readWord(String st) {
		int posold,idx;
		boolean result=false;

		if (! readSpace()) {
			result = false;
		} else {
			posold = position;
			idx = 0;

			while (idx <= st.length()-1 && lastCharacter() == st.charAt(idx)) {
				position++;
				idx++;
			}
			if (idx > st.length()-1) {
				lastString = st;
				result = true;
			} else {
				position = posold;
			}
		}
		return result;
	}

	public boolean readThisKeyword(String st) {
		return readWord(st);
	}

	//-- keyword -> 'A'-'Z' 'a'-'z' {'a'-'z' | '0'-'9' | '_'}
	public boolean readKeyword() {
		boolean result=false;

		if (! readSpace() || ! Character.isUpperCase(lastCharacter())) {
			result = false;
		} else {
			string_tmp = "";
			string_tmp += lastCharacter();
			position++;

			if (Character.isLowerCase(lastCharacter())) {
				string_tmp += lastCharacter();
				position++;				
				while (lastCharacter() != 0 &&
						(Character.isLowerCase(lastCharacter()) ||
								Character.isDigit(lastCharacter()) ||
								lastCharacter() == '_')) {
					string_tmp += lastCharacter();
					position++;
				}
				lastString = getString(string_tmp);
				result = true;
			} else {
				position--;
				result = false;
			}
		}
		return result;
	}

	//-- cap_identifier -> 'A'-'Z' {'A'-'Z' | '0'-'9' | '_'}
	public boolean readCapIdentifier() {
		int posold,idx;
		boolean result=false;
		char car;

		if (! readSpace() || ! Character.isUpperCase(lastCharacter())) {
			result = false;
		} else {
			posold = position;
			string_tmp = ""+lastCharacter();
			position++;
			isParameterType = true;
			while (lastCharacter() != 0 && 
					(Character.isUpperCase(lastCharacter()) ||
							Character.isDigit(lastCharacter()) ||
							lastCharacter() == '_')) {
				car = lastCharacter();
				isParameterType = isParameterType && (Character.isDigit(car));

				string_tmp += car;
				position++;
			}
			if (Character.isLetter(lastCharacter()) ||
					Character.isDigit(lastCharacter()) ||
					lastCharacter() == '_') {
				reporter.syntaxError("Identifier is incorrect.", getPosition());
				return false;
			} 
			idx = string_tmp.lastIndexOf("__");
			if (idx != -1) {
				position = posold + idx;
				reporter.syntaxError("Identifier is incorrect.", getPosition());
				return false;
			}
			lastString = getString(string_tmp);
			result = true;
		}
		return result;
	}

	//-- integer -> number 
	//-- number  -> {'0'-'9'} ['d'] 
	//--          | '0'-'9' {'0'-'9' | 'A'-'F' | 'a'-'f'} 'h'
	//--          | {'0'-'7'} 'o'
	//--          | {'0' | '1'} 'b'
	public boolean readInteger() {
		boolean result=false;
		//int pos_old;

		if (readSpace() && Character.isDigit(lastCharacter())) {
			result = true;
			string_tmp = ""+lastCharacter();
			//pos_old = position;
			position++;

			while (isHexadecimalDigit(lastCharacter()) || lastCharacter() == '_') {
				if (lastCharacter() != '_') {
					string_tmp += lastCharacter();
				}
				position++;
			}
			if (lastCharacter() == 'h') {
				try {
					Integer integer = Integer.valueOf(string_tmp, 16);
					lastInteger = integer.intValue();
				} catch (Exception e) {
					//System.out.println("Warning readInteger : "+e);// FIXME hex string
					lastInteger = 0;
				}
				position++;
			} else {
				if (string_tmp.charAt(string_tmp.length()-1) > '9') {
					string_tmp = string_tmp.substring(0, string_tmp.length()-1);// remove last
					position--;
				}
				if (lastCharacter() == 'o') {
					if (!isOctal(string_tmp)) {
						reporter.syntaxError("Incorrect octal number.", getPosition());
					}
					lastInteger = Integer.valueOf(string_tmp, 8).intValue();
					position++;
				} else if (lastCharacter() == 'b') {
					if (!isBinary(string_tmp)) {
						reporter.syntaxError("Incorrect binary number.", getPosition());
					}
					lastInteger = Integer.valueOf(string_tmp, 2).intValue();
					position++;
				} else {
					if (lastCharacter() == 'd') {
						position++;
					}
					if (! isInteger(string_tmp)) {
						reporter.syntaxError("Incorrect decimal number.", getPosition());
					}
					lastInteger = Integer.valueOf(string_tmp);
				}
			}
		}
		return result;
	}

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	// True when the contents is a sequence of bits (i.e., mixed
	// characters `0' and characters `1').
	private boolean isBinary(String s) {
		boolean result;
		int i;

		i = s.length()-1;
		result = true;
		while (result && i != 0) {
			result = s.charAt(i) == '0' || s.charAt(i) == '1';
			i--;
		}
		return result;
	}

	private boolean isOctal(String s) {
		try {
			Integer.parseInt(s, 8);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private boolean isHexadecimalDigit(char c) {
		boolean result=false;

		if (Character.isDigit(c)) {
			result = true;
		} else if (c >= 'a') {
			result = c <= 'f';
		} else if (c >= 'A') {
			result = c <= 'F';
		}
		return result;
	}

	//-- real -> '0'-'9' {'0'-'9'_} [ '.' {'0'-'9'} ] [ 'E' ['+'|'-'] '0'-'9' {'0'-'9'}
	public boolean readReal() {
		boolean result=false;
		int pos_old;

		if (readSpace() && Character.isDigit(lastCharacter())) {
			string_tmp = ""+lastCharacter();
			pos_old = position;
			position++;

			while (Character.isDigit(lastCharacter()) || lastCharacter() == '_') {
				if (lastCharacter() != '_') {
					string_tmp += lastCharacter();
				}
				position++;
			}
			if (lastCharacter() == '.') {
				string_tmp += '.';
				position++;

				if (Character.isDigit(lastCharacter())) {
					result = true;
					string_tmp += lastCharacter();
					position++;

					while (Character.isDigit(lastCharacter())) {
						string_tmp += lastCharacter();
						position++;
					}
				}
				if (lastCharacter() == 'E') {
					result = true;
					string_tmp += 'E';
					position++;

					if (lastCharacter() == '+' || lastCharacter() == '-') {
						string_tmp += lastCharacter();
						position++;
					}
					if (Character.isDigit(lastCharacter())) {
						string_tmp += lastCharacter();
						position++;
						while (Character.isDigit(lastCharacter())) {
							string_tmp += lastCharacter();
							position++;
						}
					} else {
						reporter.syntaxError("Incorrect real number.", getPosition());
					}
				}
			}
			if (result) {
				lastReal = getString(string_tmp);
			} else {
				position = pos_old;
			}
		}
		return result;
	}

	public void readEscapeCharacter() {
		int val;

		if (isSeparator(lastCharacter())) {
			position++;
			while (lastCharacter() != 0 && isSeparator(lastCharacter())) {
				position++;
			}
			if (lastCharacter() == '\\') {
				string_tmp.substring(0, string_tmp.length()-2); // remove last
				position++;
			} else if (lastCharacter() != 0) {
				reporter.syntaxError("Unknown escape sequence.", getPosition());
			}
		} else if (lastCharacter() != 0) {
			char c = lastCharacter();

			if (c == 'a' ||
					c == 'b' ||
					c == 'f' ||
					c == 'n' ||
					c == 'r' ||
					c == 't' ||
					c == 'v' ||
					c == '\\' ||
					c == '?' ||
					c == '\'' ||
					c == '\"') {
				string_tmp += c;
				position++;
			} else if (lastCharacter() >= '0' && lastCharacter() <= '9') {
				if (lastCharacter() == '0' && 
						position < source.length() &&
						! isHexadecimalDigit(source.charAt(position+1))) {

					string_tmp += lastCharacter();
					position++;
				} else {
					String string_tmp2 = new String(string_tmp);
					readInteger(); // result is Always TRUE.
					string_tmp = string_tmp2;

					if (lastInteger > 255) {
						reporter.syntaxError("Invalid range character number [0,255].", getPosition());
					}
					val = (int) lastInteger;
					string_tmp += (val / 64);
					string_tmp += ((val % 64) / 8);
					string_tmp += (val % 8);
					if (lastCharacter() == '\\') {
						position++;
					} else {
						reporter.syntaxError("Character '\' is needed.", getPosition());
					}
				}
			} else {
				reporter.syntaxError("Unknown escape sequence.", getPosition());
			}
		}
	}

	//-- character  -> '\'' ascii '\''
	public boolean readCharacters() {
		boolean result=false;
		int count=0;

		if (readSpace() && lastCharacter() == '\'') {
			//old_pos = position;
			position++;
			string_tmp = "";
			while (lastCharacter() != 0 && lastCharacter() != '\n' && lastCharacter() != '\'') {
				string_tmp += lastCharacter();
				if (lastCharacter() == '\\') {
					position++;
					readEscapeCharacter();
					count++;
				} else {
					position++;
					count++;
				}
			}
			if (lastCharacter() == '\'') {
				position++;
				lastString = getString(string_tmp);
				if (count != 1) {
					position = begin_position;
					reporter.syntaxError("Character constant too long.", getPosition());
				}
				result = true;
			} else {
				position = begin_position;
				reporter.syntaxError("Unterminated character constant.", getPosition());
			}
		}
		return result;
	}

	//-- string -> '\"' ascii_string '\"'
	public boolean readString() {
		boolean result=false;

		if (readSpace() && lastCharacter() == '\"') {
			//	old_pos = position;
			position = position+1;
			string_tmp = "";
			while (lastCharacter() != 0 && lastCharacter() != '\n' && lastCharacter() != '\"') {
				string_tmp += lastCharacter();
				if (lastCharacter() == '\\') {
					position = position+1;
					readEscapeCharacter();
				} else {
					position = position+1;
				}
			}
			if (lastCharacter() == '\"') {
				position = position+1;
				lastString = getString(string_tmp);
				result = true;
			} else { 
				position = begin_position;
				reporter.syntaxError("Unterminated string constant.", getPosition());
			}
		}
		return result;
	}

	//-- external -> '`' ascii_c_code '`'
	public boolean readExternal() {
		boolean result=false;
		//	int pos_old;

		if ((! readSpace()) || lastCharacter() != '`') {
			result = false;
		} else {
			//	pos_old=position;
			position = position+1;
			string_tmp = "";
			while (lastCharacter() != 0 && lastCharacter() != '`') {
				string_tmp += lastCharacter();
				if (lastCharacter() == '\\') {
					position = position+1;
					string_tmp += lastCharacter();
					if (lastCharacter() != 0) {
						position = position+1;
					}
				} else {
					position = position+1;
				}
			}
			if (lastCharacter() != 0) {
				position = position+1;
				lastString = getString(string_tmp);
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}

	private static final String operators = "!@#$%^&<|*-+=~/?\\>";

	public static boolean isOperatorSymbol(char c) {
		return operators.indexOf(c) != -1;
	}
	
	//-- operator -> '!' | '@' | '#' | '$' | '%' | '^' | '&' | '<' | '|'  
	//--           | '*' | '-' | '+' | '=' | '~' | '/' | '?' | '\' | '>'
	public boolean readOperator() {
		boolean result=false;
		//		int old_pos;

		readSpace();
		//	old_pos = position;
		string_tmp = "";
		while (lastCharacter() != 0 &&
				operators.indexOf(lastCharacter()) != -1) {
			string_tmp += lastCharacter();
			position = position+1;
		}
		if (string_tmp.length() > 0) {
			lastString = getString(string_tmp);
			if (lastString.equals(ILisaacModel.symbol_affect_immediate) ||
					lastString.equals(ILisaacModel.symbol_affect_code) ||
					lastString.equals(ILisaacModel.symbol_affect_cast)) {
				reporter.syntaxError("Incorrect operator.", getPosition());
			}
			result = true;
		}
		return result;
	}

	// True when character is a separator.
	private boolean isSeparator(char c) {
		return c == ' ' || c == '\t' || c == '\n' ||
		c == '\r' || c == '\0' || c == '\f'; // || c == '\v';
	}
	
	public String getString(String str) {
		if (model == null) {
			return str;
		}
		return model.getAliasString().get(str);
	}

	
	public ILisaacErrorHandler getReporter() {
		return reporter;
	}
	
	public void readTokenAt(int line, int column) {
		// goto (line,column) position
		while (! isEOF()) {
			if (source.charAt(pos_cur) == '\n') {
				pos_col = 0;
				pos_line++;
			} else {
				pos_col++;
			}
			if (pos_line == line && pos_col == column) {
				position = pos_cur;
				break;
			}
			pos_cur++;
		}
		// goto token begining
		while (position >= 0) {
			char c = lastCharacter();
			if (isSeparator(c)) {
				break;
			}
			position--;
		}
		// read token
		position++;
		string_tmp = "";
		while (! isEOF()) {
			char c = lastCharacter();
			if (isSeparator(c)) {
				break;
			}
			string_tmp += c;
			position++;
		}
		lastString = string_tmp;
	}
}
