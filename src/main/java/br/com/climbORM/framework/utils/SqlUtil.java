package br.com.climbORM.framework.utils;

import br.com.climbORM.framework.mapping.Json;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.List;

public class SqlUtil {

    public static byte[] getBinaryValue(Long id, String field, String entity, Connection connection)
            throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
        ResultSet resultSet = stmt.executeQuery("SELECT " + field + " FROM " + entity + " WHERE ID = " + id.toString());

        while (resultSet.next()) {
            return resultSet.getBytes(field);
        }

        return null;
    }

    public static PreparedStatement preparedStatementInsert(String schema, Connection connection, List<Model> models,
                                                            String tableName) throws Exception {

        StringBuilder atributes = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Model model : models) {

            atributes.append(model.getAttribute() + ",");
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

        return getPreparedStatement(ps, models);

    }

    public static PreparedStatement getPreparedStatement(PreparedStatement ps, List<Model> models) throws Exception {

        int i = 0;
        for (Model model : models) {

            i += 1;
            if (model.getType() == Long.class || model.getType() == long.class) {
                if (ReflectionUtil.isProxedCGLIB(model.getValue())) {

                    Object value = new PropertyDescriptor("id", model.getValue().getClass().getSuperclass()).getReadMethod().invoke(model.getValue());
                    ps.setLong(i, (long) value);

                } else {
                    ps.setLong(i, (long) model.getValue());
                }
            } else if (model.getType() == Integer.class || model.getType() == int.class) {
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
                ps.setObject(i, jsonString);
            }
        }

        return ps;
    }

    public static PreparedStatement preparedStatementUpdate(String schema, Connection connection, List<Model> models,
                                                            String tableName, Long id) throws Exception {

        StringBuilder values = new StringBuilder();
        for (Model model : models) {

            if (model.getField().isAnnotationPresent(Json.class)) {
                values.append(model.getAttribute() + "= ?::JSON,");
            } else {
                values.append(model.getAttribute() + "= ?,");
            }

        }

        String sql = "UPDATE " + schema + "." + tableName + " SET " + values.toString().substring(0, values.toString().length() -1) + " WHERE id = " + id.toString();

        System.out.println(sql);

        PreparedStatement ps = connection.prepareStatement(sql);

        return getPreparedStatement(ps, models);

    }

    public static String generateSqlInsert(String schema, List<Model> models, String tableName) {

        StringBuilder atributes = new StringBuilder();
        StringBuilder values = new StringBuilder();
        int count = 1;
        for (Model model : models) {

            if (models.size() == count) {
                atributes.append(model.getAttribute());
                values.append(model.getValue());
            } else {
                atributes.append(model.getAttribute() + ",");
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
                values.append(model.getAttribute() + "=" + model.getValue());
            } else {
                values.append(model.getAttribute() + "=" + model.getValue() + ",");
            }

            count += 1;
        }

        return "UPDATE " + schema + "." + tableName + " SET " + values.toString() + " WHERE id = " + id.toString();

    }

}

