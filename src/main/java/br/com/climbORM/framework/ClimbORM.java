package br.com.climbORM.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.com.climbORM.framework.interfaces.ManagerFactory;

public class ClimbORM {
	
	public static ManagerFactory createManagerFactory(String nameConfiguration) {
		
		Properties properties = new Properties();
		InputStream inputStream = ClimbORM.class.getClassLoader().getResourceAsStream(nameConfiguration);
		
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new Error("property file '" + nameConfiguration + "' not found in the classpath");
		}
		
		return new Manager(properties);
		
	}
	
}
