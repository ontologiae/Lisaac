package org.lisaac.ldt.model;

import java.io.InputStream;
import java.util.ArrayList;

import org.lisaac.ldt.model.lip.*;


public class LipParser extends AbstractLisaacParser {

	public LIP lipFile;

	public LipParser(InputStream contents, ILisaacModel model) {
		super(contents, model);
		this.lipFile = model.getLipCode();
	}

	public LipParser(String contents, int offset) {
		super(contents);
		setPosition(offset);
	}
	
	//
	// Parser for LIP file.
	// 

	////PROGRAM      -> { 'Section' ('Inherit' | 'Public' | 'Private') { SLOT ';' } } 
	public boolean parse() {
		boolean result=false;

		while (readThisKeyword(ILisaacModel.keyword_section)) {
			if (readThisKeyword(ILisaacModel.section_inherit)) {
				// { '+' string ':' STRING [ ':=' string ] ';' }
				while (readCharacter('+')) {
					if (! readIdentifier()) {
						reporter.syntaxError("Identifier needed.", getLine());
						return false;
					}
					if (! readCharacter(':')) {
						reporter.syntaxError("Added ':' is needed.", getLine());
						return false;
					}
					if (! readWord(ILisaacModel.prototype_string)) {
						reporter.warning("`STRING' type needed.", getLine());
					}
					if (readSymbol(ILisaacModel.symbol_affect_immediate)) {
						if (! readString()) {
							reporter.syntaxError("String needed.", getLine());
							return false;
						}
						string_tmp = new String(lipFile.getFileName());
						while (string_tmp.length() > 0) {
							char c = string_tmp.charAt(string_tmp.length()-1);
							if (c == '/' || c == '\\') {
								break;
							}// FIXME use index
							string_tmp = string_tmp.substring(0, string_tmp.length()-1);
						}
						string_tmp += lastString;
					} else {
						string_tmp = "";
					}
					// add parent slot
					lipFile.addParent(getString(string_tmp));
					//
					if (! readCharacter(';')) {
						reporter.syntaxError("Added ';' is needed.", getLine());
						return false;
					}
					result = true;
				}
			} else if (readThisKeyword(ILisaacModel.section_public) ||
					readThisKeyword(ILisaacModel.section_private)) {
				String section = new String(lastString);
				while (readSlot(section)) {
					if (! readCharacter(';')) {
						reporter.syntaxError("Added ';' is needed.", getLine());
						return false;
					}
					result = true;
				}
			} else {
				reporter.syntaxError("`Public' or `Private' or `Inherit' needed.", getLine());
				return false;	
			}
		}
		if (position < source.length()-2) {
			result = false;
		}
		
		// TODO recursive parsing !!!
		return result;
	} 

	//// SLOT         -> '+' identifier ':' TYPE [ ':=' EXPR_CONSTANT ]
	////               | '-' identifier [ identifier ':' TYPE ] '<-' '(' { EXPR ';' } ')' 
	private boolean readSlot(String sec) {
		boolean result=false;
		LIPSlotData data=null;

		if (readCharacter('+')) {
			// Data.
			result = true;
			if (sec.equals(ILisaacModel.section_public)) {
				reporter.syntaxError("No data in Public section.", getPosition(1));
			}
			if (! readIdentifier()) {
				reporter.syntaxError("Identifier is incorrect.", getLine());
				return false;
			}
			String n = new String(lastString);
			if (! readCharacter(':')) {
				reporter.syntaxError("Added ':' is needed.", getLine());
				return false;
			}
			LIPConstant t = readType();
			if (t == null) {
				reporter.syntaxError("type is incorrect.", getLine());
				return false;
			}
			data = new LIPSlotData(n, t);
			//
			lipFile.addData(data);
			//
			if (readSymbol(LisaacModel.symbol_affect_immediate)) {
				LIPConstant cst = readExprConstant();
				if (cst == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return false;
				}
				data.setValue(cst);
				cst.free();
			}
		} else if (readCharacter('-')) {
			// Function.
			result = true;
			if (! readIdentifier()) {
				reporter.syntaxError("Identifier is incorrect.", getLine());
				return false;
			}
			//Position pos = getPosition();
			setCatchComment();

			String n = new String(lastString);
			if (readIdentifier()) {
				String na = new String(lastString);
				if (! readCharacter(':')) {
					reporter.syntaxError("Added ':' is needed.", getLine());
					return false;
				}
				LIPConstant t = readType();
				if (t == null) {
					reporter.syntaxError("Incorrect type.", getLine());
					return false;
				}
				data = new LIPSlotData(na, t);// do not add argument do lipFile
			}
			//
			if (! readSymbol(ILisaacModel.symbol_affect_code)) {
				reporter.syntaxError("Added '<-' is needed.", getLine());
				return false;
			}
			if (! readCharacter('(')) {
				reporter.syntaxError("Added '(' is needed.", getLine());
				return false;
			}
			setCatchCommentOff();
			ArrayList<LIPCode> code = new ArrayList<LIPCode>();
			LIPCode instr;
			while ((instr = readExpr()) != null) {
				code.add(instr);
				if (! readCharacter(';')) {
					reporter.syntaxError("Added ';' is needed.", getLine());
					return false;
				}
			}
			if (! readCharacter(')')) {
				reporter.syntaxError("Added ')' is needed.", getLine());
				return false;
			}
			LIPSlotCode slotCode = new LIPSlotCode(sec, n, data, code.toArray(new LIPCode[code.size()]));
			lipFile.addMethod(slotCode);
			if (sec.equals(ILisaacModel.section_public)) {
				if (lastComment == null || lastComment.length() == 0) {
					reporter.syntaxError("Comment needed.", getPosition());
				} else {
					slotCode.setComment(lastComment);
				}
			}
		}
		return result;
	}

	////TYPE         -> 'BOOLEAN' | 'STRING' | 'INTEGER'
	private LIPConstant readType() {
		LIPConstant result=null;

		if (readCapIdentifier()) {
			if (lastString.equals(ILisaacModel.prototype_integer)) {
				result = LIPInteger.get(0);
			} else if (lastString.equals(ILisaacModel.prototype_string)) {
				result = LIPString.get(getString(""));
			} else if (lastString.equals(ILisaacModel.prototype_boolean)) {
				result = LIPBoolean.get(false);
			} else {
				reporter.syntaxError("Incorrect type.", getLine());
			}
		}
		return result;
	}

	//// EXPR         -> [ identifier !!AMBIGU!! ':=' ] EXPR_OPERATOR [ '.' FUNCTION ]
	public LIPCode readExpr() {
		LIPCode result=null;

		saveContext(); // !! SAVE CONTEXT !!

		if (readIdentifier()) {
			String name = new String(lastString);
			if (readSymbol(ILisaacModel.symbol_affect_immediate)) {
				LIPCode val = readExprOperator();
				if (val == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return null;
				}
				result = new LIPAffect(name, val);
			} else {
				restoreContext(); // !! RESTORE CONTEXT !!
			}
		}
		if (result == null) {
			result = readExprOperator();
			if (result != null && readCharacter('.')) {
				result = readFunction(result);
				if (result == null) {
					reporter.syntaxError("Incorrect slot.", getLine());
					return null;
				}
			}
		}
		return result;
	}

	//// FUNCTION     -> 'if' '{' { EXPR ';' }  '}' [ 'else' '{' { EXPR ';' } '}' ]
	////               | 'print'
	private LIPCode readFunction(LIPCode rec) {
		LIPCode result=null;
		ArrayList<LIPCode> thenArray=null, elseArray=null;

		if (readWord(ILisaacModel.slot_if)) {
			thenArray = new ArrayList<LIPCode>();
			if (! readCharacter('{')) {
				reporter.syntaxError("Added '{' is needed.", getLine());
				return null;
			}
			LIPCode val;
			while ((val = readExpr()) != null) {
				thenArray.add(val);
				if (! readCharacter(';')) {
					reporter.syntaxError("Added ';' is needed.", getLine());
					return null;
				}
			}
			if (! readCharacter('}')) {
				reporter.syntaxError("Added '}' is needed.", getLine());
				return null;
			}
			if (readWord(ILisaacModel.slot_else)) {
				elseArray = new ArrayList<LIPCode>();
				if (! readCharacter('{')) {
					reporter.syntaxError("Added '{' is needed.", getLine());
					return null;
				}
				while ((val = readExpr()) != null) {
					elseArray.add(val);
					if (! readCharacter(';')) {
						reporter.syntaxError("Added ';' is needed.", getLine());
						return null;
					}
				}
				if (! readCharacter('}')) {
					reporter.syntaxError("Added '}' is needed.", getLine());
					return null;
				}
			}
			result = new LIPIf(rec, thenArray.toArray(new LIPCode[thenArray.size()]),
					elseArray.toArray(new LIPCode[elseArray.size()]));
		} else if (readWord(ILisaacModel.slot_print)) {
			result = new LIPPrint(rec);
		}
		return result;
	}

	////EXPR_OPERATOR-> EXPR_CMP { ('|' | '&') EXPR_CMP }   
	private LIPCode readExprOperator() {
		LIPCode result=null;
		boolean isOr=false;

		result = readExprCmp();
		if (result != null) {
			while ((isOr = readCharacter('|')) || readCharacter('&')) {
				LIPCode right = readExprCmp();
				if (right == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return null;
				}
				if (isOr) {
					result = new LIPBinary(result, '|', right);
				} else {
					result = new LIPBinary(result, '&', right);
				}
			}
		}
		return result;
	}

	////EXPR_CMP     -> EXPR_BINARY { ('='|'!='|'>'|'<'|'>='|'<=') EXPR_BINARY }
	private LIPCode readExprCmp() {

		LIPCode result = readExprBinary();
		if (result != null) {
			while (readSymbol(ILisaacModel.symbol_great_equal) ||
					readSymbol(ILisaacModel.symbol_less_equal) ||
					readSymbol(ILisaacModel.symbol_not_equal) ||
					readSymbol(ILisaacModel.symbol_equal) ||
					readSymbol(ILisaacModel.symbol_great) ||
					readSymbol(ILisaacModel.symbol_less)) {
				String op = new String(lastString);
				LIPCode right = readExprBinary();
				if (right == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return null;
				}
				char type = 0;
				if (op.equals(">=")) {
					type = 'S';
				} else if (op.equals("<=")) {
					type = 'I';
				} else if (op.equals("!=")) {
					type = 'E';
				} else if (op.equals("=")) {
					type = '=';
				} else if (op.equals(">")) {
					type = '>';
				} else if (op.equals("<")) {
					type = '<';
				}
				result = new LIPBinary(result, type, right);	
			}
		}
		return result;
	}

	////EXPR_BINARY  -> EXPR_UNARY { ('-'|'+') EXPR_UNARY }
	private LIPCode readExprBinary() {
		boolean isSub;

		LIPCode result = readExprUnary();
		if (result != null) {
			while ((isSub = readCharacter('-')) || readCharacter('+')) {
				LIPCode right = readExprUnary();
				if (right == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return null;
				}
				if (isSub) {
					result = new LIPBinary(result, '-', right);
				} else {
					result = new LIPBinary(result, '+', right);
				}
			}
		}
		return result;
	}

	//// EXPR_UNARY   -> ( '-' | '!' ) EXPR_UNARY
	////               | EXPR_BASE
	////               | identifier [ EXPR_ARGUMENT ]
	private LIPCode readExprUnary() {
		LIPCode result=null;
		boolean isNeg;

		if ((isNeg = readCharacter('-')) || readCharacter('!')) {
			result = readExprUnary();
			if (result == null) {
				reporter.syntaxError("Incorrect expression.", getLine());
				return null;
			}
			char type;
			if (isNeg) {
				type = '-';
			} else {
				type = '+';
			}
			result = new LIPUnary(type, result);
		} else if (readIdentifier()) {
			String name = new String(lastString);
			LIPCode arg = readExprArgument();
			result = new LIPCall(name, arg);
		} else {
			result = readExprBase();
		}
		return result;
	}

	//// EXPR_BASE    -> '(' EXPR_OPERATOR ')'
	////               | EXPR_CONSTANT
	private LIPCode readExprBase() {
		LIPCode result=null;

		if (readCharacter('(')) {
			result = readExprOperator();
			if (result == null) {
				reporter.syntaxError("Incorrect expression.", getLine());
				return null;
			}
			if (! readCharacter(')')) {
				reporter.syntaxError("Added ')' is needed.", getLine());
				return null;
			}
		} else {
			LIPConstant v = readExprConstant();
			if (v != null) {
				result = new LIPValue(v);
			}
		}
		return result;
	}

	////EXPR_CONSTANT-> integer              
	////               | string
	////               | TRUE
	////               | FALSE
	private LIPConstant readExprConstant() {
		LIPConstant result=null;

		if (readInteger()) {
			result = LIPInteger.get((int)lastInteger);
		} else if (readString()) {
			result = LIPString.get(lastString);
		} else if (readCapIdentifier()) {
			if (lastString.equals(ILisaacModel.prototype_true)) {
				result = LIPBoolean.get(true);
			} else if (lastString.equals(ILisaacModel.prototype_false)){
				result = LIPBoolean.get(false);	
			} else {
				reporter.syntaxError("Type incorrect.", getLine());
			}
		}
		return result;
	}

	//// EXPR_ARGUMENT-> identifier 
	////               | EXPR_BASE
	private LIPCode readExprArgument() {
		LIPCode result=null;

		if (readIdentifier()) {
			result = new LIPCall(lastString, null);
		} else {
			result = readExprBase();
		}
		return result;
	}
}
