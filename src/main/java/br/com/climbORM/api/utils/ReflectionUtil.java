package br.com.climbORM.api.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.climbORM.api.PersistentEntity;
import br.com.climbORM.api.mapping.Column;
import br.com.climbORM.api.mapping.Entity;
import br.com.climbORM.api.mapping.Json;

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

			// ****** LAZY LOAD *******

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempValue;
	}

	public static byte[] getBinaryValue(Long id, String field, String entity, Connection connection)
			throws SQLException {
		Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
		ResultSet resultSet = stmt.executeQuery("SELECT " + field + " FROM " + entity + " WHERE ID = " + id.toString());

		while (resultSet.next()) {
			return resultSet.getBytes(field);
		}

		return null;
	}

	public static PreparedStatement preparedStatement(String schema, Connection connection, List<Model> models,
			String tableName) throws Exception {

		StringBuilder atributes = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (Model model : models) {
			
			atributes.append(model.getAtribute() + ",");
			if (model.getField().isAnnotationPresent(Json.class)) {
				values.append("?::JSON,");
			} else {
				values.append("?,");
			}
			
		}
		String sql = "INSERT INTO " + schema + "." + tableName + "("
				+ atributes.toString().substring(0, atributes.toString().length() - 1) + ") VALUES ("
				+ values.toString().substring(0, values.toString().length() -1) + ") RETURNING ID";

		System.out.println(sql);

		PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		int i = 0;
		for (Model model : models) {

			i += 1;

			System.out.println(model.getAtribute());

//			ps.setObject(parameterIndex, x);

			if (model.getType() == Long.class) {
				ps.setLong(i, (long) model.getValue());
			} else if (model.getType() == Integer.class) {
				ps.setInt(i, (int) model.getValue());
			} else if (model.getType() == Float.class || model.getType() == float.class) {
				ps.setFloat(i, (float) model.getValue());
			} else if (model.getType() == Double.class || model.getType() == double.class) {
				ps.setDouble(i, (double) model.getValue());
			} else if (model.getType() == Boolean.class || model.getType() == boolean.class) {
				ps.setBoolean(i, (boolean) model.getValue());
			} else if (model.getType() == String.class || model.getType() == char.class) {
				ps.setString(i, model.getValue().toString());
			} else if (model.getType() == byte[].class) {
				ps.setBytes(i, (byte[]) model.getValue());
			} else if (model.getType() == List.class && model.getField().isAnnotationPresent(Json.class)) {

				ObjectMapper mapper = new ObjectMapper();
				String jsonString = mapper.writeValueAsString(model.getValue());

				System.out.println("json" + jsonString);

				ps.setObject(i, jsonString);
			}
		}

		return ps;

	}

	public static String generateSqlInsert(String schema, List<Model> models, String tableName) {

		StringBuilder atributes = new StringBuilder();
		StringBuilder values = new StringBuilder();
		int count = 1;
		for (Model model : models) {

			if (models.size() == count) {
				atributes.append(model.getAtribute());
				values.append(model.getValue());
			} else {
				atributes.append(model.getAtribute() + ",");
				values.append(model.getValue() + ",");
			}

			count += 1;
		}

		return "INSERT INTO " + schema + "." + tableName + "(" + atributes.toString() + ") VALUES (" + values.toString()
				+ ")";

	}

	public static String generateSqlUpdate(String schema, List<Model> models, String tableName, Long id) {

		StringBuilder values = new StringBuilder();
		int count = 1;
		for (Model model : models) {

			if (models.size() == count) {
				values.append(model.getAtribute() + "=" + model.getValue());
			} else {
				values.append(model.getAtribute() + "=" + model.getValue() + ",");
			}

			count += 1;
		}

		return "UPDATE " + schema + "." + tableName + " SET " + values.toString() + " WHERE id = " + id.toString();

	}
	
	public static Class getFieldGenericType(Field field) {
        if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
            ParameterizedType genericType =
             (ParameterizedType) field.getGenericType();
            return ((Class)
              (genericType.getActualTypeArguments()[0])).getSuperclass();
        }
        //Returns dummy Boolean Class to compare with ValueObject & FormBean
        return new Boolean(false).getClass();
    }

}
