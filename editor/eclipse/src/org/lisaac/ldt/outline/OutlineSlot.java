package org.lisaac.ldt.outline;

import java.util.List;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.model.Position;
import org.lisaac.ldt.model.items.Slot;

public class OutlineSlot extends OutlineItem {

	protected Slot slot;

	public OutlineSlot(Slot slot) {
		this.slot = slot;
		
		Position position = slot.getPosition();
		
		fstartOffset = position.getStartOffset();
		fLength = position.length();
	}

	/**
	 * Returns the label which corresponds to the element, null otherwise.
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText() {
		String result=null;
		if (slot != null) {
			result = slot.getSignature(false);
		}
		return result;
	}
	
	public StyledString getStyledText() {
		if (slot != null) {
			return slot.getStyledSignature(false, false);
		}
		return null;
	}

	public String toString() {
		return getText();
	}

	/**
	 * Returns the image which corresponds to the element, null otherwise.
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage() {
		if (slot != null) {
			if (slot.getStyle() == '+') {
				if (slot.getSectionId() != null && 
						slot.getSectionId().isPrivateStyle()) {
					return OutlineImages.PRIVATE_NONSHARED;
				} else {
					return OutlineImages.PUBLIC_NONSHARED;
				}
			} else {
				if (slot.getSectionId() != null && 
						slot.getSectionId().isPrivateStyle()) {
					return OutlineImages.PRIVATE_SHARED;
				} else {
					return OutlineImages.PUBLIC_SHARED;
				}
			}
		}
		return null;
	}

	public List<OutlineItem> getChildren() {
		return null;
	}

	public int compareTo(Object obj) {
		if (obj instanceof OutlineSlot) {
			OutlineSlot slot = (OutlineSlot) obj;
			return getText().compareTo(slot.getText());
		}
		return 0;
	}
}
