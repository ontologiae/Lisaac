package org.lisaac.ldt.model;

import java.util.HashSet;

public class AliasString {
	private HashSet<String> list;
	
	public AliasString() {
		list = new HashSet<String>();
		
		// add model string constants
		list.add(ILisaacModel.section_header); 
		list.add(ILisaacModel.section_inherit);
		list.add(ILisaacModel.section_insert);
		list.add(ILisaacModel.section_public);
		list.add(ILisaacModel.section_private);
		list.add(ILisaacModel.section_interrupt);
		list.add(ILisaacModel.section_mapping);
		list.add(ILisaacModel.section_directory);
		list.add(ILisaacModel.section_external);
		
		list.add(ILisaacModel.keyword_section);
		list.add(ILisaacModel.keyword_right);
		list.add(ILisaacModel.keyword_left);
		list.add(ILisaacModel.keyword_ldots);
		list.add(ILisaacModel.keyword_old);
		list.add(ILisaacModel.keyword_expanded);  
		list.add(ILisaacModel.keyword_strict);  
		list.add(ILisaacModel.keyword_result);
		
		list.add(ILisaacModel.symbol_affect_immediate);
		list.add(ILisaacModel.symbol_affect_cast);
		list.add(ILisaacModel.symbol_affect_code);
		list.add(ILisaacModel.symbol_auto_export);
		list.add(ILisaacModel.symbol_equal);
		list.add(ILisaacModel.symbol_not_equal);
		list.add(ILisaacModel.symbol_great);
		list.add(ILisaacModel.symbol_great_equal);
		list.add(ILisaacModel.symbol_less);
		list.add(ILisaacModel.symbol_less_equal);
		
		list.add(ILisaacModel.slot_name);
		list.add(ILisaacModel.slot_export);
		list.add(ILisaacModel.slot_import);
		list.add(ILisaacModel.slot_external);
		list.add(ILisaacModel.slot_default);
		list.add(ILisaacModel.slot_type);
		list.add(ILisaacModel.slot_version);
		list.add(ILisaacModel.slot_date);
		list.add(ILisaacModel.slot_comment);
		list.add(ILisaacModel.slot_author);
		list.add(ILisaacModel.slot_bibliography);
		list.add(ILisaacModel.slot_language);
		list.add(ILisaacModel.slot_copyright);
		list.add(ILisaacModel.slot_bug_report);
		
		list.add(ILisaacModel.prototype_true);
		list.add(ILisaacModel.prototype_false);
		list.add(ILisaacModel.prototype_self);
		list.add(ILisaacModel.prototype_string);
		list.add(ILisaacModel.prototype_integer);
		list.add(ILisaacModel.prototype_real);
		list.add(ILisaacModel.prototype_boolean);
		list.add(ILisaacModel.prototype_character);
		list.add(ILisaacModel.prototype_block);
		
		list.add(ILisaacModel.variable_null);
		list.add(ILisaacModel.variable_void);
		list.add(ILisaacModel.variable_self);
		
		// lip
		list.add(ILisaacModel.slot_lip);
		list.add(ILisaacModel.slot_if);
		list.add(ILisaacModel.slot_else);
		list.add(ILisaacModel.slot_print);
	}
	
	public String get (String str) {
		if (! list.contains(str)) {
			list.add(str);
		}
		return str;
	}
}
