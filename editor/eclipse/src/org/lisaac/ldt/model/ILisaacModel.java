package org.lisaac.ldt.model;

import org.eclipse.core.resources.IProject;
import org.lisaac.ldt.builder.ILisaacErrorHandler;
import org.lisaac.ldt.model.lip.LIP;

public interface ILisaacModel {
	
	final String inherit_shared             = "Shared";
	final String inherit_shared_expanded    = "Shared Expanded";
	final String inherit_nonshared          = "Non Shared";
	final String inherit_nonshared_expanded = "Non Shared Expanded";
	
	//
	final String keyword_section  = "Section";
	final String keyword_right    = "Right";
	final String keyword_left     = "Left";
	final String keyword_ldots    = "...";
	final String keyword_old      = "Old";
	final String keyword_expanded = "Expanded";  
	final String keyword_strict   = "Strict";  
	final String keyword_result   = "Result";
	
	final String symbol_affect_immediate = ":=";
	final String symbol_affect_cast      = "?=";
	final String symbol_affect_code      = "<-";
	final String symbol_auto_export      = "->";
	final String symbol_auto_import      = symbol_affect_code;
	final String symbol_equal            = "=";
	final String symbol_not_equal        = "!=";
	final String symbol_great            = ">";
	final String symbol_great_equal      = ">=";
	final String symbol_less             = "<";
	final String symbol_less_equal       = "<=";
	
	final String section_header     = "Header";
	final String section_inherit    = "Inherit";
	final String section_insert     = "Insert";
	final String section_public     = "Public";
	final String section_private    = "Private";
	final String section_interrupt  = "Interrupt";
	final String section_mapping    = "Mapping";
	final String section_directory  = "Directory";
	final String section_external   = "External";
	
	final String slot_name         = "name";
	final String slot_export       = "export";
	final String slot_import       = "import";
	final String slot_external     = "external";
	final String slot_default      = "default";
	final String slot_type         = "type";
	final String slot_version      = "version";
	final String slot_date         = "date";
	final String slot_comment      = "comment";
	final String slot_author       = "author";
	final String slot_bibliography = "bibliography";
	final String slot_language     = "language";
	final String slot_copyright    = "copyright";
	final String slot_bug_report   = "bug_report";
	
	final String  prototype_true            = "TRUE";
	final String  prototype_false           = "FALSE";
	final String  prototype_self            = "SELF";
	final String  prototype_string          = "STRING";
	final String  prototype_integer         = "INTEGER";
	final String  prototype_real            = "REAL";
	final String  prototype_boolean         = "BOOLEAN";
	final String  prototype_character       = "CHARACTER";
	final String  prototype_block           = "BLOCK";
	
	final String variable_null          = "NULL";
	final String variable_void          = "VOID";
	final String variable_self          = "Self";
	
	
	final String[] keywords = new String[] { keyword_section, section_public,
			section_private, section_inherit, section_header, section_insert,
			section_mapping, section_interrupt,
			section_external, section_directory, 
			keyword_expanded, keyword_strict,
			keyword_left, keyword_right,
			variable_self, keyword_old, keyword_result};
	
	
	// lip
	final String slot_lip    = "lip";
	final String slot_if    = "if";
	final String slot_else  = "else";
	final String slot_print = "print";
	
	final String slot_debug_mode = "run_mode";
	
	ILisaacErrorHandler getReporter();
	AliasString getAliasString();
	LisaacParser getParser();
	LIP getLipCode();
	IProject getProject(); 
	LisaacPath getPathManager();
}
