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

	public ArrayList find(Class classe, String where);

	public Where find(Class classe);

	public void close();

//	public ClimbConnection where(String where);

}
