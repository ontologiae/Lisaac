package org.lisaac.ldt.model.types;

import org.lisaac.ldt.model.ILisaacModel;

/**
 * Type block definition
 */
public class TypeBlock implements ITypeMono {

	//private static List<TypeBlock> dico;
	
	protected IType typeArgument;
	protected IType typeResult;
	
	TypeBlock(IType typeArgument, IType typeResult) {
		this.typeArgument = typeArgument;
		this.typeResult = typeResult;
	}
	
	public static TypeBlock get(IType typ_arg, IType typ_res) {
		
		return new TypeBlock(typ_arg, typ_res);
		
		/*TypeBlock result=null;
		
		if (dico == null) {
			dico = new ArrayList<TypeBlock>();
		}
		int idx = 0;
		while (idx < dico.size() && 
				(!dico.get(idx).getTypeArg().equals(typ_arg) || !dico.get(idx).getTypeRes().equals(typ_res))) {
			idx++;
		}
		if (idx <= dico.size()-1) {
			result = dico.get(idx);
		} else {
			result = new TypeBlock(typ_arg, typ_res);
			dico.add(result);
		}
		return result;*/
	}

	public IType getTypeArg() {
		return typeArgument;
	}
	public IType getTypeRes() {
		return typeResult;
	}
	
	public String toString() {
		return ILisaacModel.prototype_block;
	}

	public void rename(String oldName, String newName) {
		// TODO emit error
	}
}
