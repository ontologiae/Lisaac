package org.lisaac.ldt.model.types;

import java.util.HashMap;

import org.lisaac.ldt.model.ILisaacModel;

public class TypeSimple implements ITypeMono {

	protected static HashMap<String,TypeSimple> dico;

	protected String name;

	TypeSimple(String name) {
		this.name = name;

		if (dico == null) {
			dico = new HashMap<String,TypeSimple>();
		}
		dico.put(name, this);
	}
	public String getName() {
		return name;
	}

	protected static TypeSimple typeNull;
	protected static TypeSimple typeVoid;
	protected static TypeSimple typeBoolean; // for '=' operator

	public static void init() {
		typeNull = new TypeSimple(ILisaacModel.variable_null);
		typeVoid = new TypeSimple(ILisaacModel.variable_void);
		typeBoolean = new TypeSimple(ILisaacModel.prototype_boolean);
	}

	public static TypeSimple get(String n) {
		TypeSimple result=null;

		if (dico == null) {
			dico = new HashMap<String,TypeSimple>();
		}
		result = dico.get(n);
		if (result == null) {
			result = new TypeSimple(n);
		}
		return result;
	}

	public static ITypeMono getTypeVoid() {
		return typeVoid;
	}
	public static ITypeMono getTypeNull() {
		return typeNull;
	}
	public static ITypeMono getTypeBoolean() {
		return typeBoolean;
	}

	public String toString() {        // FIXME VOID pb
		if (this.equals(typeVoid) || name.compareTo("VOID") == 0) {
			return null; // do not print void type
		}
		return name;
	}
	public void rename(String oldName, String newName) {
		if (name.compareTo(oldName) == 0) {
			dico.put(name, null);
			name = newName;
			dico.put(name, this);
		}
	}
}
