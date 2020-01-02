package br.com.climbORM.framework;

import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.interfaces.FieldsManager;
import br.com.climbORM.framework.mapping.Json;
import br.com.climbORM.framework.mapping.Relation;
import br.com.climbORM.framework.mapping.Transient;
import br.com.climbORM.framework.utils.ModelDynamicField;
import br.com.climbORM.framework.utils.ModelTableField;
import br.com.climbORM.framework.utils.ReflectionUtil;
import br.com.climbORM.framework.utils.SqlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicFieldsManager implements FieldsManager {

    private Connection connection;
    private String schema;
    private Map<String, List<ModelDynamicField>> tableOfDynamicFields;
//    private Map<Long, String> dynamicTables;

    {
        tableOfDynamicFields = new HashMap<>();
//        dynamicTables = new HashMap<>();
    }

    public DynamicFieldsManager(Connection connection, String schema) {
        this.connection = connection;
        this.schema = schema;

        loadAllDynamicTables();

    }

    private String getTableNameDynamic(Object object) {
        return ReflectionUtil.getTableName(object) + "_dynamic";
    }

    private void loadAllDynamicTables() {

        try {

            if (SqlUtil.isTableExist(this.connection,this.schema, "tb_dynamic_tables")) {

                String sql = "SELECT id, table_name FROM " + this.schema + ".tb_dynamic_tables;";

                ResultSet resultSet = null;

                try {

                    Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
                    resultSet = stmt.executeQuery(sql);

                    while (resultSet.next()) {
                        String tableName = resultSet.getString("table_name");
                        loadDynamicFieldsTable(tableName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        resultSet.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                System.out.println("Carregou tabelas: " + this.tableOfDynamicFields.size());

                return;
            }

            final String sql = "CREATE TABLE " + this.schema +".tb_dynamic_tables \n" +
                    "(\n" +
                    "    id serial NOT NULL,\n" +
                    "    table_name text NOT NULL,\n" +
                    "    PRIMARY KEY (id)\n" +
                    ")";

            final Statement statement = this.connection.createStatement();
            statement.execute(sql);

            System.out.println("criou tb_dynamic_tables");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void createDynamicTable(Object object) {

        try {

            final String tableName = getTableNameDynamic(object);

            if (this.tableOfDynamicFields.get(tableName) != null) {
                return;
            }

            final String sql = "CREATE TABLE localhost." + tableName +"\n" +
                    "(\n" +
                    "    id serial NOT NULL,\n" +
                    "    table_name text NOT NULL,\n" +
                    "    id_record bigint NOT NULL,\n" +
                    "    PRIMARY KEY (id)\n" +
                    ")";

            final Statement statement = this.connection.createStatement();

            statement.execute(sql);
            statement.execute("INSERT INTO " + this.schema +".tb_dynamic_tables (table_name) VALUES ('" + tableName +"');");

            this.tableOfDynamicFields.put(tableName, new ArrayList<ModelDynamicField>());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDynamicFieldsTable(final String tableName) {

        Map<String, String> map = SqlUtil.getCreatedFields(tableName, this.connection);

        List<ModelDynamicField> listModel = new ArrayList<>();
        for (String fieldName : map.keySet()) {
            Class type = SqlUtil.getTypeDataJava(map.get(fieldName));
            listModel.add(new ModelDynamicField(fieldName, type));
        }

        this.tableOfDynamicFields.put(tableName, listModel);
    }

    private void createDynamicFields(Object object) {

        try {

            Field field = ReflectionUtil.getDynamicField(object);

            if (field == null) {
                return;
            }

            String tableName = getTableNameDynamic(object);

            DynamicFieldsEntity dynamicFieldsEntity = (DynamicFieldsEntity) ReflectionUtil.getValueField(field,object);

            Map<String, Class> newFields = dynamicFieldsEntity.getNewFields();

            StringBuilder builder = new StringBuilder();

            List<ModelDynamicField> listModel = this.tableOfDynamicFields.get(tableName);

            for (ModelDynamicField model : listModel) {

                Class classe = newFields.get(model.getAttribute());

                if (classe != null) {

                    classe = newFields.remove(model.getAttribute());

                    System.out.println("Removed: " + model.getAttribute());

                    if (classe == null) {
                        throw new Error("cannot remove map field" + model.getAttribute());
                    }

                }


            }

            boolean add = false;
            for (String fieldName : newFields.keySet()) {
                String type = SqlUtil.getTypeDataBase(newFields.get(fieldName));

                System.out.println("Campo: " + fieldName);
                System.out.println("Tipo do banco: " + type);

                builder.append("ALTER TABLE " + this.schema +"." + tableName +"\n" +
                        "\tADD COLUMN " + fieldName + " " + type + ";\n");

                add = true;

            }

            if (add) {
                Statement statement = this.connection.createStatement();
                statement.execute(builder.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Object object) {

        try {
            createDynamicTable(object);
            createDynamicFields(object);

            Field field = ReflectionUtil.getDynamicField(object);

            if (field == null) {
                return;
            }

            DynamicFieldsEntity dynamicFieldsEntity = (DynamicFieldsEntity) ReflectionUtil.getValueField(field,object);
            Map<String, Object> newFields = dynamicFieldsEntity.getValueFields();

            StringBuilder attributes = new StringBuilder();
            StringBuilder values = new StringBuilder();

            attributes.append("table_name,");
            attributes.append("id_record,");

            values.append("?,");
            values.append("?,");

            for (String fieldName : newFields.keySet()) {
                attributes.append(fieldName + ",");
                values.append("?,");
            }

            String tableName = getTableNameDynamic(object);

            String sql = "INSERT INTO " + schema + "." + tableName + "("
                    + attributes.toString().substring(0, attributes.toString().length() - 1) + ") VALUES ("
                    + values.toString().substring(0, values.toString().length() -1) + ") RETURNING ID";

            System.out.println(sql);

            PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ReflectionUtil.getTableName(object));
            preparedStatement.setLong(2, ((PersistentEntity)object).getId());

            PreparedStatement ps = SqlUtil.getPreparedStatementDynamicFields(preparedStatement,newFields,2);
            ps.executeUpdate();

            ResultSet rsID = ps.getGeneratedKeys();
            if (rsID.next()) {
                Long id = rsID.getLong("id");

                if (id == null) {
                    throw new Error("ERROR... Not insert in Dynamic table: " + tableName);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(Object object) {

    }

    @Override
    public void findOne(Object object) {

        try {

            String tableName = getTableNameDynamic(object);

            System.out.println("aki");

            Field field = ReflectionUtil.getDynamicField(object);
            DynamicFields dynamicFields = DynamicFieldsEntity.create(object.getClass());
            ReflectionUtil.setValueField(field,object,dynamicFields);

            List<ModelDynamicField> modelDynamicFields = this.tableOfDynamicFields.get(tableName);

            StringBuilder atributes = new StringBuilder();
            for (ModelDynamicField modelDynamicField : modelDynamicFields) {
                atributes.append(modelDynamicField.getAttribute()+",");
            }

            final String sql = "SELECT " + atributes.toString().substring(0, atributes.toString().length() - 1) + " FROM " +
                    this.schema + "."+ tableName + " WHERE id_record="+((PersistentEntity)object).getId().toString();

            System.out.println(sql);

            ResultSet resultSet = null;
            Map<String, Object> mapValue = new HashMap<>();
            ((DynamicFieldsEntity)dynamicFields).setValueFields(mapValue);

            try {

                Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
                resultSet = stmt.executeQuery(sql);

                while (resultSet.next()) {
                    loadObject(modelDynamicFields, mapValue, resultSet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadObject(List<ModelDynamicField> modelDynamicFields, Map<String, Object> mapValue, ResultSet resultSet) {

        for (ModelDynamicField field : modelDynamicFields) {

            if (field.getType() == Long.class) {
                try {

                    Long value = resultSet.getLong(field.getAttribute());
                    mapValue.put(field.getAttribute(), value);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (field.getType() == Integer.class || field.getType() == int.class) {
                try {

                    Long value = resultSet.getLong(field.getAttribute());
                    mapValue.put(field.getAttribute(), value);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == Float.class || field.getType() == float.class) {
                try {

                    Long value = resultSet.getLong(field.getAttribute());
                    mapValue.put(field.getAttribute(), value);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == Double.class || field.getType() == double.class) {

                try {

                    Long value = resultSet.getLong(field.getAttribute());
                    mapValue.put(field.getAttribute(), value);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                try {

                    try {

                        Long value = resultSet.getLong(field.getAttribute());
                        mapValue.put(field.getAttribute(), value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == String.class || field.getType() == char.class) {
                try {

                    try {
                        String value = resultSet.getString(field.getAttribute());
                        mapValue.put(field.getAttribute(), value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == byte[].class) {
                try {

                    try {

                        byte[] value = resultSet.getBytes(field.getAttribute());
                        mapValue.put(field.getAttribute(), value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
