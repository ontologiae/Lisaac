package org.lisaac.ldt.model.lip;

public class LIPUnary extends LIPCode {

	protected LIPCode value;
	protected char operator;
	
	public LIPUnary(char operator, LIPCode value) {
		this.value = value;
		this.operator = operator;
	}	
	
}
