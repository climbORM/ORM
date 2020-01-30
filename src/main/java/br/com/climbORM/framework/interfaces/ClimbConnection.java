package br.com.climbORM.framework.interfaces;

import java.util.ArrayList;
import java.util.List;

public interface ClimbConnection {

	public Transaction getTransaction();

	public void save(Object object);

	public void update(Object object);

	public void delete(Object object);

	public void delete(Class object, String where);

	public Object findOne(Class classe, Long id);

	public ResultIterator find(Class classe, String where);

	public ResultIterator findWithQuery(Class classe, String sql);

	public void close();

	public void createDynamicField(DynamicFields dynamicFields);

	public void dropDynamicField(DynamicFields dynamicFields);

//	public ClimbConnection where(String where);

}
