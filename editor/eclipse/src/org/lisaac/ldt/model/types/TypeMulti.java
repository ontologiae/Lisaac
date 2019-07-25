package org.lisaac.ldt.model.types;

import java.util.*;

/**
 * List of type
 */
public class TypeMulti implements IType {

	private static List<TypeMulti> dico;
	
	protected ITypeMono[] listType;

	public TypeMulti(ITypeMono[] lst) {
		listType = lst;
	}

	public ITypeMono[] getTypeList() {
		return listType;
	}
	
	public IType getSubType(int index) {
		return listType[index];
	}
	
	public static IType get(ITypeMono[] lst) {
		TypeMulti result=null;
		
		if (dico == null) {
			dico = new ArrayList<TypeMulti>();
		}
		int idx = 0;
		while (idx < dico.size() && !dico.get(idx).getTypeList().equals(lst)) {
			idx++;
		}
		if (idx <= dico.size()-1) {
			result = dico.get(idx);
		} else {
			result = new TypeMulti(lst);
			dico.add(result);
		}
		return result;
	}
	
	public String toString() {
		return "(...)"; // TODO multi type print
	}

	public void rename(String oldName, String newName) {
		for (int i=0; i<listType.length; i++) {
			listType[i].rename(oldName, newName);
		}
	}
}
