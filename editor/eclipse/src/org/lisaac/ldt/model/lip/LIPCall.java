package org.lisaac.ldt.model.lip;

public class LIPCall extends LIPCode {

	protected String name;
	protected LIPCode argument;
	
	public LIPCall(String name, LIPCode argument) {
		this.name = name;
		this.argument = argument;
	}
	
}
