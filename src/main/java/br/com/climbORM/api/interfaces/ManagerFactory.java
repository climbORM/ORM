package br.com.climbORM.api.interfaces;

public interface ManagerFactory {

	public ClimbConnection getConnection(String schemaName);
	
}
