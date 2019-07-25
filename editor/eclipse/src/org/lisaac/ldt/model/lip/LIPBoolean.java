package org.lisaac.ldt.model.lip;

public class LIPBoolean extends LIPConstant {

	private static LIPBoolean lipTrue = new LIPBoolean(true);
	private static LIPBoolean lipFalse = new LIPBoolean(false);
	
	protected boolean value;

	LIPBoolean(boolean i) {
		value = i;
	}

	public static LIPBoolean get(boolean b) {
		if (b) {
			return lipTrue;
		} else {
			return lipFalse;
		}
	}

	public void free() {;
	}

	public String getName() {
		return "BOOLEAN";
	}
}
