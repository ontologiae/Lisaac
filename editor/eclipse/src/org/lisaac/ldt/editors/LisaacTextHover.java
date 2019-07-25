package org.lisaac.ldt.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Shell;
import org.lisaac.ldt.LisaacPlugin;
import org.lisaac.ldt.model.LisaacModel;
import org.lisaac.ldt.model.items.IVariable;
import org.lisaac.ldt.model.items.Prototype;
import org.lisaac.ldt.model.items.Slot;
import org.lisaac.ldt.preferences.PreferenceConstants;

public class LisaacTextHover implements ITextHover, ITextHoverExtension {

	protected LisaacModel model;
	protected String filename;
	protected ColorManager colorManager;

	public LisaacTextHover(LisaacModel model, String filename, ColorManager colorManager) {
		super();
		this.model = model;
		this.filename = filename;
		this.colorManager = colorManager;
	}

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		try {
			boolean enableHover = LisaacPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_LISAAC_HOVER);
			if (! enableHover) {
				return null;
			}
			
			if (model == null) {
				return null;
			}
			
			String text = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
			if (LisaacScanner.isPrototypeIdentifier(text)) {
				// get prototype info

				Prototype prototype = model.getPrototype(text);
				if (prototype != null) {
					return "<I>Prototype</I> : "+prototype.getHoverInformation();
				}
			} else if (LisaacScanner.isIdentifier(text)) {
				// get slot info

				Prototype prototype = model.getPrototype(LisaacModel.extractPrototypeName(filename));
				if (prototype != null) {
					Slot slot = prototype.getSlotFromKeyword(text, prototype.openParser(), hoverRegion.getOffset());
					if (slot != null) {
						return "<I>Slot</I> : "+slot.getHoverInformation();
					} else {
						slot = prototype.getSlot(hoverRegion.getOffset());
						IVariable variable = slot.getVariable(text, hoverRegion.getOffset());
						if (variable != null) {
							return variable.getHoverInformation();
						} else {
							// is 'text' a slot-call argument?
							slot = prototype.lookupSlot(text);
							if (slot != null) {
								return "<I>Argument</I> : "+slot.getHoverInformation();
							}
						}
					}
				}
			}
		} catch (BadLocationException e) {
		} catch (CoreException e) {
		}		
		return null;
	}


	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		try {
			return selectWord(textViewer.getDocument(), offset);
		} catch (BadLocationException e) {
		}
		return new Region(offset, 0);
	}


	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, "Lisaac", new HoverPresenter(colorManager));
			}
		};
	}

	protected IRegion selectWord(IDocument doc, int caretPos) throws BadLocationException {
		int startPos, endPos;

		int pos = caretPos;
		char c;

		while (pos >= 0) {
			c = doc.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			--pos;
		}
		startPos = pos+1;
		pos = caretPos;
		int length = doc.getLength();

		while (pos < length) {
			c = doc.getChar(pos);
			if (!Character.isJavaIdentifierPart(c))
				break;
			++pos;
		}
		endPos = pos;
		return new Region(startPos, endPos - startPos);
	}
}
