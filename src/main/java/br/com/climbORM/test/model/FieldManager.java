package br.com.climbORM.test.model;

import br.com.climbORM.framework.DynamicField;
import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.utils.ReflectionUtil;
import br.com.climbORM.framework.utils.SqlUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class FieldManager implements DynamicField {

    private Class type;

    private Map<String, Class> newFields;
    private Map<String, Object> valueFields;

    {
        newFields = new HashMap<>();
        valueFields = new HashMap<>();
    }

    private FieldManager(Class type) {
        this.type = type;
    }

    public static DynamicField create(Class type) {
        return new FieldManager(type);
    }

    @Override
    public Object getValue(String fieldName) {
        return this.valueFields.get(fieldName);
    }

    @Override
    public void createField(String name, Class type) {
        this.newFields.put(name, type);
    }

    @Override
    public void addValue(String name, Object value) {
        this.valueFields.put(name, value);
    }


}
