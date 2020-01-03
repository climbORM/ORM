package br.com.climbORM.framework.interfaces;

import java.sql.SQLException;

public interface FieldsManager {

    public void save(Object object) throws Exception;

    public void update(Object object);

    public void delete(Object object) throws SQLException;

    public void delete(String tableName, String where) throws SQLException;

    public void findOne(Object object);

}
