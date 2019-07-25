package org.lisaac.ldt.model.items;

import org.lisaac.ldt.model.types.ITypeMono;

public class ITMExternalType extends ITMExternal {

	protected ITypeMono type;
	protected ITypeMono[] typeList;
	protected boolean persistant;
	
	public ITMExternalType(String extern, boolean persistant) {
		super(extern);
		this.persistant = persistant;
	}

	public boolean isPersistant() {
		return persistant;
	}
	
	public ITypeMono getType(Prototype prototype) {
		return type;
	}

	public void setType(ITypeMono type) {
		this.type = type;
	}

	public ITypeMono[] getTypeList() {
		return typeList;
	}

	public void setTypeList(ITypeMono[] typeList) {
		this.typeList = typeList;
	}
	
}
