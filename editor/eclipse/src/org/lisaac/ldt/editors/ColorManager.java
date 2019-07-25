package org.lisaac.ldt.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public class ColorManager {

	private static ColorManager instance;
	
	protected Map<RGB,Color> fColorTable = new HashMap<RGB,Color>(10);
	private Map<String,IToken> tokenTable = new HashMap<String,IToken>(10);
	private Map<String,String> styleTable = new HashMap<String,String>(10);

	private final IPreferenceStore preferenceStore;

	private Styler operatorStyler;
	private Styler prototypeStyler;
	private Styler slotStyler;
	private Styler variableStyler;
	
	public ColorManager(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		instance = this;
		
		operatorStyler = new DefaultStyler(ILisaacColor.PREF_OPERATOR, ILisaacColor.STYLE_OPERATOR);
		prototypeStyler = new DefaultStyler(ILisaacColor.PREF_PROTOTYPE, ILisaacColor.STYLE_PROTOTYPE);
		slotStyler = new DefaultStyler(ILisaacColor.PREF_SLOT, ILisaacColor.STYLE_SLOT);
		variableStyler = new DefaultStyler(ILisaacColor.PREF_LOCAL_SLOT, ILisaacColor.STYLE_LOCAL_SLOT);
	}

	public static ColorManager getDefault() {
		return instance;
	}
	
	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	public IToken getToken(String prefKey, String styleKey) {
		Token token = (Token) tokenTable.get(prefKey);
		int style = convertToStyle(styleKey);

		if (token == null) {
			String colorName = preferenceStore.getString(prefKey);
			RGB rgb = StringConverter.asRGB(colorName);

			token = new Token(new TextAttribute(getColor(rgb), null, style));
			tokenTable.put(prefKey, token);
			styleTable.put(styleKey, prefKey);
		} else {
			TextAttribute attrib = (TextAttribute) token.getData();
			if (attrib.getStyle() != style) {
				token = new Token(new TextAttribute(attrib.getForeground(), null, style));
				tokenTable.put(prefKey, token);
			}
		}
		return token;
	}

	public IToken getToken2(String prefKey, String prefKey2) {
		Token token = (Token) tokenTable.get(prefKey);
		if (token == null) {
			String colorName = preferenceStore.getString(prefKey);
			RGB rgb = StringConverter.asRGB(colorName);
			String colorName2 = preferenceStore.getString(prefKey2);
			RGB rgb2 = StringConverter.asRGB(colorName2);
			token = new Token(new TextAttribute(getColor(rgb2), getColor(rgb), SWT.NORMAL));
			tokenTable.put(prefKey, token);
		}
		return token;
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		Token token = (Token) tokenTable.get(event.getProperty());
		return (token != null) || styleTable.get(event.getProperty()) != null;
	}

	public void handlePreferenceStoreChanged (PropertyChangeEvent event) {
		String prefKey = event.getProperty();
		Token token = (Token) tokenTable.get(prefKey);
		if (token != null) {      	
			String colorName = preferenceStore.getString(prefKey);
			RGB rgb = StringConverter.asRGB(colorName);

			if (prefKey.equals(ILisaacColor.PREF_EXTERNAL)) {
				String colorName2 = preferenceStore.getString(ILisaacColor.PREF_LOCAL_SLOT);
				RGB rgb2 = StringConverter.asRGB(colorName2);
				token.setData(new TextAttribute(getColor(rgb2), getColor(rgb), SWT.NORMAL));
			} else {
				token.setData(new TextAttribute(getColor(rgb)));
			}
		} else { // update style
			String key = (String) styleTable.get(prefKey);
			if (key != null) {
				token = (Token) tokenTable.get(key);
				if (token != null) {      	
					int style = convertToStyle(prefKey);
					TextAttribute attrib = (TextAttribute) token.getData();
					token.setData(new TextAttribute(attrib.getForeground(), null, style));
				}
			}
		}
	}
	
	public int convertToStyle(String prefKey) {
		String pref = preferenceStore.getString(prefKey);
		if (pref.equals(ILisaacColor.PREF_NORMAL)) {
			return SWT.NORMAL;
		}
		if (pref.equals(ILisaacColor.PREF_BOLD)) {
			return SWT.BOLD;
		}
		if (pref.equals(ILisaacColor.PREF_ITALICS)) {
			return SWT.ITALIC;
		}
		if (pref.equals(ILisaacColor.PREF_UNDERLINE)) {
			return TextAttribute.UNDERLINE;
		}
		return SWT.NORMAL; 
	}
	
	public Styler getOperatorStyler() {
		return operatorStyler;
	}
	
	public Styler getPrototypeStyler() {
		return prototypeStyler;
	}
	
	public Styler getSlotStyler() {
		return slotStyler;
	}
	
	public Styler getVariableStyler() {
		return variableStyler;
	}
	
	private class DefaultStyler extends Styler {
		String prefKey, styleKey;
		
		public DefaultStyler(String prefKey, String styleKey) {
			this.prefKey = prefKey;
			this.styleKey = styleKey;
		}
		public void applyStyles(TextStyle textStyle) {
			IToken token = getToken(prefKey, styleKey);
			TextAttribute attrib = (TextAttribute) token.getData();
			textStyle.foreground = attrib.getForeground();
		}
	}
}
