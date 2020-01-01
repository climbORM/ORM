package br.com.climbORM.framework.utils;

import java.lang.reflect.Field;

public class ModelTableField {

	private String attribute;
	private Object value;
	private Class type;
	private Field field;

	public ModelTableField(String attribute, Object value, Class type, Field field) {
		this.attribute = attribute;
		this.value = value;
		this.type = type;
		this.field = field;
	}

	public ModelTableField(String attribute, Object value, Class type) {
		this.attribute = attribute;
		this.value = value;
		this.type = type;
	}

	public String getAttribute() {
		return attribute;
	}

	public Object getValue() {
		return value;
	}

	public Class getType() {
		return type;
	}

	public Field getField() {
		return field;
	}

}
