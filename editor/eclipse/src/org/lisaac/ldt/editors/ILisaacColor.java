package org.lisaac.ldt.editors;

import org.eclipse.swt.graphics.RGB;

/**
 * Associate a color to each token lexical class.
 * @author Damien Bouvarel
 */
public interface ILisaacColor {
	RGB COMMENT = new RGB(200, 50, 0);
	RGB PROTOTYPE = new RGB(0, 128, 0);
	RGB PROTOTYPE_STYLE = new RGB(255, 0, 0);
	RGB KEYWORD = new RGB(128, 0, 255);
	RGB CHARACTER = new RGB(128, 128, 255);
	RGB STRING = new RGB(210, 150, 150);
	RGB NUMBER = new RGB(128, 0, 255);
	RGB OPERATOR = new RGB(200, 130, 0);
	RGB EXTERNAL = new RGB(128, 255, 128);
	RGB UNDEFINED = new RGB(0, 0, 255);
	RGB DEFAULT = new RGB(0, 0, 0);
	
	RGB GRAY = new RGB(128, 128, 128);
	
	String PREF_NORMAL = "normal";
	String PREF_BOLD = "bold";
	String PREF_ITALICS = "italic";
	String PREF_UNDERLINE = "underline";
	
	String PREF_COMMENT = "comment_color";
	String PREF_PROTOTYPE = "prototype_color";
	String PREF_PROTOTYPE_STYLE = "prototype_style_color";
	String PREF_KEYWORD = "keyword_color";
	String PREF_SLOT = "slot_color";
	String PREF_LOCAL_SLOT = "local_slot_color";
	String PREF_CHARACTER = "character_color";
	String PREF_STRING  = "string_color";
	String PREF_NUMBER = "number_color";
	String PREF_OPERATOR = "operator_color";
	String PREF_EXTERNAL = "external_color";

	String STYLE_PROTOTYPE = "prototype_style";
	String STYLE_PROTOTYPE_STYLE = "prototype_style_style";
	String STYLE_KEYWORD = "keyword_style";
	String STYLE_SLOT = "slot_style";
	String STYLE_LOCAL_SLOT = "local_slot_style";
	String STYLE_CHARACTER = "character_style";
	String STYLE_STRING  = "string_style";
	String STYLE_NUMBER = "number_style";
	String STYLE_OPERATOR = "operator_style";
}