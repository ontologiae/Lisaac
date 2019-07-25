package org.lisaac.ldt.model.lip;

public class LIPAffect extends LIPCode {

	protected String name;
	protected LIPCode value;
	
	public LIPAffect(String name, LIPCode value) {
		this.name = name;
		this.value = value;
	}
}
