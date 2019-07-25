package org.lisaac.ldt.model.types;

import java.util.HashMap;

/**
 * Parameter type for argument define.
 */
public class TypeParameter extends TypeSimple {

	public TypeParameter(String name) {
		super(name);
	}
	
	public static TypeParameter get(String n) {
		TypeParameter result=null;
		
		if (dico != null && dico.containsKey(n)) {
			result = (TypeParameter) dico.get(n);
		}
		if (result == null) {
			result = new TypeParameter(n);
		}
		return result;
	}
}
