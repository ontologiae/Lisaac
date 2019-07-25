package org.lisaac.ldt.model.lip;

public class LIPInteger extends LIPConstant {
	
	protected int value;

	LIPInteger(int i) {
		value = i;
	}

	public static LIPInteger get(int i) {
		// TODO storage..
		return new LIPInteger(i);
	}
	
	public void free() {
		// TODO storage.add_last Self;
	}

	public String getName() {
		return "INTEGER";
	}
}
