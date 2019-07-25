package org.lisaac.ldt.model.types;

import java.util.HashMap;

public class TypeSelf implements ITypeMono {

	private static HashMap<String,TypeSelf> dico;
	
	protected String staticType;

	TypeSelf(String staticType) {
		this.staticType = staticType;
	}
	
	public static TypeSelf get(String n) {
		TypeSelf result=null;
		
		if (dico == null) {
			dico = new HashMap<String,TypeSelf>();
		}
		result = dico.get(n == null ? "" : n);
		if (result == null) {
			result = new TypeSelf(n);
		}
		return result;
	}
	
	public String getStaticType() {
		return staticType;
	}
	
	public String toString() {      
		return staticType;// ou SELF 
	}

	public void rename(String oldName, String newName) {
		// TODO emit error
	}
}
