package org.lisaac.ldt.model.lip;

import org.lisaac.ldt.model.ILisaacModel;

public class LIPSlotCode extends LIPCode {
	protected String section;
	
	protected String name;
	protected String comment;
	
	protected LIPSlotData argument;
	protected LIPCode[] code;
	
	public LIPSlotCode(String section, String name, LIPSlotData argument, LIPCode[] code) {
		this.section = section;
		this.name = name;
		this.argument = argument;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public boolean isPublic() {
		return section.equals(ILisaacModel.section_public);
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer("  -");
		result.append(name);
		if (argument != null) {
			result.append(" <");
			result.append(argument);
			result.append(">");
		}
		result.append(" :\n");
		if (comment != null) {
			result.append(comment);
		} else {
			result.append("\t Sorry, no comment (see `make.lip').\n");
		}
		return result.toString();
	}

	public void addLastCode(LIPCode instr) { // TODO use array list
		
		LIPCode[] newCode = new LIPCode[code.length + 1];
		System.arraycopy(code, 0, newCode, 0, code.length);
		newCode[newCode.length - 1] = instr;
		
		code = newCode;
	}
}
