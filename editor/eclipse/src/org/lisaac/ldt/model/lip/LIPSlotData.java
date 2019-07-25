package org.lisaac.ldt.model.lip;

public class LIPSlotData extends LIPCode {

	protected String name;
	protected LIPConstant value;
	
	public LIPSlotData(String name, LIPConstant value) {
		this.name = name;
		this.value = value;
		// TODO check double declaration
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(name);
		result.append(":");
		result.append(value.getName());
		return result.toString();
	}

	public void setValue(LIPConstant value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}
}
