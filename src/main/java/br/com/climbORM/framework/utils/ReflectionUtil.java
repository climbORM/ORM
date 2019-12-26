package br.com.climbORM.framework.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.mapping.Column;
import br.com.climbORM.framework.mapping.Entity;

public class ReflectionUtil {

	public static String getFieldName(Field field) {
		String fieldName = field.getName();
		Column column = (Column) field.getAnnotation(Column.class);

		if (column != null && column.name().trim().length() > 0) {
			fieldName = column.name();
		}

		return fieldName;
	}

	public static Object getValueField(Field field, Object object) {

		Object tempValue = null;

		try {

			Object value = new PropertyDescriptor(field.getName(), object.getClass()).getReadMethod().invoke(object);

			if (value == null) {
				return "null";
			}

			if (value != null && value.getClass().isAnnotationPresent(Entity.class)) {
				tempValue = ((PersistentEntity) value).getId();
			} else {
				tempValue = value;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempValue;
	}

	public synchronized static boolean isProxedCGLIB(Object object) {

		return object.getClass().getName().contains("EnhancerByCGLI");
	}

	public static Class getFieldGenericType(Field field) {
        if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
            ParameterizedType genericType =
             (ParameterizedType) field.getGenericType();
            return ((Class)
              (genericType.getActualTypeArguments()[0])).getSuperclass();
        }

        return new Boolean(false).getClass();
    }

}
