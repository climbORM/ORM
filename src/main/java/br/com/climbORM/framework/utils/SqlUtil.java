package br.com.climbORM.framework.utils;

import br.com.climbORM.framework.mapping.Json;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.PropertyDescriptor;
import java.sql.*;
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

    public synchronized static PreparedStatement preparedStatementInsert(String schema, Connection connection, List<ModelTableField> modelTableFields,
                                                            String tableName) throws Exception {

        StringBuilder atributes = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (ModelTableField modelTableField : modelTableFields) {

            atributes.append(modelTableField.getAttribute() + ",");
            if (modelTableField.getField().isAnnotationPresent(Json.class)) {
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

        return getPreparedStatement(ps, modelTableFields);

    }

    public synchronized static PreparedStatement getPreparedStatementDynamicFields(PreparedStatement ps, Map<String, Object> fields, int start) throws Exception {

        int i = start;

        for (String fieldName : fields.keySet()) {

            Object object = fields.get(fieldName);

            i += 1;
            if (object.getClass() == Long.class || object.getClass() == long.class) {
                ps.setLong(i, (Long) object);
            } else if (object.getClass() == Integer.class || object.getClass() == int.class) {
                ps.setInt(i, (int) object);
            } else if (object.getClass() == Float.class || object.getClass() == float.class) {
                ps.setFloat(i, (float) object);
            } else if (object.getClass() == Double.class || object.getClass() == double.class) {
                ps.setDouble(i, (double) object);
            } else if (object.getClass() == Boolean.class || object.getClass() == boolean.class) {
                ps.setBoolean(i, (boolean) object);
            } else if (object.getClass() == String.class || object.getClass() == char.class) {
                ps.setString(i, (String) object);
            } else if (object.getClass() == byte[].class) {
                ps.setBytes(i, (byte[]) object);
            }
        }

        return ps;
    }

    public synchronized static PreparedStatement getPreparedStatement(PreparedStatement ps, List<ModelTableField> modelTableFields) throws Exception {

        int i = 0;
        for (ModelTableField modelTableField : modelTableFields) {

            i += 1;
            if (modelTableField.getType() == Long.class || modelTableField.getType() == long.class) {
                if (ReflectionUtil.isProxedCGLIB(modelTableField.getValue())) {

                    Object value = new PropertyDescriptor("id", modelTableField.getValue().getClass().getSuperclass()).getReadMethod().invoke(modelTableField.getValue());
                    ps.setLong(i, (long) value);

                } else {
                    if (modelTableField.getValue() != null) {
                        ps.setLong(i, (long) modelTableField.getValue());
                    } else {
                        ps.setNull(i, 0);
                    }
                }
            } else if (modelTableField.getType() == Integer.class || modelTableField.getType() == int.class) {

                if (modelTableField.getValue() == null) {
                    ps.setNull(i, 0);
                } else {
                    ps.setInt(i, (int) modelTableField.getValue());
                }

            } else if (modelTableField.getType() == Float.class || modelTableField.getType() == float.class) {
                if (modelTableField.getValue() == null) {
                    ps.setNull(i,0);
                } else {
                    ps.setFloat(i, (float) modelTableField.getValue());
                }

            } else if (modelTableField.getType() == Double.class || modelTableField.getType() == double.class) {

                if (modelTableField.getValue() == null) {
                    ps.setNull(i, 0);
                } else {
                    ps.setDouble(i, (double) modelTableField.getValue());
                }

            } else if (modelTableField.getType() == Boolean.class || modelTableField.getType() == boolean.class) {

                if (modelTableField.getValue() == null) {
                    ps.setNull(i, 0);
                } else {
                    ps.setBoolean(i, (boolean) modelTableField.getValue());
                }

            } else if (modelTableField.getType() == String.class || modelTableField.getType() == char.class) {

                if (modelTableField.getValue() == null) {
                    ps.setNull(i, 0);
                } else {
                    ps.setString(i, modelTableField.getValue().toString());
                }

            } else if (modelTableField.getType() == byte[].class) {

                if (modelTableField.getValue() == null) {
                    ps.setNull(i, 0);
                } else {
                    ps.setBytes(i, (byte[]) modelTableField.getValue());
                }

            } else if (modelTableField.getType() == List.class && modelTableField.getField().isAnnotationPresent(Json.class)) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(modelTableField.getValue());
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
        } else if ("bigint".equals(type)) {
            return Long.class;
        } else if ("decimal".equals(type) || "numeric".equals(type)) {
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

    public synchronized static PreparedStatement preparedStatementUpdate(String schema, Connection connection, List<ModelTableField> modelTableFields,
                                                            String tableName, Long id) throws Exception {

        StringBuilder values = new StringBuilder();
        for (ModelTableField modelTableField : modelTableFields) {

            if (modelTableField.getField().isAnnotationPresent(Json.class)) {
                values.append(modelTableField.getAttribute() + "= ?::JSON,");
            } else {
                values.append(modelTableField.getAttribute() + "= ?,");
            }

        }

        String sql = "UPDATE " + schema + "." + tableName + " SET " + values.toString().substring(0, values.toString().length() -1) + " WHERE id = " + id.toString();

        System.out.println(sql);

        PreparedStatement ps = connection.prepareStatement(sql);

        return getPreparedStatement(ps, modelTableFields);

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

