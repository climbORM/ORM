package br.com.climbORM.framework;

import br.com.climbORM.framework.interfaces.DynamicFields;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicFieldsEntity implements DynamicFields {

    private Class type;

    public Set<String> getDropFields() {
        return dropFields;
    }

    public void setDropFields(Set<String> dropFields) {
        this.dropFields = dropFields;
    }

    private Set<String> dropFields;

    private Map<String, Class> newFields;

    private Map<String, Object> valueFields;

    {
        newFields = new HashMap<>();
        valueFields = new HashMap<>();
        dropFields = new HashSet<>();
    }

    private DynamicFieldsEntity(Class type) {
        this.type = type;
    }

    public static DynamicFields create(Class type) {
        return new DynamicFieldsEntity(type);
    }

    @Override
    public Object getValue(String fieldName) {
        return this.valueFields.get(fieldName.toLowerCase());
    }

    @Override
    public void createField(String name, Class type) {
        this.newFields.put(name.toLowerCase(), type);
    }

    @Override
    public void dropField(String name) {
        this.dropFields.add(name);
    }

    @Override
    public void addValue(String name, Object value) {
        this.valueFields.put(name, value);
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Map<String, Class> getNewFields() {
        return newFields;
    }

    public void setNewFields(Map<String, Class> newFields) {
        this.newFields = newFields;
    }

    public Map<String, Object> getValueFields() {
        return valueFields;
    }

    public void setValueFields(Map<String, Object> valueFields) {
        this.valueFields = valueFields;
    }

}
