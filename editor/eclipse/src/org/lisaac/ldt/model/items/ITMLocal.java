package org.lisaac.ldt.model.items;

import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.IType;
import org.lisaac.ldt.model.types.ITypeMono;

/**
 * Local declaration slot
 */
public class ITMLocal implements IVariable {
	protected ITypeMono type;
	protected String name;
	
	protected Position position;
	
	public ITMLocal(String name, Position position) {
		this.position = position;
		this.name = name;
	}
	
	public ITMLocal(ITypeMono type, String name) {
		this.type = type;
		this.name = name;
	}

	public IType getType() {
		return type;
	}
	
	public void setType(ITypeMono type) {
		this.type = type;
	}
	
	public String getHoverInformation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<I>Local</I> : <b>");
		buffer.append(name);
		buffer.append("</b> <g> : ");
		buffer.append(type.toString());
		buffer.append("</g>");
		
		return buffer.toString();
	}

	public Position getPosition() {
		return position;
	}
}
