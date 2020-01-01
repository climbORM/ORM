package br.com.climbORM.framework.utils;

import br.com.climbORM.framework.mapping.Json;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlUtil {

    public synchronized static byte[] getBinaryValue(Long id, String field, String entity, Connection connection)
            throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
        ResultSet resultSet = stmt.executeQuery("SELECT " + field + " FROM " + entity + " WHERE ID = " + id.toString());

        while (resultSet.next()) {
            return resultSet.getBytes(field);
        }

        return null;
    }

    public synchronized static PreparedStatement preparedStatementInsert(String schema, Connection connection, List<Model> models,
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

    public synchronized static PreparedStatement getPreparedStatement(PreparedStatement ps, List<Model> models) throws Exception {

        int i = 0;
        for (Model model : models) {

            i += 1;
            if (model.getType() == Long.class || model.getType() == long.class) {
                if (ReflectionUtil.isProxedCGLIB(model.getValue())) {

                    Object value = new PropertyDescriptor("id", model.getValue().getClass().getSuperclass()).getReadMethod().invoke(model.getValue());
                    ps.setLong(i, (long) value);

                } else {
                    if (model.getValue() != null) {
                        ps.setLong(i, (long) model.getValue());
                    } else {
                        ps.setNull(i, 0);
                    }
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

    public synchronized static Map<String,String> getCreatedFields(final String tableName, final Connection connection) {

        final String sql = "SELECT column_name, data_type FROM information_schema.columns WHERE \n" +
                "table_name = ?;";

        Map<String, String> map = null;
        PreparedStatement ps = null;
        try {

            map = new HashMap<>();

            ps = connection.prepareStatement(sql);
            ps.setString(1, tableName);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("column_name"), rs.getString("data_type"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return map;


    }

    public synchronized static String getTypeDataBase(final Class type) {

        if (type == Long.class || type == long.class) {
            return "integer";
        } else if (type == Integer.class || type == int.class) {
            return "integer";
        } else if (type == Float.class || type == float.class) {
            return "decimal";
        } else if (type == Double.class || type == double.class) {
            return "real";
        } else if (type == Boolean.class || type == boolean.class) {
            return "boolean";
        } else if (type == String.class || type == char.class) {
            return "text";
        } else if (type == byte[].class) {
            return "bytea";
        } else {
            new Error("Not suportend type: " + type);
        }

        return null;
    }

    public synchronized static Class getTypeDataJava(final String type) {

        if ("integer".equals(type)) {
            return Long.class;
        } else if ("decimal".equals(type)) {
            return Float.class;
        } else if ("real".equals(type)) {
            return Double.class;
        } else if ("boolean".equals(type)) {
            return Boolean.class;
        } else if ("text".equals(type)) {
            return String.class;
        } else if ("bytea".equals(type)) {
            return byte[].class;
        } else {
            new Error("Not suportend type: " + type);
        }

        return null;
    }

    public synchronized static PreparedStatement preparedStatementUpdate(String schema, Connection connection, List<Model> models,
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

    public static synchronized boolean isTableExist(Connection connection, String schema, String table) {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT to_regclass('"+schema+"."+table+"') is not null");

            if (rs.next()) {
                return rs.getBoolean(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;

    }


}

