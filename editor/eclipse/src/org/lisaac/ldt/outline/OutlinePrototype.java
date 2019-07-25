package org.lisaac.ldt.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.editors.ColorManager;
import org.lisaac.ldt.model.items.Prototype;

public class OutlinePrototype extends OutlineItem {
	protected String name;
	
	protected List<OutlineItem> sections;
	
	
	public OutlinePrototype(Prototype prototype, List<OutlineItem> sections) {
		name = prototype.getName();
		this.sections = sections;
	}
	
	/**
     * Returns the label which corresponds to the element, null otherwise.
     * @see ILabelProvider#getText(Object)
     */
    public String getText() {
    	return name;
    }
    
    public StyledString getStyledText() {
		ColorManager colors = ColorManager.getDefault();
		return new StyledString(name, colors.getPrototypeStyler());
	}
    
    public String toString() {
    	return name;
    }
    
    /**
     * Returns the image which corresponds to the element, null otherwise.
     * @see ILabelProvider#getImage(Object)
     */
    public Image getImage() {
    	return OutlineImages.PROTOTYPE;
    }
    
	public List<OutlineItem> getChildren() {
		List<OutlineItem> result;
		
		if (showSections) {
			result = sections;
		} else {
			result = new ArrayList<OutlineItem>();
			for (int i=0; i<sections.size(); i++) {
				result.addAll(((OutlineSection)sections.get(i)).slots);
			}
		}
		return result;
	}

	public int compareTo(Object arg0) {
		return 0;
	}
}
