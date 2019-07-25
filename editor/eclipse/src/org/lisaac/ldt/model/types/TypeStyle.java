package org.lisaac.ldt.model.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Type with style
 */
public class TypeStyle extends TypeSimple {

	private static List<TypeStyle> dico;
	
	protected String style;
	
	TypeStyle(String name, String style) {
		super(name);
		this.style = style;
	}
	
	public String getStyle() {
		return style;
	}
	
	public static TypeStyle get(String n, String s) {
		TypeStyle result=null;
		
		if (dico == null) {
			dico = new ArrayList<TypeStyle>();
		}
		int idx = 0;
		while (idx < dico.size() && 
				(!dico.get(idx).getName().equals(n) || !dico.get(idx).getStyle().equals(s))) {
			idx++;
		}
		if (idx <= dico.size()-1) {
			result = dico.get(idx);
		} else {
			result = new TypeStyle(n, s);
			dico.add(result);
		}
		return result;
	}
}
