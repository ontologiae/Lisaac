package org.lisaac.ldt.model.types;

public class TypeGeneric extends TypeStyle {

	protected ITypeMono[] listType;
	
	public TypeGeneric(String name, String style, ITypeMono[] lt) {
		super(name, style);
		listType = lt;
	}
	
	public ITypeMono getGenericElt(int index) {
		return listType[index];
	}
}
