package br.com.climbORM.api.utils;

import java.lang.reflect.Field;

public class Model {

	private String atribute;
	private Object value;
	private Class type;
	private Field field;
	
	public Model(String atribute, Object value, Class type, Field field) {
		this.atribute = atribute;
		this.value = value;
		this.type = type;
		this.field = field;
	}

	public String getAtribute() {
		return atribute;
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
