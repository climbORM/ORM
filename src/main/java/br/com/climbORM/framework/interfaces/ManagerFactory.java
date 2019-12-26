package br.com.climbORM.framework.interfaces;

public interface ManagerFactory {

	public ClimbConnection getConnection(String schemaName);
	
}
