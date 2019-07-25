package org.lisaac.ldt.model.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.LisaacCompletionParser;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.LisaacParser;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.ITypeMono;
import org.lisaac.ldt.model.types.TypeParameter;
import org.lisaac.ldt.outline.OutlineItem;
import org.lisaac.ldt.outline.OutlinePrototype;
import org.lisaac.ldt.outline.OutlineSection;

public class Prototype {
	protected IFile file;

	protected ILisaacModel parent;
	protected LisaacParser parser;

	protected String name;
	protected String typeStyle;

	protected HashMap<String, Slot> parentList;
	protected HashMap<String, Slot> slotList;

	// genericity
	protected TypeParameter[] genericList;
	
	// hover informations
	protected String headerData;
	protected String headerComment;

	protected Section firstSection;

	// refactor information
	protected int nameOffset;
	protected Position authorOffset;
	protected Position bibliographyOffset;
	protected Position copyrightOffset;

	public Prototype(IFile file, String name, ILisaacModel model) {
		this.file = file;
		this.name = name;
		this.parent = model;
		this.parser = model.getParser();

		slotList = new HashMap<String, Slot>();
		parentList = new HashMap<String, Slot>();
	}

	public ILisaacModel getModel() {
		return parent;
	}

	public boolean setName(String n) {
		if (name != null && name.equals(n)) {
			return false;
		}
		name = n;
		return true;
	}

	public IFile getFile() {
		return file;
	}

	public String getFileName() {
		return file.getName();
	}

	public String getName() {
		return name;
	}

	public int getGenericIndex(TypeParameter param) {
		if (genericList != null) {
			for (int i=0; i<genericList.length; i++) {
				if (genericList[i].equals(param)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public void setGenericList(TypeParameter[] list) {
		genericList = list;
	}
	
	public void setHeaderComment(String comment) {
		this.headerComment = comment;
	}

	public void addHeaderData(String slotName, String data, Position position) {
		String info = "\n<g>" + slotName + "</g> " + data;
		if (headerData == null) {
			headerData = info;
		} else {
			this.headerData += info;
		}
		if (slotName.equals(ILisaacModel.slot_author)) {
			authorOffset = position;
		} else if (slotName.equals(ILisaacModel.slot_bibliography)) {
			bibliographyOffset = position;
		} else if (slotName.equals(ILisaacModel.slot_copyright)) {
			copyrightOffset = position;
		}
	}

	
	public LisaacParser openParser() {
		parser.initialize();
		return parser;
	}

	public void setTypeStyle(String s) {
		typeStyle = s;
	}

	public String getTypeStyle() {
		return typeStyle;
	}

	public Section getFirstSection() {
		return firstSection;
	}

	public void setFirstSection(Section s) {
		firstSection = s;
	}

	public boolean isExpanded() {
		if (typeStyle != null) {
			return typeStyle.equals(ILisaacModel.keyword_expanded)
			|| name.equals(ILisaacModel.prototype_true)
			|| name.equals(ILisaacModel.prototype_false);
		}
		return false;
	}

	public Slot lookupSlot(String n) {
		Slot result = null;

		//
		// Search in 'Self'.
		//
		result = getSlot(n);
		if (result == null) {
			result = getParentSlot(n);
		}

		if (result == null) {
			//
			// Search in parents.
			//
			Collection<Slot> values = parentList.values();
			Iterator<Slot> it = values.iterator();
			while (it.hasNext()) {
				Slot slotParent = it.next();
				IType typeParent = slotParent.getResultType();
				try {
					Prototype parent = LisaacCompletionParser
					.findPrototype("" + typeParent);
					if (parent != null) {
						result = parent.lookupSlot(n);
						if (result != null) {
							return result;
						}
					}
				} catch (CoreException e) {
					return null;
				}
			}
		}
		return result;
	}

	public Slot getSlot(String n) {
		//
		// Search in 'Self' only.
		//
		if (slotList.containsKey(n)) {
			return slotList.get(n);
		}
		return null;
	}

	public Slot getParentSlot(String n) {
		// Search in 'Self' parent.
		if (parentList.containsKey(n)) {
			return parentList.get(n);
		}
		return null;
	}

	public Slot getSlot(int offset) {
		return getSlot(openParser(), offset);
	}

	public Slot getSlot(String s, int offset) {
		return getSlot(new LisaacParser(null, s), offset);
	}

	public Slot getSlot(LisaacParser parser, int offset) {
		//
		// Use indentation to get slot
		//
		String source = parser.getSource();
		boolean again;

		if (offset >= source.length() - 1) {
			return null;
		}

		do {
			again = false;

			// find beginning of line
			while (offset > 0 && source.charAt(offset) != '\n') {
				offset--;
			}
			// look at indentation
			if (offset > 0 && source.length() > 4) {
				if (source.charAt(offset + 1) == ' '
					&& source.charAt(offset + 2) == ' '
						&& (source.charAt(offset + 3) == '+' || source
								.charAt(offset + 3) == '-')) {
					String slotName = parser
					.readSlotNameFromOffset(offset + 4, false);
					if (slotName != null) {
						return getSlot(slotName);
					}
				} else {
					again = true;
					offset--;
				}
			}
		} while (again);

		return null;
	}

	public void lookupSlotMatch(String n,
			ArrayList<ICompletionProposal> matchList, int offset, int length) {
		//
		// Search in 'Self'.
		//
		getSlotMatch(n, matchList, offset, length);
		getParentSlotMatch(n, matchList, offset, length);

		//
		// Search in parents.
		//
		Collection<Slot> values = parentList.values();
		Iterator<Slot> it = values.iterator();
		while (it.hasNext()) {
			Slot slotParent = it.next();
			IType typeParent = slotParent.getResultType();
			try {
				Prototype parent = LisaacCompletionParser.findPrototype(""
						+ typeParent);
				if (parent != null) {
					parent.lookupSlotMatch(n, matchList, offset, length);
				}
			} catch (CoreException e) {
			}
		}
	}

	public void getSlotMatch(String n,
			ArrayList<ICompletionProposal> matchList, int offset, int length) {
		//
		// // Search in 'Self' only.
		//
		Collection<Slot> values = slotList.values();
		Iterator<Slot> it = values.iterator();
		while (it.hasNext()) {
			Slot slot = it.next();
			if (slot.match(n)) {
				slot.getSlotMatchProposals(matchList, offset, length, n
						.length());
			}
		}
	}

	public void getParentSlotMatch(String n,
			ArrayList<ICompletionProposal> matchList, int offset, int length) {
		// Search in 'Self' parent.
		Collection<Slot> values = parentList.values();
		Iterator<Slot> it = values.iterator();
		while (it.hasNext()) {
			Slot slot = it.next();
			if (slot.match(n)) {
				slot.getSlotMatchProposals(matchList, offset, length, n
						.length());
			}
		}
	}

	public Slot getSlotFromKeyword(String keyword, LisaacParser parser,
			int baseOffset) throws CoreException {
		String source = parser.getSource();
		int bracketLevel = 0, invBracketLevel = 0;
		Prototype receiver = null;
		Slot result = null;
		char c = 0;

		int offset = baseOffset;
		if (offset >= source.length() - 1) {
			return null;
		}
		//
		parser.enableErrorReport(false); // turn off error reporting
		//
		while (offset > 0) {
			//
			// find beginning of SEND_MSG grammar rule
			//

			// Rewind to '(' '{' ';' '[' '.' '<-' 'operator'
			c = source.charAt(offset);

			if (c == '\n') {// '//' comments
				offset = unreadSingleLineComment(offset, source);
				continue;
			}
			
			if (c == '(' || c == '{' || c == '[') {
				if (bracketLevel == 0) {
					break;
				}
				bracketLevel--;
				invBracketLevel++;
			}
			if (c == ')' || c == '}' || c == ']') {
				bracketLevel++;
				invBracketLevel--;
			}

			// strings 
			if (c == '\"' || c == '\'' || c == '`') {
				offset = unreadString(c, offset, source);
				continue;
			} 
			
			// ok, we're not in nested statements
			if (bracketLevel == 0 && invBracketLevel == 0) {
				if (c == ';' || c == '.') {
					break;
				}
				// affectation
				if (c == '=' && source.length() - offset > 2) {
					if (source.charAt(offset - 1) == ':'
						|| source.charAt(offset - 1) == '?') {
						break;
					}
				} else if (c == '-' && source.length() - offset > 2) {
					if (source.charAt(offset - 1) == '<') {
						break;
					}
				}
				if (LisaacParser.isOperatorSymbol(c)) {
					if (c == '+' || c == '-') {
						// slot definition
						if (offset - 3 > 0 && source.charAt(offset - 1) == ' ' &&
								source.charAt(offset - 2) == ' ' && 
								source.charAt(offset - 3) == '\n') {
							String slotName = parser
							.readSlotNameFromOffset(offset + 1, false);
							
							parser.enableErrorReport(true);// finish with parser
							
							if (slotName != null) {
								result = lookupSlot(slotName);
								if (result == null) {
									return null;
								}
								if (result.keywordCount() == 1) {
									if (result.getName().compareTo(keyword) == 0) {
										return result;
									}
									return null; // not a keyword
								} else {
									// find keyword next token
									offset = baseOffset;
									while (offset < source.length() - 1
											&& Character
											.isJavaIdentifierPart(source
													.charAt(offset))) {
										offset++;
									}
									// read space
									while (offset < source.length() - 1
											&& Character.isWhitespace(source
													.charAt(offset))) {
										offset++;
									}
									if (source.charAt(offset) != ':') {
										return result;// 'keyword' is a slot keyword
									}
									return null;
								}
							}
						}
					}
					// comments   */
					if (offset - 1 > 0 && c == '/' && source.charAt(offset - 1) == '*')  {
						offset = unreadMultiLineComment(offset, source);
						continue;
					}
					break;
				}
			}
			offset--;
		}
		if (result == null) {
			// Slot Call.

			if (c == '.') {
				int pointOffset = offset;
				offset--;
				bracketLevel = 0;
				invBracketLevel = 0;

				// Rewind to '(' '{' ';' '[' '.' '<-' 'operator'
				while (offset > 0) {
					c = source.charAt(offset);

					if (c == '(' || c == '{' || c == '[') {
						if (bracketLevel == 0) {
							break;
						}
						bracketLevel--;
						invBracketLevel++;
					}
					if (c == ')' || c == '}' || c == ']') {
						bracketLevel++;
						invBracketLevel--;
					}

					// ok, we're not in nested statements
					if (bracketLevel == 0 && invBracketLevel == 0) {
						if (c == ';') {
							break;
						}
						// affectation
						if (c == '=' && source.length() - offset > 2) {
							if (source.charAt(offset - 1) == ':'
								|| source.charAt(offset - 1) == '?') {
								break;
							}
						} else if (c == '-' && source.length() - offset > 2) {
							if (source.charAt(offset - 1) == '<') {
								break;
							}
						}
						if (LisaacParser.isOperatorSymbol(c)) {
							break;
						}
						// strings 
						if (c == '\"' || c == '\'' || c == '`') {
							offset = unreadString(c, offset, source);
							continue;
						} 
					}
					offset--;
				}
				if (offset > 0) {
					LisaacCompletionParser p = new LisaacCompletionParser(
							source, (LisaacModel) getModel());
					receiver = p
					.readReceiver(offset + 1, pointOffset, this);

					offset = pointOffset;
				}
			} else {
				receiver = this;
			}
			parser.setPosition(offset + 1);
			parser.readSpace();
			String slotName = parser.readKeywordInSendMsg(keyword,
					baseOffset);

			if (slotName != null && receiver != null) {
				result = receiver.lookupSlot(slotName);
			}
		}
		//
		parser.enableErrorReport(true); // turn on error reporting
		//
		return result;
	}

	private int unreadSingleLineComment(int offset, String source) {
		int saveOffset;
		
		offset--; // unread '\n'
		saveOffset = offset;
		
		while (offset > 0) {
			char c = source.charAt(offset);
			if (c == '\n') {// no comment in the line
				return saveOffset;
			}
			if (offset - 1 > 0 && c == '/' && source.charAt(offset - 1) == '/')  {
				offset = offset - 2;
				break;
			}
			if (c == '\"' || c == '\'' || c == '`') {
				offset = unreadString(c, offset, source);
				continue;
			}
			offset--; 
		}
		if (offset < 0) {
			offset = 0;
		}
		return offset;
	}

	private int unreadMultiLineComment(int offset, String source) {
		offset -= 2; // unread '*/'
		
		while (offset > 0) {
			char c = source.charAt(offset);
			// read '/*'
			if (offset - 1 > 0 && c == '*' && source.charAt(offset - 1) == '/')  {
				offset = offset - 2;
				break;
			}
			offset--;
		}
		if (offset < 0) {
			offset = 0;
		}
		return offset;
	}
	
	private int unreadString(char type, int offset, String source) {
		char c;
		do {
			offset--;
			c = source.charAt(offset);
		} while (offset > 0 && c != type);
		if (c == type) {
			offset--;
		}
		return offset;
	}
	
	public void addSlot(Slot s) {
		slotList.put(s.getName(), s);
	}

	public void addParentSlot(Slot s) {
		parentList.put(s.getName(), s);
	}

	public String getHoverInformation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<b>");
		buffer.append(name);
		buffer.append("</b>");
		if (headerComment != null) {
			buffer.append("\n" + headerComment);
		}
		if (headerData != null) {
			buffer.append("\n\n" + headerData);
		}
		return buffer.toString();
	}

	public List<OutlineItem> getOutlineItems() {
		List<OutlineItem> sections = new ArrayList<OutlineItem>();

		Section current = firstSection;
		while (current != null) {
			sections.add(new OutlineSection(current));
			current = current.getNext();
		}

		List<OutlineItem> result = new ArrayList<OutlineItem>();
		result.add(new OutlinePrototype(this, sections));
		return result;
	}

	public void getSlotProposals(ArrayList<ICompletionProposal> proposals,
			int offset, int length) {
		Collection<Slot> values = slotList.values();
		Iterator<Slot> it = values.iterator();
		while (it.hasNext()) {
			Slot slot = it.next();
			slot.getSlotProposals(proposals, offset, length);
		}

		values = parentList.values();
		it = values.iterator();
		while (it.hasNext()) {
			Slot slotParent = it.next();
			IType typeParent = slotParent.getResultType();
			try {
				Prototype parent = LisaacCompletionParser.findPrototype(""
						+ typeParent);
				if (parent != null) {
					parent.getSlotProposals(proposals, offset, length);
				}
			} catch (CoreException e) {
			}
		}
	}

	public ArrayList<org.eclipse.jface.text.Position> getPositions() {
		ArrayList<org.eclipse.jface.text.Position> result = new ArrayList<org.eclipse.jface.text.Position>();

		// section positions
		Section current = firstSection;
		while (current != null) {
			Position p = current.getPosition();
			result.add(new org.eclipse.jface.text.Position(p.offset, p.length));

			current = current.getNext();
		}

		// slot positions
		Collection<Slot> values = slotList.values();
		Iterator<Slot> it = values.iterator();
		while (it.hasNext()) {
			Slot slot = it.next();
			Position p = slot.getPositionBody();
			if (p != null) {
				result.add(new org.eclipse.jface.text.Position(p.offset, p.length));
			}
		}
		return result;
	}

	public IPath getWorkspacePath() {
		return file.getFullPath();
	}

	public Change refactorRenameSelf(String newName) {
		TextFileChange change = new TextFileChange("Change 'name' slot in section header", file);

		// rename 'name' slot
		MultiTextEdit edit= new MultiTextEdit();
		edit.addChild(new DeleteEdit(nameOffset, name.length()));
		edit.addChild(new InsertEdit(nameOffset, newName));

		change.setEdit(edit);
		return change;
	}

	public Change refactorRenamePrototype(String oldName, String newName) {
		MultiTextEdit edit= new MultiTextEdit();

		// 1. refactor sections
		Section current = firstSection;
		while (current != null) {
			Position p = current.getPosition();	
			ITypeMono[] typeList = current.getTypeList();
			if (typeList != null) {
				for (int i=0; i<typeList.length; i++) {
					if (typeList[i].toString().compareTo(oldName) == 0) {
						typeList[i].rename(oldName, newName);

						// rename section 
						edit.addChild(new DeleteEdit(p.offset+1, p.length-1));
						edit.addChild(new InsertEdit(p.offset+1, current.getName()+"\n\n  "));

						// for model invariance (other changes in refactor)
						typeList[i].rename(newName, oldName);
					}
				}
			}
			current = current.getNext();
		}

		// 2. refactor slots
		Collection<Slot> values = slotList.values();
		Iterator<Slot> it = values.iterator();
		while (it.hasNext()) {
			Slot slot = it.next();
			TextEdit[] slotEdits = slot.refactorRenamePrototype(oldName, newName);
			if (slotEdits != null) {
				edit.addChildren(slotEdits);
			}
		}
		// create change
		if (edit.getChildrenSize() > 0) {
			TextFileChange change = new TextFileChange("Rename prototype occurences", file);
			change.setEdit(edit);
			return change;
		}
		return null;
	}

	public Change refactorHeader(String author, String bibliography,
			String copyright, String license) {
		MultiTextEdit edit= new MultiTextEdit();

		// update header slots.
		refactorHeaderSlot(ILisaacModel.slot_author, authorOffset, author, edit);
		refactorHeaderSlot(ILisaacModel.slot_bibliography, bibliographyOffset, bibliography, edit);
		refactorHeaderSlot(ILisaacModel.slot_copyright, copyrightOffset, copyright, edit);

		// update license.
		if (license != null) {
			int offset = getOffsetBeforeSection();
			edit.addChild(new DeleteEdit(0, offset-1));
			edit.addChild(new InsertEdit(0, license));
		}

		// create change
		if (edit.getChildrenSize() > 0) {
			TextFileChange change = new TextFileChange("Update Section Header", file);
			change.setEdit(edit);
			return change;
		}
		return null;
	}

	private void refactorHeaderSlot(String name, Position pos, String newValue, MultiTextEdit edit) {
		if (newValue != null) {
			if (pos != null) {
				// slot already exist
				edit.addChild(new DeleteEdit(pos.offset, pos.length));
				edit.addChild(new InsertEdit(pos.offset, "\"" + newValue + "\""));
			} else {
				// create slot
				String slot = "\n  - " + name + " := \"" + newValue + "\";";
				edit.addChild(new InsertEdit(getOffsetAfterName(), slot));
			}
		}
	}

	private int getOffsetAfterName() {
		LisaacParser parser = openParser();
		parser.setPosition(nameOffset);
		parser.readCapIdentifier();
		parser.readCharacter(';');

		return parser.getOffset();
	}

	private int getOffsetBeforeSection() {
		LisaacParser parser = openParser();
		parser.setPosition(0);
		if (parser.readThisKeyword (ILisaacModel.keyword_section)) {
			return parser.getOffset() - 7;
		}
		return 0;
	}

	public void setNameOffset(int offset) {
		nameOffset = offset;
	}

	public void setAuthorOffset(Position p) {
		authorOffset = p;
	}

	public void setBibliographyOffset(Position p) {
		bibliographyOffset = p;
	}

	public void setCopyrightOffset(Position p) {
		copyrightOffset = p;
	}

	public IRegion getRegionAt(int line, int column) {
		LisaacParser parser = openParser();
		parser.readTokenAt(line, column);

		return new Region(parser.getOffset(), parser.getLastString().length());
	}
}
