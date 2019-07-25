package org.lisaac.ldt.model.lip;

public class LIPString extends LIPConstant {

	protected String value;

	LIPString(String i) {
		value = i;
	}

	public static LIPString get(String i) {
		// TODO storage..
		return new LIPString(i);
	}

	public void free() {
		// TODO storage.add_last Self;
	}

	public String getName() {
		return "STRING";
	}
}
