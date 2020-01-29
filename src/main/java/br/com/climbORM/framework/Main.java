package br.com.climbORM.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.framework.interfaces.ResultIterator;
import br.com.climbORM.test.model.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Main {

	public static void main(String[] args) throws IOException {

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, 294l);

		assertTrue(pessoa.getId() != null);

		pessoa.getDynamicFields().createField("nome_do_gato3", String.class);
		connection.update(pessoa);
		connection.close();

		connection = factory.getConnection("localhost");
		pessoa = (Pessoa) connection.findOne(Pessoa.class, 294l);
		pessoa.getDynamicFields().addValue("nome_do_gato3","gatinho");
		connection.update(pessoa);
		connection.close();

	}

}
