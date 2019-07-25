package org.lisaac.ldt.model;

import org.lisaac.ldt.model.items.Prototype;

public class SlotContext implements ILisaacContext {
	private LisaacParser parser;
	
	public SlotContext(LisaacParser parser) {
		this.parser = parser;
	}
	
	//++ SLOT         -> style TYPE_SLOT [':' (TYPE|'('TYPE_LIST')') ][ affect DEF_SLOT ]';'
	public boolean parseDefinition(Prototype prototype) {
		boolean result=false;
		
		result = parser.readSlot(prototype);
		
		return result;
	}
	
	public ILisaacContext getNextContext() {
		// FIXME skip until next slot !!!!
		if (parser.skipUntilThisKeyword(ILisaacModel.keyword_section)) {
			return parser.getSectionContext();
		}
		return null;
	}

}
