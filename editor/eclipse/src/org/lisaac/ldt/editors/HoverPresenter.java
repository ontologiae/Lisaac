package org.lisaac.ldt.editors;

import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.TextPresentation;


/**
 * Format Lisaac hover text.
 */
public class HoverPresenter implements DefaultInformationControl.IInformationPresenter, DefaultInformationControl.IInformationPresenterExtension {

	private static final int NONE = 0;
	private static final int BOLD = 1;
	private static final int ITALIC = 2;
	private static final int GRAY = 3;
	
	protected ColorManager colorManager;
	
	public HoverPresenter(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	
	/*
	 * @see IHoverInformationPresenterExtension#updatePresentation(Drawable drawable, String, TextPresentation, int, int)
	 * @since 3.2
	 */
	public String updatePresentation(Drawable drawable, String hoverInfo,
			TextPresentation presentation, int maxWidth, int maxHeight) {

		if (hoverInfo == null)
			return null;

		StringBuffer buffer= new StringBuffer();

		int style = NONE;
		Stack<Integer> styles = new Stack<Integer>();
		int startOffset = 0;

		int len = hoverInfo.length();
		int i = 0;
		int derive = 0;

		while (i < len) {
			char c = hoverInfo.charAt(i);

			if (c == '<' && i < len-3) {
				if (hoverInfo.charAt(i+1) == '/') {
					// end of style

					c = Character.toLowerCase(hoverInfo.charAt(i+2));
					if (hoverInfo.charAt(i+3) == '>') {
						style = styles.pop();
						startOffset = styles.pop();
						int styleDerive = styles.pop();
						
						int lengthDerive = derive - styleDerive;
						
						if (lengthDerive > 0) {
							// FIXME ranges cannot overlap...
							style = NONE;
						}
						
						switch(style) {
						case BOLD:
							presentation.addStyleRange(new StyleRange(
									startOffset - styleDerive, i - startOffset - lengthDerive, null, null, SWT.BOLD));
							break;
						case ITALIC:
							presentation.addStyleRange(new StyleRange(
									startOffset - styleDerive, i - startOffset - lengthDerive, null, null, SWT.ITALIC));
							break;
						case GRAY:
							Color gray = colorManager.getColor(ILisaacColor.GRAY);
							presentation.addStyleRange(new StyleRange(
									startOffset - styleDerive, i - startOffset - lengthDerive, gray, null, SWT.NONE));
							break;
						}
						i += 3;
						derive += 4;
					}
					style = NONE;
				} else {
					c = Character.toLowerCase(hoverInfo.charAt(i+1));
					startOffset = i+3;
					switch(c) {
					case 'b':
						style = BOLD;
						break;
					case 'i':
						style = ITALIC;
						break;
					case 'g':
						style = GRAY;
						break;
					}
					c = hoverInfo.charAt(i+2);
					if (c != '>') {
						buffer.append(c);
						style = NONE;
					} else {
						i += 2;
						derive += 3;
						
						styles.push(derive);
						styles.push(startOffset);
						styles.push(style);
					}
				}
			} else {
				buffer.append(c);
			}
			i++;
		}
		
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter#updatePresentation(org.eclipse.swt.widgets.Display, java.lang.String, org.eclipse.jface.text.TextPresentation, int, int)
	 * @deprecated
	 */
	public String updatePresentation(Display display, String hoverInfo, TextPresentation presentation, int maxWidth, int maxHeight) {
		return updatePresentation((Drawable)display, hoverInfo, presentation, maxWidth, maxHeight);
	}
}

