package br.com.climbORM.framework.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.mapping.*;

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

	public static  String getTableName(Object object) {

		Entity entity = null;

		if (ReflectionUtil.isProxedCGLIB(object)) {
			entity = (Entity) object.getClass().getSuperclass().getAnnotation(Entity.class);
		} else {
			entity = (Entity) object.getClass().getAnnotation(Entity.class);
		}

		String tableName = "";

		if (entity != null && entity.name().trim().length() > 0) {
			tableName = entity.name().trim();
		} else {
			throw new Error("ERROR: " + object.getClass().getName() + " Unnamed entity ");
		}

		return tableName;
	}

	public static List<Model> generateModel(Object object) {

		List<Model> models = new ArrayList<Model>();
		Field[] fields = null;

		if (ReflectionUtil.isProxedCGLIB(object)) {
			fields = object.getClass().getSuperclass().getDeclaredFields();
		} else {
			fields = object.getClass().getDeclaredFields();
		}

		for (Field field : fields) {

			if (field.isAnnotationPresent(Transient.class)) {
				continue;
			}

			if (field.isAnnotationPresent(Relation.class)) {
				String fieldName = ReflectionUtil.getFieldName(field);
				Object tempValue = ReflectionUtil.getValueField(field, object);
				models.add(new Model(fieldName, tempValue, Long.class, field));

				continue;
			}

			if (field.getType() == Long.class) {
				try {

					ID id = field.getAnnotation(ID.class);

					if (id != null && id.sequencial()) {
						continue;
					}

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Long.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Integer.class) {
				try {

					ID id = field.getAnnotation(ID.class);

					if (id != null && id.sequencial()) {
						continue;
					}

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Integer.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Float.class || field.getType() == float.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Float.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Double.class || field.getType() == double.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Double.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, Boolean.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == String.class || field.getType() == char.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, String.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == byte[].class) {
				try {
					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, byte[].class, field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == List.class && field.isAnnotationPresent(Json.class)) {
				try {
					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					models.add(new Model(fieldName, tempValue, List.class, field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return models;
	}

}
