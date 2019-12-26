package br.com.climbORM.framework.interfaces;

public interface ClimbConnection {

	public Transaction getTransaction();

	public void save(Object object);

	public void update(Object object);

	public void delete(Object object);

	public void delete(Class object, String where);

	public Object findOne(Class classe, Long id);
	
	public void close();

//	public ClimbConnection where(String where);

}
