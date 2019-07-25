package org.lisaac.ldt.model.lip;

public class LIPIf extends LIPCode {
	
	protected LIPCode condition;
	protected LIPCode[] thenCode;
	protected LIPCode[] elseCode;
	
	public LIPIf(LIPCode condition, LIPCode[] thenCode, LIPCode[] elseCode) {
		this.condition = condition;
		this.thenCode = thenCode;
		this.elseCode = elseCode;
	}
	
}
