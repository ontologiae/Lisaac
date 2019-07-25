package org.lisaac.ldt.outline;

import java.util.List;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.editors.ColorManager;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.items.Section;
import org.lisaac.ldt.model.types.ITypeMono;

public class OutlineSection extends OutlineItem {

	protected Section section;

	protected List<OutlineItem> slots;

	public OutlineSection(Section section) {
		this.section = section;
		this.slots = section.getOutlineItems();

		Position position = section.getPosition();

		fstartOffset = position.getStartOffset() - 7;
		fLength = 7;
		//fstartOffset = position.getStartOffset();
		//fLength = position.length();
	}

	/**
	 * Returns the label which corresponds to the element, null otherwise.
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText() {
		String result="";
		if (section != null) {
			if (section.getName() != null) {
				result = section.getName();
			} else {
				ITypeMono[] types = section.getTypeList();
				for (int i=0; i<types.length; i++) {
					result += types[i];
					if (i != types.length-1) {
						result += ", ";
					}
				}
			}
		}
		return result;
	}

	public StyledString getStyledText() {
		StyledString result = new StyledString();
		ColorManager colors = ColorManager.getDefault();
		
		if (section != null) {
			if (section.getName() != null) {
				result.append(section.getName());
			} else {
				ITypeMono[] types = section.getTypeList();
				for (int i=0; i<types.length; i++) {
					result.append(types[i].toString(), colors.getPrototypeStyler());
					if (i != types.length-1) {
						result.append(", ");
					}
				}
			}
		}
		return result;
	}

	public String toString() {
		return getText();
	}

	/**
	 * Returns the image which corresponds to the element, null otherwise.
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage() {
		if (section != null) {
			return OutlineImages.PRIVATE_NONSHARED;
		}
		return null;
	}

	public List<OutlineItem> getChildren() {
		return slots;
	}

	public int compareTo(Object arg0) {
		return 0;
	}
}
