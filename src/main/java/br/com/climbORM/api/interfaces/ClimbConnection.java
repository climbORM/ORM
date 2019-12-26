package br.com.climbORM.api.interfaces;

public interface ClimbConnection {

	public void transactionStart();

	public void transactionCommit();

	public void transactionRolback();

	public void save(Object object);

	public void update(Object object);

	public void delete(Object object);

	public void delete(Class object, String where);

	public Object findOne(Class classe, Long id);
	
	public void close();

//	public ClimbConnection where(String where);

}
