package br.com.climbORM.framework.interfaces;

public interface DynamicFields {

    public Object getValue(String fieldName);

    public void addValue(String fieldName, Object value);

    public void createField(String name, Class type);

}
