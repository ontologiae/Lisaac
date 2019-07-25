package org.lisaac.ldt.outline;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class OutlineContentProvider implements ITreeContentProvider {

	/**
     * @see ITreeContentProvider#getChildren(Object)
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof OutlineItem) {
            OutlineItem outlineElement = (OutlineItem) parentElement;
            List<OutlineItem> children = outlineElement.getChildren();
            if (children != null) {
            	return children.toArray(new Object[children.size()]);
            }
        } else if (parentElement instanceof Object[]) {
            return ((Object[]) parentElement);
        }
        return new Object[0];
    }


	 /**
     * @see ITreeContentProvider#getParent(Object)
     */
    public Object getParent(Object element) {
        return null;
    }

	 /**
     * @see ITreeContentProvider#hasChildren(Object)
     */
	public boolean hasChildren(Object element) {
        return (getChildren(element).length > 0);
    }

	 /**
     * @see IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
