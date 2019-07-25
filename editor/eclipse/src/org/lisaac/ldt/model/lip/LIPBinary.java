package org.lisaac.ldt.model.lip;

public class LIPBinary extends LIPCode {

	protected LIPCode left, right;
	protected char operator;
	
	public LIPBinary(LIPCode left, char operator, LIPCode right) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}
}
