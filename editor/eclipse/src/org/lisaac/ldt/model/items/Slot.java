	package org.lisaac.ldt.model.items;

	import java.util.ArrayList;
	import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
	import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.editors.ColorManager;
import org.lisaac.ldt.editors.LisaacCompletionProposal;
	import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.LisaacParser;
	import org.lisaac.ldt.model.Position;
	import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.outline.OutlineSlot;

	public class Slot {

		protected Section sectionId;
		protected Position position;

		protected String name;
		protected char style;
		protected char affect; // ':', '?', '<'

		protected IArgument[] argumentList;
		protected IType resultType;

		protected String[] keywordList;

		protected ICode value;

		protected ArrayList<ICode> subLists;

		protected String comment;

		protected Position body;
		
		public Slot(Position position, String name, Section sectionId) {
			this.name = name;
			this.position = position;
			this.sectionId = sectionId;
		}

		public String getName() {
			return name;
		}

		public char getStyle() {
			return style;
		}

		public char getAffect() {
			return affect;
		}

		public IArgument getArgument(int i) {
			return argumentList[i];
		}

		public Section getSectionId() {
			return sectionId;
		}

		public IType getResultType() {
			return resultType;
		}

		public void setArgumentList(IArgument[] argumentList) {
			this.argumentList = argumentList;
		}

		public void setResultType(IType resultType) {
			this.resultType = resultType;
		}

		public void setKeywordList(String[] keywordList) {
			this.keywordList = keywordList;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public void setAffect(char affect) {
			this.affect = affect;
		}

		public Position getPosition() {
			return position;
		}
		
		public Position getPositionBody() {
			return body;
		}

		public void setBody(Position p) {
			body = p;
		}
		
		public int keywordCount() {
			if (keywordList == null) {
				return 0;
			}
			return keywordList.length;
		}

		//
		// Value.
		//

		public void setValue(ICode v) {
			if (affect == '<') {
				value = v;
			} else {
				// TODO not yet implemented
			}
		}

		public ICode getValue() {
			return value;
		}

		public void addSubList(ICode list) {
			if (subLists == null) {
				subLists = new ArrayList<ICode>();
			}
			subLists.add(list);
		}

		//
		// Access associativity & priority level.
		//
		protected int priorityAndLevel;

		public void setAssociativity(String p, int l) {
			if (p == null || p.equals(ILisaacModel.keyword_left)) {
				priorityAndLevel = l;
			} else {
				priorityAndLevel = -l;
			}
		}

		public String getAssociativity() {
			String result;

			if (priorityAndLevel >= 0) {
				result = ILisaacModel.keyword_left;
			} else {
				result = ILisaacModel.keyword_right;
			}
			return result;
		}

		public int getPriority() {
			if (priorityAndLevel < 0) {
				return -priorityAndLevel;
			}
			return priorityAndLevel;
		}

		public void setStyle(char style) {
			this.style = style;
		}

		public boolean hasArgument(String word) {
			return getArgument(word) != null;
		}

		public IArgument getArgument(String word) {
			if (argumentList != null) {
				for (int i = 0; i < argumentList.length; i++) {
					if (argumentList[i].hasName(word)) {
						return argumentList[i];
					}
				}
			}
			return null;
		}

		public boolean hasVariableDefinition(String word, int offset) {
			return getVariableDefinition(word, offset) != null;
		}

		public IVariable getVariableDefinition(String word, int offset) {
			ITMList list;

			if (subLists != null) {
				for (int i = 0; i < subLists.size(); i++) {
					ICode c = subLists.get(i);

					if (c instanceof ITMList) {
						list = (ITMList) c;
						if (list != null && list.isInside(offset)) {
							// list variable
							IVariable var = list.getLocal(word);
							if (var != null) {
								return var;
							}
						}
					} else if (c instanceof ITMBlock) {
						list = ((ITMBlock) c).list;
						IArgument arg = ((ITMBlock) c).argument;

						if (list != null && arg != null
								&& list.isInside(offset) && arg.hasName(word)) {
							// block argument
							return arg;
						}
					}
				}
			}
			return null;
		}

		public IVariable getVariable(String word, int offset) {
			IVariable result = getArgument(word);
			if (result == null) {
				result = getVariableDefinition(word, offset);
			}
			return result;
		}

		public Prototype getPrototype() {
			return sectionId.getPrototype();
		}

		private String getOperatorName() {
			String s = name.substring(2);
			StringBuffer result = new StringBuffer("'");
			int index;
			do {
				index = s.indexOf("_");
				s = s.substring(index + 1);
				if (index != -1) {
					if (s.startsWith("add")) {
						result.append('+');
					} else if (s.startsWith("sub")) {
						result.append('-');
					} else if (s.startsWith("logicnot")) {
						result.append('~');
					} else if (s.startsWith("not")) {
						result.append('!');
					} else if (s.startsWith("div")) {
						result.append('/');
					} else if (s.startsWith("mul")) {
						result.append('*');
					} else if (s.startsWith("xor")) {
						result.append('^');
					} else if (s.startsWith("mod")) {
						result.append('%');
					} else if (s.startsWith("greater")) {
						result.append('>');
					} else if (s.startsWith("less")) {
						result.append('<');
					} else if (s.startsWith("equal")) {
						result.append('=');
					} else if (s.startsWith("notdiv")) {
						result.append('\\');
					} else if (s.startsWith("or")) {
						result.append('|');
					} else if (s.startsWith("and")) {
						result.append('&');
					} else if (s.startsWith("dollar")) {
						result.append('$');
					} else if (s.startsWith("diese")) {
						result.append('#');
					} else if (s.startsWith("at")) {
						result.append('@');
					} else if (s.startsWith("ask")) {
						result.append('?');
					}
				}
			} while (index != -1);

			result.append('\'');
			return result.toString();
		}

		public String getSignature(boolean isCall) {
			if (name.startsWith("__")) {
				return getOperatorName();
			}
			if (keywordList == null || keywordList.length < 1) {
				return name;
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append(keywordList[0]);
			buffer.append(" ");

			int keywordIndex = 1;
			for (int argIndex = 0; argIndex < argumentList.length; argIndex++) {
				if (isCall) {
					buffer.append(argumentList[argIndex].getName());
				} else {
					argumentList[argIndex].printIn(buffer);
				}
				buffer.append(" ");

				if (keywordIndex < keywordList.length) {
					buffer.append(keywordList[keywordIndex]);
					buffer.append(" ");
					keywordIndex++;
				}
			}
			if (!isCall && resultType.toString() != null) {
				buffer.append(" : " + resultType);
			}
			
			return buffer.toString();
		}
		
		public StyledString getStyledSignature(boolean isCall, boolean showProto) {
			ColorManager colors = ColorManager.getDefault();
			StyledString result = new StyledString();
			
			if (name.startsWith("__")) {
				result.append(getOperatorName(), colors.getOperatorStyler());
				return result;
			}
			if (keywordList == null || keywordList.length < 1) {
				result.append(name, colors.getSlotStyler());
				return result;
			}
			result.append(keywordList[0], colors.getSlotStyler());
			result.append(" ");

			int keywordIndex = 1;
			for (int argIndex = 0; argIndex < argumentList.length; argIndex++) {
				if (isCall) { 
					result.append(argumentList[argIndex].getName(), colors.getVariableStyler());
				} else {
					argumentList[argIndex].styledPrintIn(result);
				}
				result.append(" ");

				if (keywordIndex < keywordList.length) {
					result.append(keywordList[keywordIndex], colors.getSlotStyler());
					result.append(" ");
					keywordIndex++;
				}
			}
			if (!isCall && resultType.toString() != null) {
				result.append(" : ");
				result.append(resultType.toString(), colors.getPrototypeStyler());
			}
			if (showProto) {
				result.append("  - ", StyledString.QUALIFIER_STYLER);
				result.append(getPrototype().getName(), StyledString.QUALIFIER_STYLER);
			}
			return result;
		}
		
		public void getSlotProposals(ArrayList<ICompletionProposal> proposals,
				int offset, int length) {

			if (name.startsWith("__")) {
				// no operator in the completion.
				return; 
			}
			Image image = new OutlineSlot(this).getImage();
			String displayString = getSignature(true);
			StyledString styledString = getStyledSignature(false, true);
			
			if (checkUnicity(proposals,styledString.getString())) {
				proposals.add(new LisaacCompletionProposal(displayString, offset, length,
					displayString.length() - 1, image, styledString));
			}
		} 

		public void getSlotMatchProposals(
				ArrayList<ICompletionProposal> proposals, int offset,
				int length, int matchLength) {

			Image image = new OutlineSlot(this).getImage();
			String displayString = getSignature(true);
			StyledString styledString = getStyledSignature(false, true);
			
			if (checkUnicity(proposals,styledString.getString())) {
				displayString = displayString.substring(matchLength);
				proposals.add(new LisaacCompletionProposal(displayString, offset, length,
					displayString.length(), image, styledString));
			}
		}
		
		public void getArgumentMatchProposals(String n,
				ArrayList<ICompletionProposal> matchList, int offset, int length) {
			
			if (argumentList != null) {
				for (int i = 0; i < argumentList.length; i++) {
					argumentList[i].getMatchProposals(n,matchList, offset, length);
				}
			}
		}
		
		public void getLocalMatchProposals(String n,
				ArrayList<ICompletionProposal> matchList, int offset, int length) {
			
			ITMList list;
			if (subLists != null) {
				for (int i = 0; i < subLists.size(); i++) {
					ICode c = subLists.get(i);

					if (c instanceof ITMList) {
						list = (ITMList) c;
						if (list != null && list.isInside(offset)) {
							// list variable
							list.getMatchProposals(n, matchList, offset, length);
						}
					} else if (c instanceof ITMBlock) {
						list = ((ITMBlock) c).list;
						IArgument arg = ((ITMBlock) c).argument;

						if (list != null && arg != null
								&& list.isInside(offset)) {
							// block argument
							arg.getMatchProposals(n, matchList, offset, length);
						}
					}
				}
			}
		}

		// FIXME cannot compare display string now... they're already unique, ex: "slot - SON" and "slot - FATHER"
		public static boolean checkUnicity(ArrayList<ICompletionProposal> proposals, String str) {
			for (int i=0; i<proposals.size(); i++) {
				if (proposals.get(i).getDisplayString().compareTo(str) == 0) {
					return false;
				}
			}
			return true;
		}
		
		public boolean match(String n) {
			return name.startsWith(n);
		}

		public String getHoverInformation() {
			StringBuffer buffer = new StringBuffer("<b>");
			buffer.append(getSignature(false));
			buffer.append("</b> <I>- ");
			buffer.append(getPrototype().getName());
			buffer.append("</I>");
			if (comment != null) {
				buffer.append("\n\n<g>");
				buffer.append(comment);
				buffer.append("</g>");
			}
			return buffer.toString();
		}

		public TextEdit[] refactorRenamePrototype(String oldName, String newName) {
			ArrayList<TextEdit> result = new ArrayList<TextEdit>();
			
			// 1. rename arguments
			for (int i=0; i<argumentList.length; i++) {
				IType type = argumentList[i].getType();
				Position p = argumentList[i].getPosition();
				
				if (p != null && type.toString().compareTo(oldName) == 0) {
					LisaacParser parser = getPrototype().openParser();
					parser.setPosition(p.offset+p.length);
					parser.readCharacter(':');
					parser.readSpace();
					
					int startOffset = parser.getOffset();
					
					result.add(new DeleteEdit(startOffset, oldName.length()));
					result.add(new InsertEdit(startOffset, newName));
				}
			}
			
			// 2. rename result type
			if (resultType != null && resultType.toString() != null) {
				if (resultType.toString().compareTo(oldName) == 0) {
					LisaacParser parser = getPrototype().openParser();
					parser.setPosition(position.offset);
					parser.readSlotNameFromOffset(position.offset, true);
					parser.readCharacter(':');
					parser.readSpace();
					
					int startOffset = parser.getOffset();
					
					result.add(new DeleteEdit(startOffset, oldName.length()));
					result.add(new InsertEdit(startOffset, newName));
				}
			}
			
			// 3. rename code
			if (value != null) {
				value.refactorRenamePrototype(oldName, newName, result);
			}
			
			if (result.size() > 0) {
				return result.toArray(new TextEdit[result.size()]);
			}
			return null;
		}
	}
