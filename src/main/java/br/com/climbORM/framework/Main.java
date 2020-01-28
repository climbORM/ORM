package br.com.climbORM.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.framework.interfaces.ResultIterator;
import br.com.climbORM.test.model.*;

public class Main {

	public static void main(String[] args) throws IOException {

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		connection.getTransaction().start();
		ResultIterator pessoasIterator = connection.find(Pessoa.class, "where id > 0");

		Pessoa pessoa = new Pessoa();
		pessoa.setAltura(10f);
		pessoa.setNome("Taliba jose");

		DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
		pessoa.setDynamicFields(dynamicFields);

		pessoa.getDynamicFields().createField("nome_do_cachorro", String.class);

		connection.save(pessoa);
		connection.getTransaction().commit();
		connection.close();


		System.out.println(pessoa.getId());

		//******************************

		connection = factory.getConnection("localhost");

		connection.getTransaction().start();
		pessoa = (Pessoa) connection.findOne(Pessoa.class, pessoa.getId());
		dynamicFields = pessoa.getDynamicFields();

		pessoa.getDynamicFields().addValue("nome_do_cachorro","Dog");

		connection.update(pessoa);
		connection.getTransaction().commit();
		connection.close();

		//******************************
		System.out.println("***********************************************");

		connection = factory.getConnection("localhost");
		pessoa = (Pessoa) connection.findOne(Pessoa.class, pessoa.getId());
		dynamicFields = pessoa.getDynamicFields();

		System.out.println("nome: " + pessoa.getNome());
		System.out.println("Nome do cachorro: " + dynamicFields.getValue("nome_do_cachorro"));
		connection.close();

	}

}
