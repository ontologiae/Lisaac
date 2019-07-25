package org.lisaac.ldt.outline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class OutlineLabelProvider extends StyledCellLabelProvider  {

	/**
	 * @see WorkbenchLabelProvider#getImage(Object)
	 * @param element the element for which an image is created
	 * @return the image associated to the element
	 */
	public Image getImage(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) element;
			Image image = item.getImage();

			if (image != null) {
				return image;
			}
		}
		return OutlineImages.BLANK;
	}

	/**
	 * @see WorkbenchLabelProvider#getText(Object)
	 * @param element the element for which a label is created
	 * @return the label associated to the element
	 */
	public String getText(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) element;
			return item.getText();
		}
		if (element != null) {
			return element.toString();
		} else {
			return new String();
		}
	}

	public StyledString getStyledText(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) element;
			return item.getStyledText();
		}
		if (element != null) {
			return new StyledString(element.toString());
		} else {
			return new StyledString();
		}
	}

	public void update(ViewerCell cell) {
		Object obj = cell.getElement();
		StyledString styledString = getStyledText(obj);

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());
		cell.setImage(getImage(obj));
		super.update(cell);
	}
}
