package br.com.climbORM.framework.interfaces;

import java.util.Map;

public interface DynamicFields {

    public Object getValue(String fieldName);

    public void addValue(String fieldName, Object value);

    public void createField(String name, Class type);

    public void dropField(String name);

    public Map<String, Object> getValueFields();
}
