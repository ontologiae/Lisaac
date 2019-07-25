package org.lisaac.ldt.model;

import java.util.ArrayList;

import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.items.Section;
import org.lisaac.ldt.model.types.ITypeMono;
import org.lisaac.ldt.model.types.TypeParameter;

public class SectionContext implements ILisaacContext {

	private LisaacParser parser;
	private boolean firstSection;

	public SectionContext(LisaacParser parser) {
		this.parser = parser;
		firstSection = true;
	}

	public boolean parseDefinition(Prototype prototype) {
		Position sectionPosition;

		parser.readSpace();

		// read Section
		if (! parser.readThisKeyword (ILisaacModel.keyword_section)) {
			parser.getReporter().syntaxError("`Section' is needed.", parser.getLine());
			return false;
		}
		sectionPosition = parser.getPosition();

		if (firstSection) {
			//
			// Read Section Header.
			// 
			if (! parser.readThisKeyword (ILisaacModel.section_header)) {
				parser.getReporter().syntaxError("Section `Header' is needed.", parser.getLine());
				return false;
			}    
			firstSection = false;

			if (! readSectionHeaderContent(prototype)) {
				return false;
			}
		} else {
			//
			// Read Other Section.
			//
			if (parser.readKeyword()) {
				String section = parser.getLastString();
				if (section.equals(ILisaacModel.section_inherit) ||
						section.equals(ILisaacModel.section_insert) ||
						section.equals(ILisaacModel.section_interrupt) ||
						section.equals(ILisaacModel.section_private) ||
						section.equals(ILisaacModel.section_public) ||
						section.equals(ILisaacModel.section_mapping) ||
						section.equals(ILisaacModel.section_directory) ||
						section.equals(ILisaacModel.section_external)) {

					Section lastSection = new Section(prototype, section, sectionPosition);
					parser.setLastSection(lastSection);

					if (prototype.getFirstSection() == null) {// section list head
						prototype.setFirstSection(lastSection);
					}

					if (lastSection.isInheritOrInsert() &&
							parser.getLastSlot() != null &&
							! parser.getLastSlot().getSectionId().isInheritOrInsert()) {
						parser.getReporter().syntaxError("`Section Inherit/Insert' must to be first section.", parser.getLine());
						return false;

					} else if (prototype.isExpanded() && section.equals(ILisaacModel.section_inherit)) {
						parser.getReporter().warning("`Section Inherit' is not possible with Expanded object (Use `Section Insert').", parser.getLine());
					}
				} else {
					parser.getReporter().syntaxError("Incorrect type section.", parser.getLine());
					return false;
				}
			} else {
				// TYPE_LIST.
				ITypeMono[] t = parser.readTypeList(true);
				if (t == null) {
					parser.getReporter().syntaxError("Incorrect type section.", parser.getLine());
					return false;
				} 
				parser.setLastSection(new Section(prototype, t, sectionPosition));
			}
			// content of section is out of this context
		}
		return true;
	}

	private boolean readSectionHeaderContent(Prototype prototype) {
		boolean result;
		boolean first=true;

		//
		// Read Slots of Section Header.
		//
		do {
			result = false;

			char style = parser.readStyle();
			if (style != ' ') {
				result = true;

				if (!first && style == '+') {
					parser.getReporter().warning("Incorrect style slot ('-').", parser.getPosition(1));
				}
				if (first) {
					first = false;

					if (parser.readWord(ILisaacModel.slot_name)) {
						//
						// Read `name' slot.
						//
						if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
							parser.getReporter().syntaxError("Added ':='.", parser.getLine());
							return false;
						}
						if (parser.readThisKeyword(ILisaacModel.keyword_expanded) ||
								parser.readThisKeyword(ILisaacModel.keyword_strict)) {
							prototype.setTypeStyle(parser.getLastString());
						}
						prototype.setNameOffset(parser.getOffset());// for refactor

						if (! parser.readCapIdentifier()) {
							parser.getReporter().syntaxError("Prototype identifier is needed.", parser.getLine());
							return false;
						}
						if (parser.getLastString().compareTo(prototype.getName()) != 0) {
							int len = parser.getLastString().length();
							parser.getReporter().syntaxError("Incorrect name (filename != name).", parser.getPosition(len));
						}

						if (parser.readCharacter('(')) {
							//
							// Generic loader.
							//
							if (parser.readIdentifier()) {
								return false;
								// TODO syntax identifier : PROTO
							} else if (parser.readCapIdentifier()) {
								if (! parser.isParameterType) {
									parser.getReporter().syntaxError("Identifier parameter type is needed.", parser.getLine());
								}
								ArrayList<TypeParameter> genlist = new ArrayList<TypeParameter>();
								TypeParameter param = TypeParameter.get(parser.getLastString());
								genlist.add(param);
								
								while (parser.readCharacter(',')) {
									if (! parser.readCapIdentifier()) {
										parser.getReporter().syntaxError("Identifier parameter type is needed.", parser.getLine());
										return false;
									}
									if (! parser.isParameterType) {
										parser.getReporter().syntaxError("Identifier parameter type is needed.", parser.getLine());
									}
									param = TypeParameter.get(parser.getLastString());
									genlist.add(param);
								}
								if (! parser.readCharacter(')')) {
									parser.getReporter().syntaxError("Added ')'.", parser.getLine());
									return false;
								}
								prototype.setGenericList(genlist.toArray(new TypeParameter[genlist.size()]));
							} else {
								parser.getReporter().syntaxError("Identifier parameter type is needed.", parser.getLine());
								return false;
							}
						}
					} else {
						parser.getReporter().syntaxError("Slot `name' must to be first slot.", parser.getLine());
					}
				} else if (parser.readWord(ILisaacModel.slot_export) ||
						parser.readWord(ILisaacModel.slot_import)) {

					// - ("export"|"import") ':=' TYPE_LIST 
					if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
						parser.getReporter().syntaxError("Added ':='.", parser.getLine());
						return false;
					}
					if (parser.readTypeList(false) == null) {
						parser.getReporter().syntaxError("Incorrect type list.", parser.getLine());
						return false;
					}
					//  TODO store export / import

				} else if (parser.readWord(ILisaacModel.slot_external)) {	
					// - "external" ':=' `<code_c>`

					if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
						parser.getReporter().syntaxError("Added ':='.", parser.getLine());
						return false;
					}
					if (! parser.readExternal()) {
						parser.getReporter().syntaxError("Incorrect external.", parser.getLine());
						return false;
					}
				} else if (parser.readWord(ILisaacModel.slot_default)) {
					// '-' "default" ':=' EXPR_PRIMARY

					if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
						parser.getReporter().syntaxError("Added ':='.", parser.getLine());
						return false;
					}
					if (parser.readExprPrimary() == null) {
						parser.getReporter().syntaxError("Incorrect expr.", parser.getLine());
						return false;
					}
					// TODO check double default slot
					// TODO set prototyp default value
				} else if (parser.readWord(ILisaacModel.slot_type)) {
					// '-' "type" ':=' `<type C>`

					if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
						parser.getReporter().syntaxError("Added ':='.", parser.getLine());
						return false;
					}
					if (! parser.readExternal()) {
						parser.getReporter().syntaxError("Incorrect external.", parser.getLine());
						return false;
					}
					// TODO check double type declaration

				} else if (parser.readWord(ILisaacModel.slot_version)) {
					//
					// Read `version' slot.
					//

					// '-' "version" ':=' integer
					if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
						parser.getReporter().syntaxError("Added ':='.", parser.getLine());
						return false;
					}
					if (! parser.readInteger()) {
						parser.getReporter().syntaxError("Incorrect number.", parser.getLine());
						return false;
					}
				} else if (parser.readWord(ILisaacModel.slot_lip)) {

					// '-' lip <- ( { LIP_EXPR ';' } )
					if (! parser.readSymbol(ILisaacModel.symbol_affect_code)) {
						parser.getReporter().warning("Added '<-' is needed.", parser.getLine());
					}
					if (! parser.readCharacter('(')) {
						parser.getReporter().warning("Added '(' is needed.", parser.getLine());
					}
					//
					// LIP interpreter
					//
					LipParser lipParser = new LipParser(parser.getSource(), parser.getOffset());

					while (lipParser.readExpr() != null) {
						// instr.run(); // TODO interpret lip code if needed
						if (! lipParser.readCharacter(';')) {
							parser.getReporter().warning("Added ';' is needed.", parser.getLine());
						}
					}
					if (! lipParser.readCharacter(')')) {
						parser.getReporter().warning("Added ')' is needed.", parser.getLine());
					}
					parser.setPosition(lipParser.getOffset());

				} else if (parser.readWord(ILisaacModel.slot_date) ||
						parser.readWord(ILisaacModel.slot_comment) ||
						parser.readWord(ILisaacModel.slot_author) ||
						parser.readWord(ILisaacModel.slot_bibliography) ||
						parser.readWord(ILisaacModel.slot_language) ||
						parser.readWord(ILisaacModel.slot_copyright) ||
						parser.readWord(ILisaacModel.slot_bug_report)) {
					//						  
					// Read `date', `comment', `author', `bibliography', 
					// `language', `copyright' or `bug_report' slots.
					//

					String headerSlot = new String(parser.getLastString());

					// '-' ("date"|"comment"|"author"|"bibliography"|"language"|"copyright"|"bug_report") 
					// ':=' string
					if (! parser.readSymbol(ILisaacModel.symbol_affect_immediate)) {
						parser.getReporter().syntaxError("Added ':='.", parser.getLine());
						return false;
					}
					parser.readSpace();
					Position slotPosition = parser.getPosition();
					if (! parser.readString()) {
						parser.getReporter().syntaxError("Incorrect string.", parser.getLine());
						return false;
					}
					if (headerSlot.equals(ILisaacModel.slot_comment)) {
						prototype.setHeaderComment(new String(parser.getLastString()));
					} else {
						slotPosition.setLength(parser.getOffset() - slotPosition.offset);
						prototype.addHeaderData(headerSlot,new String(parser.getLastString()), slotPosition);
					}

				} else {
					parser.getReporter().syntaxError("Incorrect slot.", parser.getPosition());
					return false;
				}
				if (! parser.readCharacter(';')) {
					parser.getReporter().warning("Added ';'.", parser.getPosition());
				}
			}
		} while (! parser.isEOF() && result);

		return true;
	}

	public ILisaacContext getNextContext() {
		if (parser.skipUntilThisKeyword(ILisaacModel.keyword_section)) {
			return this;
		}
		return null;
	}
}
