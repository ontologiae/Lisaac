package org.lisaac.ldt.outline;

import java.util.List;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

public abstract class OutlineItem implements Comparable<Object> {
	
	public static boolean showSections = true;
	
	protected int fstartOffset;
	protected int fLength;
	
	/**
     * Returns the image which corresponds to the element, null otherwise.
     * @see ILabelProvider#getImage(Object)
     */
    public abstract Image getImage();
    
	
	/**
     * Returns the label which corresponds to the element, null otherwise.
     * @see ILabelProvider#getText(Object)
     */
    public abstract String getText();
	
    public abstract StyledString getStyledText();
    
	/**
     * Returns the children of this element.
     */
    public abstract List<OutlineItem> getChildren();


	public int startOffset() {
		return fstartOffset;
	}

	public int length() {
		return fLength;
	}	
}
