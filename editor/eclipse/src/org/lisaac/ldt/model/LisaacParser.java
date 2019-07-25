package org.lisaac.ldt.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lisaac.ldt.model.items.IArgument;
import org.lisaac.ldt.model.items.ICode;
import org.lisaac.ldt.model.items.IConstant;
import org.lisaac.ldt.model.items.ITMArgs;
import org.lisaac.ldt.model.items.ITMArgument;
import org.lisaac.ldt.model.items.ITMBlock;
import org.lisaac.ldt.model.items.ITMCharacter;
import org.lisaac.ldt.model.items.ITMExpression;
import org.lisaac.ldt.model.items.ITMExternal;
import org.lisaac.ldt.model.items.ITMExternalType;
import org.lisaac.ldt.model.items.ITMLDots;
import org.lisaac.ldt.model.items.ITMList;
import org.lisaac.ldt.model.items.ITMListIdf;
import org.lisaac.ldt.model.items.ITMLocal;
import org.lisaac.ldt.model.items.ITMNumber;
import org.lisaac.ldt.model.items.ITMOld;
import org.lisaac.ldt.model.items.ITMOperator;
import org.lisaac.ldt.model.items.ITMPrototype;
import org.lisaac.ldt.model.items.ITMRead;
import org.lisaac.ldt.model.items.ITMReadArg1;
import org.lisaac.ldt.model.items.ITMReadArg2;
import org.lisaac.ldt.model.items.ITMReadArgs;
import org.lisaac.ldt.model.items.ITMReal;
import org.lisaac.ldt.model.items.ITMResult;
import org.lisaac.ldt.model.items.ITMString;
import org.lisaac.ldt.model.items.ITMWrite;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.items.Section;
import org.lisaac.ldt.model.items.Slot;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.ITypeMono;
import org.lisaac.ldt.model.types.TypeBlock;
import org.lisaac.ldt.model.types.TypeGeneric;
import org.lisaac.ldt.model.types.TypeMulti;
import org.lisaac.ldt.model.types.TypeParameter;
import org.lisaac.ldt.model.types.TypeSelf;
import org.lisaac.ldt.model.types.TypeSimple;

/**
 *  Lisaac Prototype Parser
 */


public class LisaacParser extends AbstractLisaacParser {

	private String selfType;

	private ILisaacContext sectionContext;
	private ILisaacContext slotContext;

	//
	private Slot lastSlot;
	private ITMList lastGroup;
	private Section lastSection;
	//

	public LisaacParser(String selfType, InputStream contents, ILisaacModel model) {
		super(contents, model);
		this.model = model;
		this.selfType = selfType;
		sectionContext = new SectionContext(this);
		slotContext = new SlotContext(this);

		initialize();
	}

	public LisaacParser(String selfType, String contents) {
		super(contents);
		this.selfType = selfType;
		initialize();
	}

	public void initialize() {
		//
		// initialisations
		//
		super.initialize();
		TypeSimple.init();
	}
	
	public void enableErrorReport(boolean enable) {
		reporter.enableErrorReport(enable);
	}

	public ILisaacContext getSectionContext() {
		return sectionContext;
	}

	public Slot getLastSlot() {
		return lastSlot;
	}

	public void setLastSection(Section section) {
		if (lastSection != null) {
			// update last section length
			Position pos = lastSection.getPosition();
			pos.setLength(position - pos.offset);

			// update link
			lastSection.setNext(section);
		}
		lastSection = section;
	}

	//
	// Lisaac Prototype Parser
	//

	//++ TYPE_LIST    -> TYPE { ',' TYPE }
	public ITypeMono[] readTypeList(boolean isSection) {
		ArrayList<ITypeMono> lst=null;
		ITypeMono t;

		t = readType(false);
		if (t != null) {
			if (isSection) {
				if (! (t instanceof TypeSimple) && ! (t instanceof TypeSelf)) {
					reporter.syntaxError("For a section, the prototype name only (without '('...')').", getLine());
					return null;
				}
			}
			lst = new ArrayList<ITypeMono>();
			lst.add(t);
			while (readCharacter(',')) {
				t = readType(false);
				if (t == null) {
					reporter.syntaxError("Incorrect type list.", getLine());
					return null;
				}
				if (isSection) {
					if (! (t instanceof TypeSimple) && ! (t instanceof TypeSelf)) {
						reporter.syntaxError("For a section, the prototype name only (without '('...')').", getLine());
						return null;
					}
				}
				lst.add(t);
			}
			// TODO alias lst
		}
		if (lst != null) {
			return lst.toArray(new ITypeMono[lst.size()]);
		}
		return null;
	}

	//++ TYPE         -> '{' [ (TYPE | '(' TYPE_LIST ')') ';' ] [ TYPE_LIST ] '}'
	//++               | [type] PROTOTYPE [ CONTRACT ]
	public ITypeMono readType (boolean isLocal) {
		ITypeMono result=null;
		ITypeMono[] lst=null;
		IType typ_arg=null,typ_res=null;
		String style=null;

		if (readCharacter('{')) {
			// '{' [ (TYPE | '(' TYPE_LIST ')') ';' ] [ TYPE_LIST ] '}' 
			if (readCharacter('(')) {
				// Read vector argument.
				lst = readTypeList(false);
				if (lst == null) {
					reporter.syntaxError("Incorrect type list.", getLine());
					return null;
				}
				if (lst.length == 1) {
					typ_arg = lst[0];
				} else {
					typ_arg = TypeMulti.get(lst);
				}
				if (! readCharacter(')')) {
					reporter.syntaxError("Added ')'.", getLine());
					return null;
				}
				if (! readCharacter(';')) {
					reporter.syntaxError("Added ';'.", getLine());
				}
				// Read result type.
				lst = readTypeList(false);
			} else {

				lst = readTypeList(false);
				if (lst != null) {
					if (readCharacter(';')) {
						if (lst.length == 1) {
							typ_arg = lst[0];
						} else {
							typ_arg = TypeMulti.get(lst);
							// TODO warning "Added 'typ_arg'."
						}
						// Read result type.
						lst = readTypeList(false);
					}
				}
			}
			if (lst != null) {
				if (lst.length == 1) {
					typ_res = lst[0];
				} else {
					typ_res = TypeMulti.get(lst);
				}
			}
			if (! readCharacter('}')) {
				reporter.syntaxError("Added '}'.", getLine());
				return null;
			}
			result = TypeBlock.get(typ_arg, typ_res);
		} else {
			// Expanded | Strict 
			if (readThisKeyword(ILisaacModel.keyword_expanded) ||
					readThisKeyword(ILisaacModel.keyword_strict)) {

				style = getLastString();
				if (isLocal && (style.equals(ILisaacModel.keyword_expanded))) {
					int len = ILisaacModel.keyword_expanded.length();
					reporter.syntaxError("`Expanded' is not possible.", getPosition(len));
				}
			}
			// PROTOTYPE
			result = readPrototype(style);

			// TODO read contract
		}
		return result;
	}

	//++ PROTOTYPE    -> cap_identifier{('.'|'...')cap_identifier}['('PARAM_TYPE{','PARAM_TYPE}')']
	public ITypeMono readPrototype(String style) {
		ITypeMono result=null;
		String name=null;

		if (readCapIdentifier()) {
			// TODO syntax {('.'|'...')cap_identifier}
			name = getString(lastString);
			if (readCharacter('(')) {
				//
				// Genericity.
				//
				ArrayList<ITypeMono> genericity = new ArrayList<ITypeMono>();
				do {
					ITypeMono t = readParamType();
					if (t == null) {
						reporter.syntaxError("Type needed.", getLine());
						return null;
					}
					genericity.add(t);
				} while (readCharacter(','));
				// alias genericity array...
				result = new TypeGeneric(name, style, genericity.toArray(new ITypeMono[genericity.size()]));

				if (! readCharacter(')')) {
					reporter.syntaxError("Added ')'.", getLine());
					return result;
				}
			} else {
				// Simple type.	 
				if (isParameterType) {
					if (style != null) {
						reporter.warning("Style `"+style+"' for parameter type is ignored.", getPosition(name.length()));
					}
					result = TypeParameter.get(name);
				} else if (style == null) {
					if (name.equals(ILisaacModel.prototype_self)) {
						result = TypeSelf.get(selfType);
					} else {
						result = TypeSimple.get(name);
					}
				} else {
					if (name.equals(ILisaacModel.prototype_self)) {
						reporter.warning("Style `"+style+"' ignored.", getPosition(name.length()));
						result = TypeSelf.get(selfType);
					} else {
						if (name.equals(ILisaacModel.prototype_self)) {
							result = TypeSelf.get(selfType);
						} else {
							result = TypeSimple.get(name);
						}
					}
				}
			}
		}
		return result;
	}

	//++ PARAM_TYPE   -> TYPE
	//++               | CONSTANT
	//++               | identifier
	private ITypeMono readParamType() {
		ITypeMono result = readType(false);
		if (result == null) {
			IConstant cst = readConstant();
			if (cst != null) {
				// TODO compiler not yet implemented
			} else if (readIdentifier()) {
				// TODO compiler not yet implemented
			}
		}
		return result;
	}

	//++ SLOT         -> style TYPE_SLOT [':' (TYPE|'('TYPE_LIST')') ][ affect DEF_SLOT ]';'
	public boolean readSlot(Prototype prototype) {
		char affect;
		boolean result=false;
		IType t;

		char style = readStyle();
		if (style != ' ') {
			//
			// Classic slot.
			//
			result = true;
			lastSlot = readTypeSlot();
			if (lastSlot == null) {
				reporter.syntaxError("Incorrect slot declaration.", getLine());
				return false;
			}
			lastSlot.setStyle(style);

			if (readAffect()) {
				affect = lastString.charAt(0);
			} else {
				affect = ' ';
			}
			// ':' (TYPE|'('TYPE_LIST')'
			if (affect == ' ' && readCharacter(':')) {
				if (readCharacter('(')) {
					ITypeMono[] lt = readTypeList(false);
					if (lt == null) {
						reporter.syntaxError("Incorrect result type.", getLine());
						return false;
					}
					if (! readCharacter(')')) {
						reporter.warning("Added ')' is needed.", getLine());
					}
					t = TypeMulti.get(lt);
				} else {
					t = readType(false);
					if (t == null) {
						reporter.syntaxError("Incorrect result type.", getLine());
						return false;
					}
				}
				if (readAffect()) {
					affect = lastString.charAt(0);
				}
			} else {
				t = TypeSimple.getTypeVoid();
			}
			lastSlot.setResultType(t);
			lastSlot.setAffect(affect);

			setCatchComment();

			if (affect != ' ') {
				readSpace();

				setCatchCommentOff();

				//
				readDefSlot();
				//
			}
			if (! readCharacter(';')) {
				reporter.syntaxError("Added ';'.", getLine());
				return false;
			}
			// update slot body position
			updateLine();
			Position body = lastSlot.getPositionBody();
			if (body.line != pos_line) {
				body.setLength(position - body.offset);
			} else {
				// one line slot - delete position
				lastSlot.setBody(null);
			}

			if (lastComment != null && lastComment.length() > 0) {
				lastSlot.setComment(lastComment);
			}
			if (lastComment != null && lastSection.isInheritOrInsert()) {
				// Add parent slot
				Slot s = prototype.getParentSlot(lastSlot.getName());
				if (s != null) {
					reporter.semanticError("Double slot declaration.", getPosition());
				} else {
					prototype.addParentSlot(lastSlot);
				}
			} else {
				// Added slot in prototype :
				Slot s = prototype.getSlot(lastSlot.getName());
				if (s != null) {
					reporter.semanticError("Double slot declaration.", getPosition());
				} else {
					prototype.addSlot(lastSlot);
					lastSection.addSlot(lastSlot);
				}
			}
		}
		return result;
	}

	//++ DEF_SLOT     -> [CONTRACT] EXPR [CONTRACT]
	private void readDefSlot() {

		readRequire();
		ICode expr = readExpr();
		if (expr == null) {
			reporter.syntaxError("Incorrect expression.", getLine());
		}
		lastSlot.setValue(expr);
		readEnsure();
	}

	//++ TYPE_SLOT    -> [ LOC_ARG '.' ] identifier [ LOC_ARG { identifier LOC_ARG } ]
	//++               | [ LOC_ARG ] '\'' operator '\'' [("Left"|"Right") [integer]] [LOC_ARG]
	public Slot readTypeSlot() {
		Slot result=null;
		IArgument arg=null;

		ArrayList<IArgument> list_arg = new ArrayList<IArgument>();

		arg = readLocArg(false,true);
		if (arg == null) {
			if (readCharacter('\'')) {
				result = readSlotOperator(list_arg);
			} else {
				//arg = new ITMArgument(ILisaacModel.variable_self, TypeSimple.getTypeSelf());
				//list_arg.add(arg); no use here? 

				result = readSlotKeyword(list_arg);
			}
		} else {
			list_arg.add(arg);
			if (readCharacter('.')) {
				result = readSlotKeyword(list_arg);
			} else if (readCharacter('\'')) {
				result = readSlotOperator(list_arg);
			}
		}
		if (result != null) {
			result.setArgumentList(list_arg.toArray(new IArgument[list_arg.size()]));
		}
		return result;
	}

	private Slot readSlotKeyword(ArrayList<IArgument> list_arg) {
		Slot result=null;
		Position slotPosition = getPosition();
		Position slotBody = getPosition();
		int start_pos = position;

		if (readIdentifier()) {
			String n = new String(lastString);

			ArrayList<String> keywords = new ArrayList<String>();
			keywords.add(n);

			IArgument arg = readLocArg(false,false);
			if (arg != null) {
				list_arg.add(arg);
				if (readIdentifier()) {
					// TODO section external -> syntax error
					do {
						n += "__" + lastString;
						keywords.add(new String(lastString));

						arg = readLocArg(false,false);
						if (arg == null) {
							reporter.syntaxError("Incorrect symbol.", getLine());
							return null;
						}
						list_arg.add(arg);
					} while (readIdentifier());
				}
			}
			slotPosition.setLength(position - start_pos);
			result = new Slot(slotPosition, getString(n), lastSection);
			//
			result.setKeywordList(keywords.toArray(new String[keywords.size()]));
			result.setBody(slotBody);
			//
		}
		return result;
	}  

	//++ LOC_ARG      -> identifier ':' TYPE
	//++               | '(' LOCAL ')'
	public IArgument readLocArg(boolean mute, boolean selfFirst) {
		IArgument result=null;

		if ((selfFirst && readThisKeyword(ILisaacModel.variable_self)) ||
				(! selfFirst && readIdentifier())) {

			int startPos = position;

			//Position pos = getPosition(); 
			String n = new String(lastString);
			if (readCharacter(':') && lastCharacter() != '=') {
				ITypeMono t = readType(true);
				if (t == null) {
					reporter.syntaxError("Incorrect type.", getLine());
					return null;
				}

				// TODO SELF
				/*if (selfFirst && (t != TypeSimple.getTypeSelf()) &&
						((object.name != ALIAS_STR.prototype_block) || 
					            {tb ?= t; tb = NULL})) {
					reporter.syntaxError("Type `SELF' is needed.", getPosition());
				}*/
				Position p = getPosition();
				p.offset = startPos;
				result = new ITMArgument(n, t, p);
			} else {
				if (! mute) {
					reporter.warning("Added ':' is needed.", getLine());
				}
			}
		} else if (readCharacter('(')) {
			result = readLocalArg(mute, selfFirst);
			if (result == null) {
				if (! mute) {
					reporter.syntaxError("Incorrect argument definition.", getLine());
					return null;
				}
			} else {
				if (! readCharacter(')')) {
					reporter.warning("Added ')'.", getPosition());
				}
			}
		}	
		return result;
	}

	private IArgument readLocalArg(boolean m, boolean s) {
		IArgument result=null;
		boolean mute = m;
		int startPos;

		int firstPos = position;

		if ((s && readThisKeyword(ILisaacModel.variable_self)) ||
				readIdentifier()) {
			List<String> name = new ArrayList<String>();
			List<ITypeMono> type = new ArrayList<ITypeMono>();
			int beg = 0;


			do {
				if (name.size() != 0 && !readIdentifier() && !mute) {
					reporter.syntaxError("Incorrect argument identifier.", getLine());
					return null;
				}

				startPos = position;

				name.add(lastString);
				if (readCharacter(':') && lastCharacter() != '=') {
					mute = false;
					ITypeMono t = readType(true);

					if (t == null) {
						reporter.syntaxError("Incorrect argument type.", getLine());
						return null;
					}
					for (int i=beg; i<name.size(); i++) {
						type.add(t);
					}
					beg = name.size();
				}
			} while (readCharacter(','));

			if (beg != name.size()) {
				if (! mute) {
					reporter.syntaxError("Incorrect argument type.", getLine());
					return null;
				}
				// free arrays..
			} else {
				/*if (s && (
						type.get(0) != TypeSimple.getTypeSelf() ||
						)) {
					// TODO  syntax_error (current_position,"Type `SELF' is needed.");
				}*/

				if (name.size() == 1) {
					// Single Argument.
					Position p = new Position(0,0,startPos, 0);

					result = new ITMArgument(name.get(0), type.get(0), p);

					// free arrays
				} else {
					// Vector Arguments.
					// alias arrays...
					TypeMulti tm = new TypeMulti(type.toArray(new ITypeMono[type.size()]));
					result = new ITMArgs(name.toArray(new String[name.size()]), tm, getPosition(position - firstPos));
				}
			}
		}
		return result;
	}

	private Slot readSlotOperator(ArrayList<IArgument> list_arg) { 
		Slot result=null;
		String associativity=null;
		int priority=0;
		Position slotPosition = getPosition();
		Position slotBody = getPosition();

		if (! readOperator()) {
			reporter.syntaxError("Operator is needed.", getLine());
			return null;
		}
		if (lastString.equals(ILisaacModel.symbol_equal) ||
				lastString.equals(ILisaacModel.symbol_not_equal)) {
			reporter.syntaxError("Incorrect operator.", getLine());
			return null;
		}
		String name = new String(lastString);
		slotPosition.setLength(name.length());

		if (! readCharacter('\'')) {
			reporter.warning("Added `''.", getLine());
		}
		if (readThisKeyword(ILisaacModel.keyword_left) ||
				readThisKeyword(ILisaacModel.keyword_right)) {
			associativity = new String(lastString);
			if (readInteger()) {
				priority = (int) lastInteger;
			}
		}
		if (list_arg.isEmpty()) {
			// Prefix operator.
			IArgument arg = readLocArg(false, true);
			if (arg == null) {
				reporter.syntaxError("Operator declaration invalid.", getLine());
				return null;
			}
			if (arg != null) {
				list_arg.add(arg);
			}
			name = getOperator("__prefix", name);
			if (associativity != null) {
				reporter.syntaxError("No associativity for postfix operator.", getLine());
			}
		} else {
			IArgument arg = readLocArg(false,false);
			if (arg != null) {
				// Infix operator.
				list_arg.add(arg);
				name = getOperator("__infix", name);
				if (associativity == null) {
					associativity = ILisaacModel.keyword_left;
				}
			} else {
				// Postfix operator.
				name = getOperator("__postfix", name);
				if (associativity != null) {
					reporter.syntaxError("No associativity for prefix operator.", getLine());
				}
			}
		}
		result = new Slot(slotPosition, name, lastSection);
		result.setAssociativity(associativity, priority);
		result.setBody(slotBody);

		return result;
	} 

	private String getOperator(String typ, String op) {
		StringBuffer s = new StringBuffer(typ);


		for (int i=0; i<op.length(); i++) {
			char c = op.charAt(i);
			switch (c) {
			case '+': s.append("_add"); break;
			case '-': s.append("_sub"); break;
			case '~': s.append("_logicnot"); break;
			case '!': s.append("_not"); break;
			case '/': s.append("_div"); break;
			case '*': s.append("_mul"); break;
			case '^': s.append("_xor"); break;
			case '%': s.append("_mod"); break;
			case '>': s.append("_greater"); break;
			case '<': s.append("_less"); break;
			case '=': s.append("_equal"); break;
			case '\\': s.append("_notdiv"); break;
			case '|': s.append("_or"); break;
			case '&': s.append("_and"); break;
			case '$': s.append("_dollar"); break;
			case '#': s.append("_diese"); break;
			case '@': s.append("_at"); break;
			case '?': s.append("_ask"); break;
			}
		}
		return getString(s.toString()); // alias string
	}

	//++ EXPR         -> { ASSIGN !!AMBIGU!! affect } EXPR_OPERATOR
	//++ ASSIGN       -> '(' IDF_ASSIGN { ',' IDF_ASSIGN } ')'
	//++               | IDF_ASSIGN
	//++ IDF_ASSIGN   -> identifier { identifier }
	public ICode readExpr() {
		ICode result=null;
		boolean again;
		String string_tmp2="";

		// !! AMBIGU resolution !!    
		saveContext();

		if (readCharacter('(')) {
			ArrayList<String> l_assignment = new ArrayList<String>();
			do {
				again = false;
				if (readIdentifier()) {
					//p = position - lastString.length();

					string_tmp2 = new String(lastString);
					while (readIdentifier()) {
						string_tmp2 += "__" + lastString;
					}
					String name = getString(string_tmp2);
					l_assignment.add(name);

					if (readCharacter(',')) {
						again = true;
					}
				}
			} while(again);

			if (!l_assignment.isEmpty() && readCharacter(')') && readAffect()) {
				result = new ITMListIdf(l_assignment);

				char affect = lastString.charAt(0);
				ICode value = readExpr();
				if (value == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return null;
				}
				if (affect == '<') {
					reporter.syntaxError("Impossible '<-' style assignment with vector.", getPosition(lastString.length()));
					return null;
				}
				result = new ITMWrite(result, value, affect);
			} else {
				// FREE l_assignment
			}
		} else if (readIdentifier()) {
			//p = position - lastString.length();
			string_tmp2 = new String(lastString);
			while (readIdentifier()) {
				string_tmp2 += "__" + lastString;
			}
			String name = getString(string_tmp2);

			if (readAffect()) {
				result = new ITMRead(name);

				char affect = lastString.charAt(0);
				ICode value = readExpr();
				if (value == null) {
					reporter.syntaxError("Incorrect expression.", getLine());
					return null;
				}
				result = new ITMWrite(result, value, affect);
			}
		}
		if (result == null) {
			restoreContext();
			result = readExprOperator();
		}
		return result;
	}

	//++ EXPR_OPERATOR-> { operator } EXPR_MESSAGE { operator {operator} EXPR_MESSAGE } {operator}
	private ICode readExprOperator() {
		ICode result=null;
		int first_msg,last_msg;

		ArrayList<ICode> l_expr = new ArrayList<ICode>();
		while (readOperator()) {
			ICode expr = new ITMOperator(new String(lastString));
			l_expr.add(expr);
		}
		ICode expr = readExprMessage();
		if (expr == null) {
			// Error.
			if (l_expr.size() > 0) {
				reporter.syntaxError("Incorrect expression.", getLine());
			}
			// free l_expr
		} else {
			// { operator {operator} EXPR_MESSAGE } {operator}
			first_msg = l_expr.size();
			do {
				last_msg = l_expr.size();
				l_expr.add(expr);
				if (readOperator()) {
					do {
						expr = new ITMOperator(new String(lastString));
						l_expr.add(expr);
					} while (readOperator());

					expr = readExprMessage();
				} else {
					expr = null;
				}
			} while (expr != null);

			// Last Post-fix operator.
			while (last_msg < l_expr.size()-1) {
				ITMOperator itm_op = (ITMOperator) l_expr.get(last_msg+1);
				expr = new ITMReadArg1(getOperator("__postfix", itm_op.getName()), l_expr.get(last_msg));

				l_expr.set(last_msg, expr);
				l_expr.remove(last_msg+1);
			}
			if (last_msg - first_msg < 3) {
				// First Pre-fix operator.
				while (first_msg != 0) {
					ITMOperator itm_op = (ITMOperator) l_expr.get(first_msg - 1);
					expr = new ITMReadArg1(getOperator("__prefix", itm_op.getName()), l_expr.get(first_msg));

					l_expr.add(first_msg, expr);
					first_msg = first_msg - 1;
					l_expr.remove(first_msg);
				}
			}
			if (l_expr.size() == 1) {
				result = l_expr.get(0);// first
				// free l_expr
			} else if (l_expr.size() == 3) {
				// Simple binary message.
				ITMOperator itm_op = (ITMOperator) l_expr.get(1);// second
				result = new ITMReadArg2(getOperator("__infix", itm_op.getName()),
						l_expr.get(0),
						l_expr.get(2));
				// free l_expr
			} else {
				// Complex expression.
				result = new ITMExpression(l_expr.toArray(new ICode[l_expr.size()]));
			}
		}
		return result;
	}

	//++ EXPR_MESSAGE -> EXPR_BASE { '.' SEND_MSG }
	protected ICode readExprMessage() {

		ICode result = readExprBase();
		if (result != null) {
			while (readCharacter('.')) {
				result = readSendMsg(result);
				if (result == null) {
					reporter.syntaxError("Incorrect message.", getLine());
					return null;
				}
			}
		}
		return result;
	}

	//++ EXPR_BASE    -> "Old" EXPR
	//++               | EXPR_PRIMARY
	//++               | SEND_MSG
	public ICode readExprBase() {
		ICode result=null;

		if (readThisKeyword(ILisaacModel.keyword_old)) {
			ICode old_value = readExpr();
			if (old_value == null) {
				reporter.syntaxError("Incorrect `Old' expression.", getLine());
				return null;
			}
			result = new ITMOld(old_value);
		} else {
			result = readExprPrimary();
			if (result == null) {
				result = readSendMsg(null);
			}
		}
		return result;
	}

	//++ EXPR_PRIMARY -> "Self"
	//++               | result
	//++               | PROTOTYPE
	//++               | CONSTANT
	//++               | '(' GROUP ')'
	//++               | '{' [ LOC_ARG ';' !! AMBIGU!! ] GROUP '}'
	//++               | external [ ':' ['('] TYPE ['{' TYPE_LIST '}'] [')'] ]
	public ICode readExprPrimary() {
		ICode result=null;
		String result_id=null;
		ITypeMono type=null;
		ITMList group_sav=null;

		readSpace();
		Position pos = getPosition();
 
		if (readThisKeyword(ILisaacModel.variable_self)) {
			result = new ITMRead(new String(lastString));
		} else if (readThisKeyword(ILisaacModel.keyword_result)) {
			if (lastCharacter() == '_') {
				position = position + 1;
				string_tmp = "" + ILisaacModel.keyword_result + "_";

				while (Character.isDigit(lastCharacter())) {
					string_tmp += lastCharacter();
					position = position + 1;
				}
				if (string_tmp.length() <= 0) {
					reporter.syntaxError("Incorrect Result number.", getLine());
				}
				result_id = getString(string_tmp);
			} else {
				result_id = ILisaacModel.keyword_result;
			}
			result = new ITMRead(result_id);

		} else if ((type = readPrototype(null)) != null) {
			result = new ITMPrototype(type, pos);

		} else if ((result = readConstant()) != null) {
		} else if (readCharacter('(')) {
			group_sav = lastGroup;
			lastGroup = new ITMList(lastSlot, position);
			result = lastGroup;

			lastGroup.setCode(readGroup());
			if (! readCharacter(')')) {
				reporter.syntaxError("Added ')'.", getLine());
				return null;
			}
			lastGroup.setEndOffset(position);
			lastGroup = group_sav;
		} else if (readCharacter('{')) {
			group_sav = lastGroup;  
			lastGroup = new ITMList(lastSlot, position);

			saveContext(); // !! SAVE CONTEXT !!

			//
			IArgument arg = readLocArg(true,false);
			//
			if (arg != null) {
				if (! readCharacter(';')) {
					reporter.syntaxError("Added ';'.", getLine());
					return null;
				}
			} else {
				restoreContext(); // !! RESTORE CONTEXT !!
			}
			result = new ITMBlock(lastGroup, arg, lastSlot);

			lastGroup.setCode(readGroup());
			if (! readCharacter('}')) {
				reporter.syntaxError("Added '}'.", getLine());
				return null;
			}
			lastGroup.setEndOffset(position);
			lastGroup = group_sav; 
		} else if (readExternal()) {
			if (! readCharacter(':')) {
				result = new ITMExternal(new String(lastString));
			} else {
				boolean persistant = readCharacter('(');
				ITMExternalType ext = new ITMExternalType(new String(lastString), persistant);
				type = readType(false);
				if (type == null) {
					reporter.syntaxError("Incorrect type.", getLine());
					return null;
				}
				ext.setType(type);
				if (readCharacter('{')) {
					ITypeMono[] ltype = readTypeList(false);
					if (ltype == null) {
						reporter.syntaxError("Incorrect live type list.", getLine());
						return null;
					}
					if (! readCharacter('}')) {
						reporter.syntaxError("Added '}'.", getLine());
						return null;
					}
					ext.setTypeList(ltype);
				}
				if (ext.isPersistant() && (! readCharacter(')'))) {
					reporter.syntaxError("Added '}'.", getLine());
					return null;
				}
				result = ext;
			}
		}
		return result;
	}

	//++ CONSTANT     -> integer
	//++               | real
	//++               | characters
	//++               | string
	private IConstant readConstant() {
		IConstant result=null;

		if (readReal()) {
			result = new ITMReal(new String(lastReal));
		} else if (readInteger()) {
			result = new ITMNumber(lastInteger);
		} else if (readCharacters()) {
			result = new ITMCharacter(new String(lastString));
		} else if (readString()) {
			result = new ITMString(new String(lastString));
		}
		return result;
	}

	//++ GROUP        -> DEF_LOCAL {EXPR ';'} [ EXPR {',' {EXPR ';'} EXPR } ]
	private ICode[] readGroup() {		
		readDefLocal();

		ArrayList<ICode> result = new ArrayList<ICode>();
		ICode e = readExpr();
		while (e != null && readCharacter(';')) {
			result.add(e);
			e = readExpr();
		}
		if (e != null) {
			if (readCharacter(',')) {
				do {
					e = new ITMResult(e);
					result.add(e);
					e = readExpr();
					while (e != null && readCharacter(';')) {
						result.add(e);
						e = readExpr();
					}
					if (e == null) {
						reporter.syntaxError("Incorrect multiple result expression.", getLine());
						return null;
					}
				} while (readCharacter(','));
			}
			e = new ITMResult(e);
			result.add(e);
		}
		return result.toArray(new ICode[result.size()]);
	}

	//++ DEF_LOCAL    -> { style LOCAL ';' } !! AMBIGU !!
	private void readDefLocal() {
		List<ITMLocal> loc_lst;

		saveContext(); // !! SAVE CONTEXT !!

		char style = readStyle();
		ArrayList<ITMLocal> local_list = new ArrayList<ITMLocal>();
		ArrayList<ITMLocal> static_list = new ArrayList<ITMLocal>();

		while (style != ' ') {
			loc_lst = readLocal(true);
			if (loc_lst != null) {
				if (style == '+') {
					local_list.addAll(loc_lst);
				} else {
					static_list.addAll(loc_lst);
				}
				if (! readCharacter(';')) {
					reporter.syntaxError("Added ';'.", getLine());
				}
				saveContext(); // !! SAVE CONTEXT !!

				style = readStyle();
			} else {
				restoreContext(); // !! RESTORE CONTEXT !!
				style = ' ';
			}
		}
		if (local_list.isEmpty()) {
			// free local_list
		} else {
			lastGroup.setLocalList(local_list.toArray(new ITMLocal[local_list.size()]));
		}
		if (static_list.isEmpty()) {
			// free static_list
		} else {
			lastGroup.setStaticList(static_list.toArray(new ITMLocal[static_list.size()]));
		}
	}

	//++ SEND_MSG     -> identifier [ ARGUMENT { identifier ARGUMENT } ]
	public ICode readSendMsg(ICode firstArg) {
		ICode result=null;

		if (readIdentifier()) {
			//
			// Classic Message.
			//
			String n = getString(lastString);// create alias

			// Argument list.
			LinkedList<ICode> l_arg = new LinkedList<ICode>();
			ICode arg = readArgument();
			if (arg != null) {
				l_arg.addLast(arg);
				while (readIdentifier()) {
					n += "__" + lastString; // FIXME: alias pb
					arg = readArgument();
					if (arg == null) {
						reporter.syntaxError("Incorrect argument.", getLine());
						return null;
					}
					l_arg.addLast(arg);
				}
			}
			String name = getString(n); // FIXME alias pb
			if (l_arg.isEmpty()) {
				if (firstArg == null) {
					// Local ou Implicite Slot without argument.
					result = new ITMRead(name);
				} else {
					result = new ITMReadArg1(name, firstArg);
				}
				// free l_arg
			} else if (l_arg.size() == 1) {
				result = new ITMReadArg2(name, firstArg, l_arg.get(0));
				// free l_arg
			} else {
				l_arg.addFirst(firstArg);
				result = new ITMReadArgs(name, l_arg.toArray(new ICode[l_arg.size()]));
			}
		}	
		return result;
	}

	//++ ARGUMENT     -> EXPR_PRIMARY
	//++               | identifier
	private ICode readArgument() {
		ICode result = readExprPrimary();
		if (result == null && readIdentifier()) {
			result = new ITMRead(new String(lastString));
		}
		return result;
	}

	//++ LOCAL        -> { identifier [ ':' TYPE ] ',' } identifier ':' TYPE
	private List<ITMLocal> readLocal(boolean m) {
		List<ITMLocal> result=null;
		int beg = 0;

		boolean mute = m;
		if (readIdentifier()) {
			result = new LinkedList<ITMLocal>();			
			do {
				if (result.size() != 0 && !readIdentifier() && !mute) {
					reporter.syntaxError("Incorrect identifier.", getLine());
					return null;
				}
				ITMLocal loc = new ITMLocal(new String(lastString), getPosition());
				result.add(loc);
				if (readCharacter(':') && lastCharacter() != '=') {
					mute = false;
					ITypeMono t = readType(false);
					if (t == null) {
						reporter.syntaxError("Incorrect local type.", getLine());
						return null;
					}
					for (int j=beg; j<result.size(); j++) {
						result.get(j).setType(t);
					}
					beg = result.size(); // upper+1
				}
			} while(readCharacter(','));
			if (beg != result.size()) {
				if (mute) {
					// free result
					result = null;
				} else {
					reporter.syntaxError("Incorrect local type.", getLine());
					return null;
				}
			} else {
			}
		}	
		return result;
	}

	public boolean readRequire() {
		boolean result=false;

		ITMList lst = readContract();
		if (lst != null) {
			// lastSlot.setRequire lst
			result = true;
		}
		return result;
	}

	public boolean readEnsure() {
		boolean result=false;

		ITMList lst = readContract();
		if (lst != null) {
			// lastSlot.setEnsure lst
			result = true;
		}
		return result;
	}

	//++ CONTRACT     -> '[' DEF_LOCAL { ( EXPR ';' | "..." ) } ']'
	private ITMList readContract() {
		ITMList result = null;

		if (readCharacter('[')) {
			result = new ITMList(lastSlot, position);
			lastGroup = result;

			readDefLocal();

			ArrayList<ICode> lst = new ArrayList<ICode>();
			boolean doContinue = false;
			do {
				ICode e = readExpr();
				if (e == null) {
					doContinue = readWord(ILisaacModel.keyword_ldots);
					if (doContinue) {
						lst.add(new ITMLDots());
					}
				} else {
					lst.add(e);
					if (! readCharacter(';')) {
						reporter.syntaxError("Added ';'.", getLine());
						return null;
					}
					doContinue = true;
				}
			} while (doContinue);

			if (! readCharacter(']')) {
				reporter.syntaxError("Added ']'.", getLine());
				return null;
			}
			// TODO lst add prototype void
			result.setCode(lst.toArray(new ICode[lst.size()]));
			result.setEndOffset(position);
		}
		return result;
	}	   

	public boolean skipUntilThisKeyword(String st) {
		int idx;
		int posold;
		boolean result=false;

		while (! isEOF() && ! result) {
			idx = 0;
			while ((readSpace() || lastCharacter() == '\n') && lastCharacter() != st.charAt(idx)) {
				position++;
			}
			posold = position;
			position++;
			idx++;
			if (! isEOF()) {
				while (idx <= st.length()-1 && lastCharacter() == st.charAt(idx)) {
					position++;
					idx++;
				}
				if (idx > st.length()-1) {
					lastString = st;
					position = posold;
					result = true;
				}
			}
		}
		return result;
	}

	public String readSlotNameFromOffset(int offset, boolean modifyCurrentOffset) {
		String result=null;
		int oldPosition = position;
		position = offset;

		//++ TYPE_SLOT    -> [ LOC_ARG '.' ] identifier [ LOC_ARG { identifier LOC_ARG } ]
		//++               | [ LOC_ARG ] '\'' operator '\'' [("Left"|"Right") [integer]] [LOC_ARG]
		if (! skipLocalArg(true)) {
			if (readCharacter('\'')) {
				result = readSlotNameOperator();
			} else {
				result = readSlotNameKeyword();
			}
		} else {
			if (readCharacter('.')) {
				result = readSlotNameKeyword();
			} else if (readCharacter('\'')) {
				result = readSlotNameOperator();
			}
		}
		if (! modifyCurrentOffset) {
			position = oldPosition;
		}
		return result;
	}

	//++ LOC_ARG      -> identifier ':' TYPE
	//++               | '(' LOCAL ')'
	public boolean skipLocalArg(boolean selfFirst) {
		boolean result=false;
		if ((selfFirst && readThisKeyword(ILisaacModel.variable_self)) ||
				(! selfFirst && readIdentifier())) {
			if (readCharacter(':') && lastCharacter() != '=') {
				return skipType();
			}
		} else if (readCharacter('(')) {
			result = skipLocal(selfFirst);
			if (! result) {
				return false;
			} else {
				if (! readCharacter(')')) {
					return false;
				}
				result = true;
			}
		}	
		return result;
	}

	private boolean skipLocal(boolean s) {
		boolean result = false;

		if ((s && readThisKeyword(ILisaacModel.variable_self)) ||
				readIdentifier()) {
			int size = 0;
			do {
				if (size != 0 && !readIdentifier()) {
					return false;
				}
				size++;
				if (readCharacter(':') && lastCharacter() != '=') {
					if (!skipType()) {
						return false;
					}
					result = true;
				}
			} while (readCharacter(','));
		}
		return result;
	}

	private String readSlotNameKeyword() {
		String result=null;

		if (readIdentifier()) {
			result = new String(lastString);

			if (skipLocalArg(false)) {
				if (readIdentifier()) {
					do {
						result += "__" + lastString;
						if (! skipLocalArg(false)) {
							return null;
						}
					} while (readIdentifier());
				}
			}
		}
		return result;
	}  

	private String readSlotNameOperator() { 
		String result=null;

		if (! readOperator()) {
			return null;
		}
		result = new String(lastString);
		result = getOperator("__infix", result);// TODO fix!! prefix postfix

		return result;
	} 

	//++ TYPE         -> '{' [ (TYPE | '(' TYPE_LIST ')') ';' ] [ TYPE_LIST ] '}'
	//++               | [type] PROTOTYPE [ CONTRACT ]
	public boolean skipType () {
		boolean result=false;

		if (readCharacter('{')) {
			// '{' [ (TYPE | '(' TYPE_LIST ')') ';' ] [ TYPE_LIST ] '}' 
			if (readCharacter('(')) {
				// Read vector argument.
				result = skipTypeList();
				if (! result) {
					return false;
				}
				if (! readCharacter(')')) {
					return false;
				}
				if (! readCharacter(';')) {
					return false;
				}
				// Read result type.
				result = skipTypeList();
			} else {

				result = skipTypeList();
				if (result) {
					if (readCharacter(';')) {
						// Read result type.
						result = skipTypeList();
					}
				}
			}
			if (! readCharacter('}')) {
				return false;
			}
			result = true;
		} else {
			// Expanded | Strict 
			if (readThisKeyword(ILisaacModel.keyword_expanded) ||
					readThisKeyword(ILisaacModel.keyword_strict)) {
			}
			// PROTOTYPE
			result = skipPrototype();
			// TODO read contract
		}
		return result;
	}

	//++ TYPE_LIST    -> TYPE { ',' TYPE }
	public boolean skipTypeList() {
		boolean result=false;

		result = skipType();
		if (result) {
			while (readCharacter(',')) {
				result = skipType();
				if (! result) {
					return false;
				}
			}
		}
		return result;
	}

	//++ PROTOTYPE    -> cap_identifier{('.'|'...')cap_identifier}['('PARAM_TYPE{','PARAM_TYPE}')']
	public boolean skipPrototype() {
		boolean result=false;

		if (readCapIdentifier()) {
			// TODO syntax {('.'|'...')cap_identifier}
			if (readCharacter('(')) {
				//
				// Genericity.
				//
				do {
					if (! skipParamType()) {
						return false;
					}
				} while (readCharacter(','));
				if (! readCharacter(')')) {
					return false;
				}
				result = true;
			} else {
				// Simple type.	 
				result = true;
			}
		}
		return result;
	}

	//++ PARAM_TYPE   -> TYPE
	//++               | CONSTANT
	//++               | identifier
	private boolean skipParamType() {
		if (! skipType()) {
			// TODO compiler not yet implemented
			return false;
		}
		return true;
	}

	//++ SEND_MSG     -> identifier [ ARGUMENT { identifier ARGUMENT } ]
	public String readKeywordInSendMsg(String keyword, int keywordOffset) {
		String result=null;
		boolean keywordFound=false;

		if (readIdentifier()) {
			//
			// Classic Message.
			//
			String n = getString(lastString);// create alias
			if (n.compareTo(keyword) == 0 && position == keywordOffset+keyword.length()) {
				keywordFound = true;
			}
			// Argument list.
			ICode arg = readArgument();
			if (arg != null) {
				while (readIdentifier()) {
					if (lastString.compareTo(keyword) == 0 && position == keywordOffset+keyword.length()) {
						keywordFound = true;
					}
					n += "__" + lastString; // FIXME: alias pb
					arg = readArgument();
					if (arg == null) {
						reporter.syntaxError("Incorrect argument.", getPosition());
						return null;
					}
				}
			}
			// return slot full name
			result = getString(n); // FIXME alias pb
		}	
		if (! keywordFound) {
			result = null;
		}
		return result;
	}

	/**
	 * Read the next context in lisaac code.
	 * @return Context at parser position
	 */
	public ILisaacContext readContext() {
		readSpace();

		int old_pos = position;

		//
		// Try read Section Context.
		//    
		if (readThisKeyword (ILisaacModel.keyword_section)) {
			position = old_pos;
			return sectionContext;
		}

		//
		// Try read Slot Context.
		//
		if (readCharacter('-') || readCharacter('+')) {
			position = old_pos;
			return slotContext;
		}
		// restore old position (unread)
		position = old_pos;

		if (position >= source.length()-1) {
			return null;
		} else {
			// error
			reporter.syntaxError("Syntax error", getLine());
			return sectionContext.getNextContext(); // go to next section
		}
	}
}
