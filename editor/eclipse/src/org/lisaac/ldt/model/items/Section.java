package org.lisaac.ldt.model.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lisaac.ldt.model.ILisaacModel;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.types.ITypeMono;
import org.lisaac.ldt.outline.OutlineItem;
import org.lisaac.ldt.outline.OutlineSlot;

public class Section {
	protected Prototype prototype;

	protected String name;
	protected ITypeMono[] typeList;

	protected Section next;

	protected Position position;

	protected LinkedList<Slot> slots;


	public Section(Prototype prototype, String name, Position position) {
		this.name = name;
		this.prototype = prototype;
		this.position = position;
		this.next = null;
	}

	public Section(Prototype prototype, ITypeMono[] typeList, Position position) {
		this.typeList = typeList;
		this.prototype = prototype;
		this.position = position;
		this.next = null;
	}

	public String getName() {
		if (name == null) {
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i<typeList.length; i++) {
				buffer.append(typeList[i].toString());
				if (i != typeList.length-1) {
					buffer.append(", ");
				}
			}
			return buffer.toString();
		}
		return name;
	}

	public ITypeMono[] getTypeList() {
		return typeList;
	}

	public Section getNext() {
		return next;
	}

	public void setNext(Section next) {
		this.next = next;
	}

	public Position getPosition() {
		return position;
	}

	public void addSlot(Slot slot) {
		if (slots == null) {
			slots = new LinkedList<Slot>();
		}
		slots.add(slot);
	}

	//
	// Consultation
	//

	public boolean isMapping() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_mapping);
	}

	public boolean isPrivate() {
		if (name == null) {
			return true;
		}
		return name.equals(ILisaacModel.section_private);
	}

	public boolean isPublic() {
		if (name != null) {
			return name.equals(ILisaacModel.section_public);
		}
		return false;
	}

	public boolean isHeader() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_header);
	}

	public boolean isInherit() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_inherit);
	}

	public boolean isInsert() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_insert);
	}

	public boolean isInheritOrInsert() {
		return isInherit() || isInsert();
	}

	public boolean isInterrupt() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_interrupt);
	}

	public boolean isDirectory() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_directory);
	}

	public boolean isExternal() {
		if (name == null) {
			return false;
		}
		return name.equals(ILisaacModel.section_external);
	}

	public boolean isPrivateStyle() {
		return !isPublic() && typeList == null;
	}

	public Prototype getPrototype() {
		return prototype;
	}

	public List<OutlineItem> getOutlineItems() {
		List<OutlineItem> items = new ArrayList<OutlineItem>();

		if (slots != null) {
			for (int i=0; i<slots.size(); i++) {
				items.add(new OutlineSlot(slots.get(i)));
			}
		}
		return items;
	}
}
