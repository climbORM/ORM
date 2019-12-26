package br.com.climbORM.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;

public class Manager implements ManagerFactory {

	private Properties properties;
	private Map<String, ConnectionDB> mapConnections = new HashMap<String, ConnectionDB>();

	public Manager(Properties properties) {
		this.properties = properties;
	}

	public ClimbConnection getConnection(String schemaName) {

		this.properties.setProperty("currentSchema", schemaName);
		ConnectionDB connection = mapConnections.get(schemaName);
		
		if (connection == null) {
			System.out.println("Criou");
			connection = new ConnectionDB(this.properties);
			mapConnections.put(schemaName, connection);
			return connection;
		}
		
		if (connection.isClosedConnection()) {
			System.out.println("usou o objeto");
			connection.createConnectionDB();
		}
		
		return connection;
		
	}

}
