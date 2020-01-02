package br.com.climbORM.framework.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.mapping.*;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

public class ReflectionUtil {

	public synchronized static String getFieldName(Field field) {
		String fieldName = field.getName();
		Column column = (Column) field.getAnnotation(Column.class);

		if (column != null && column.name().trim().length() > 0) {
			fieldName = column.name();
		}

		return fieldName;
	}

	public synchronized static Field getDynamicField(Object object) {

		Field field = null;
		Field[] fields = null;

		if (isProxedCGLIB(object)) {
			fields = object.getClass().getSuperclass().getDeclaredFields();
		} else {
			fields = object.getClass().getDeclaredFields();
		}

		for (Field f : fields) {
			if (f.getType() == DynamicFields.class) {
				field = f;
			}
		}

		return field;

	}

	public synchronized static boolean isContainsDynamicFields(Object object) {

		boolean exist = false;
		Field[] fields = null;

		if (isProxedCGLIB(object)) {
			fields = object.getClass().getSuperclass().getDeclaredFields();
		} else {
			fields = object.getClass().getDeclaredFields();
		}

		for (Field field : fields) {
			if (field.isAnnotationPresent(DynamicField.class)) {
				exist= true;
				break;
			}
		}

		return exist;
	}

	public synchronized static void setValueField(Field field, Object object, Object value) {
		try {
			new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
					.invoke(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}

	public synchronized static Object getValueField(Field field, Object object) {

		Object tempValue = null;

		try {

			Object value = new PropertyDescriptor(field.getName(), object.getClass()).getReadMethod().invoke(object);

			if (value == null) {
				return null;
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

		if (object == null) {
			return false;
		}

		return object.getClass().getName().contains("EnhancerByCGLI");
	}


	public synchronized static  String getTableName(Object object) {

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

	public synchronized static List<ModelTableField> generateModel(Object object) {

		List<ModelTableField> modelTableFields = new ArrayList<ModelTableField>();
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
				modelTableFields.add(new ModelTableField(fieldName, tempValue, Long.class, field));

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
					modelTableFields.add(new ModelTableField(fieldName, tempValue, Long.class, field));

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
					modelTableFields.add(new ModelTableField(fieldName, tempValue, Integer.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Float.class || field.getType() == float.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					modelTableFields.add(new ModelTableField(fieldName, tempValue, Float.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Double.class || field.getType() == double.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					modelTableFields.add(new ModelTableField(fieldName, tempValue, Double.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					modelTableFields.add(new ModelTableField(fieldName, tempValue, Boolean.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == String.class || field.getType() == char.class) {
				try {

					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					modelTableFields.add(new ModelTableField(fieldName, tempValue, String.class, field));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == byte[].class) {
				try {
					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					modelTableFields.add(new ModelTableField(fieldName, tempValue, byte[].class, field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (field.getType() == List.class && field.isAnnotationPresent(Json.class)) {
				try {
					String fieldName = ReflectionUtil.getFieldName(field);
					Object tempValue = ReflectionUtil.getValueField(field, object);
					modelTableFields.add(new ModelTableField(fieldName, tempValue, List.class, field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return modelTableFields;
	}

	public static Class<?> beanGenerator(final String className, final Map<String, Class<?>> properties) {
		BeanGenerator beanGenerator = new BeanGenerator();
		beanGenerator.setNamingPolicy(new NamingPolicy() {
			@Override
			public String getClassName(String prefix, String source, Object key, Predicate names) {
				return className;
			}
		});

		BeanGenerator.addProperties(beanGenerator, properties);
		return (Class<?>) beanGenerator.createClass();
	}

}
