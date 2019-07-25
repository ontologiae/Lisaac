package org.lisaac.ldt.outline;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.lisaac.ldt.LisaacPlugin;

public class OutlineImages {
	public static final Image PROTOTYPE = getImage("/icons/prototype.gif");
	
	public static final Image PUBLIC_SHARED = getImage("/icons/public-shared.gif");
	public static final Image PRIVATE_SHARED = getImage("/icons/private-shared.gif");
	public static final Image PUBLIC_NONSHARED = getImage("/icons/public-nonshared.gif");
	public static final Image PRIVATE_NONSHARED = getImage("/icons/private-nonshared.gif");
	
	public static final Image KEYWORD = getImage("/icons/keyword.gif");
	
	public static final Image BLANK = getImage("/icons/blank.gif");
	
	
	public static final String SORT_ALPHA = "/icons/alphab_sort_co.gif";
	public static final String SORT_SECTION = "/icons/sections_co.gif";
	
	private static Image getImage(String path) {
		ImageDescriptor descr = LisaacPlugin.getImageDescriptor(path);
		return descr.createImage();
	}
}
