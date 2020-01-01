package br.com.climbORM.framework;

import br.com.climbORM.framework.interfaces.ResultIterator;
import br.com.climbORM.framework.mapping.*;
import br.com.climbORM.framework.utils.ModelTableField;
import br.com.climbORM.framework.utils.ReflectionUtil;
import br.com.climbORM.framework.utils.SqlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LazyLoader implements ResultIterator {

    public final static String QUERY_RESULT = "QueryResult";
    public final static String ENTITY = "Entity";

    private Connection connection;
    private String schema;
    private Class classe;
    private ResultSet resultSet;
    private String sql;
    private Object object;

    public LazyLoader(Connection connection, String schema) {
        this.connection = connection;
        this.schema = schema;
    }

    public LazyLoader(Connection connection, String schema, Class classe, String sql, String typeQuery) {
        this.connection = connection;
        this.schema = schema;
        this.sql = sql;
        this.classe = classe;

        if (typeQuery.equals(QUERY_RESULT)) {
            findWithQueryExecute();
        } else if (typeQuery.equals(ENTITY)) {
            findWithWhereQueryExecute();
        } else {
            throw new Error("Invalid typeQuery : " + typeQuery);
        }

    }

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public boolean next() {

        try {
            if (this.resultSet.next()) {
                this.object = newEnhancer(this.classe).create();
                Field[] fields = this.object.getClass().getSuperclass().getDeclaredFields();
                loadObject(fields, this.object, this.resultSet);
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();;
        }

        return false;
    }


    private void findWithQueryExecute() {

        final QueryResult QueryResult = (QueryResult) classe.getAnnotation(QueryResult.class);

        if (QueryResult == null) {
            throw new Error(classe.getName() + " not is QueryResult");
        }

        System.out.println(sql);

        try {

            Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
            this.resultSet = stmt.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void findWithWhereQueryExecute() {

        final Entity entity = (Entity) this.classe.getAnnotation(Entity.class);

        if (entity == null) {
            throw new Error(this.classe.getName() + " not is Entity");
        }

        final StringBuilder atributes = getAtributes(this.classe);

        final String sql = "SELECT id," + atributes.toString().substring(0, atributes.toString().length() - 1) + " FROM "
                + entity.name() + " " + this.sql;

        System.out.println(sql);

        ArrayList objects = null;

        try {

            Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
            this.resultSet = stmt.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Object loadLazyObject(Class classe, Long id) {

        final Entity entity = (Entity) classe.getAnnotation(Entity.class);

        if (entity == null) {
            throw new Error(classe.getName() + " not is Entity");
        }

        final StringBuilder atributes = getAtributes(classe);

        final String sql = "SELECT id," + atributes.toString().substring(0, atributes.toString().length() - 1) + " FROM " +
                this.schema + "."+ entity.name() + " WHERE ID="+id.toString();

        System.out.println(sql);

        Object object = null;

        ResultSet resultSet = null;

        try {

            Statement stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 1);
            resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {

                final long localid = resultSet.getLong("id");

                object = newEnhancer(classe, entity.name(), localid).create();
                ((PersistentEntity) object).setId(localid);
                Field[] fields = object.getClass().getSuperclass().getDeclaredFields();
                loadObject(fields, object, resultSet);

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

        return object;
    }

    private StringBuilder getAtributes(Class classe) {
        List<ModelTableField> modelTableFields = null;
        try {
            modelTableFields = ReflectionUtil.generateModel(classe.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder atributes = new StringBuilder();
        for (ModelTableField modelTableField : modelTableFields) {

            if (modelTableField.getType() == byte[].class) {
                continue;
            }

            atributes.append(modelTableField.getAttribute() + ",");
        }

        return atributes;
    }

    private Enhancer newEnhancer(Class classe) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(classe);
        enhancer.setCallback(new MethodInterceptor() {

            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

                Object inst = proxy.invokeSuper(obj, args);

                if (inst != null) {

                    if (inst.getClass().getAnnotation(Entity.class) != null) {
                        Long id = ((PersistentEntity) inst).getId();
                        return loadLazyObject(inst.getClass(), id);
                    }

                }

                return proxy.invokeSuper(obj, args);
            }
        });

        return enhancer;
    }

    private Enhancer newEnhancer(Class classe, String entity, long id) {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(classe);
        enhancer.setCallback(new MethodInterceptor() {

            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

                Object inst = proxy.invokeSuper(obj, args);

                if (inst != null) {

                    if (inst.getClass().getAnnotation(Entity.class) != null) {
                        Long id = ((PersistentEntity) inst).getId();
                        return loadLazyObject(inst.getClass(), id);
                    }

                    if (inst.getClass() == byte[].class) {
                        byte[] b = (byte[]) inst;
                        String fieldName = new String(b);
                        return SqlUtil.getBinaryValue(id, fieldName, entity, connection);
                    }

                }

                return proxy.invokeSuper(obj, args);
            }
        });

        return enhancer;
    }

    private void loadObject(Field[] fields, Object object, ResultSet resultSet) {
        for (Field field : fields) {

            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Relation.class)) {

                try {

                    Object instance = Class.forName(field.getGenericType().getTypeName()).newInstance();

                    String fieldName = ReflectionUtil.getFieldName(field);
                    Long value = resultSet.getLong(fieldName);

                    PersistentEntity persistentEntity = (PersistentEntity) instance;
                    persistentEntity.setId(value);

                    Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                            .invoke(object, instance);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                continue;
            }

            if (object.getClass().getSuperclass().getSuperclass() == PersistentEntity.class) {
                try {
                    ((PersistentEntity)object).setId(resultSet.getLong("id"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (field.getType() == Long.class) {
                try {

                    String fieldName = ReflectionUtil.getFieldName(field);
                    Long value = resultSet.getLong(fieldName);
                    Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                            .invoke(object, value);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (field.getType() == Integer.class || field.getType() == int.class) {
                try {

                    String fieldName = ReflectionUtil.getFieldName(field);
                    Integer value = resultSet.getInt(fieldName);
                    Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                            .invoke(object, value);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == Float.class || field.getType() == float.class) {
                try {

                    String fieldName = ReflectionUtil.getFieldName(field);
                    Float value = resultSet.getFloat(fieldName);
                    Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                            .invoke(object, value);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == Double.class || field.getType() == double.class) {

                try {

                    String fieldName = ReflectionUtil.getFieldName(field);
                    Double value = resultSet.getDouble(fieldName);
                    Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                            .invoke(object, value);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                try {

                    try {

                        String fieldName = ReflectionUtil.getFieldName(field);
                        Boolean value = resultSet.getBoolean(fieldName);
                        Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                                .invoke(object, value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == String.class || field.getType() == char.class) {
                try {

                    try {


                        String fieldName = ReflectionUtil.getFieldName(field);
                        String value = resultSet.getString(fieldName);
                        Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                                .invoke(object, value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == byte[].class) {
                try {

                    try {

                        String fieldName = ReflectionUtil.getFieldName(field);
                        byte[] value = fieldName.getBytes();
                        Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                                .invoke(object, value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (field.getType() == List.class && field.isAnnotationPresent(Json.class)) {
                try {

                    try {
                        ObjectMapper mapper = new ObjectMapper();

                        Class jsonType = (Class) ((ParameterizedType) field.getGenericType())
                                .getActualTypeArguments()[0];

                        String fieldName = ReflectionUtil.getFieldName(field);
                        String json = resultSet.getString(fieldName);

                        if (json != null && json.trim().length() > 0) {
                            ArrayList value = mapper.readValue(json,
                                    mapper.getTypeFactory().constructCollectionType(List.class, jsonType));

                            Object rs = new PropertyDescriptor(field.getName(), object.getClass()).getWriteMethod()
                                    .invoke(object, value);
                        }

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
