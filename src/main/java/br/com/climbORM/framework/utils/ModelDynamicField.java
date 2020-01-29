package br.com.climbORM.framework.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public class ModelDynamicField {

	private String attribute;
	private Class type;

	public ModelDynamicField(String attribute, Class type) {
		this.attribute = attribute;
		this.type = type;
	}

	public String getAttribute() {
		return attribute;
	}

	public Class getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ModelDynamicField that = (ModelDynamicField) o;
		return Objects.equals(attribute, that.attribute) &&
				Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(attribute, type);
	}
}
