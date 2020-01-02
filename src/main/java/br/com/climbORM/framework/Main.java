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

		ResultIterator pessoasIterator = connection.find(Pessoa.class, "where id > 0");

//		while (pessoasIterator.next()) {
//
//			Pessoa pessoa = (Pessoa) pessoasIterator.getObject();
//			System.out.println(pessoa.getNome());
//
//			for(String fieldName : pessoa.getDynamicFields().getValueFields().keySet()) {
//				System.out.println(fieldName + " = " +pessoa.getDynamicFields().getValue(fieldName));
//			}
//
//			System.out.println("=======================");
//		}


		Pessoa pessoa = new Pessoa();
		pessoa.setNome("Taliba jose");

		DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
		pessoa.setDynamicFields(dynamicFields);

		pessoa.getDynamicFields().createField("NomeDoCachoroo", String.class);
		pessoa.getDynamicFields().createField("IdadeDoCachorro", Long.class);
		pessoa.getDynamicFields().createField("FotoDoCachorro", byte[].class);

		pessoa.getDynamicFields().addValue("NomeDoCachoroo","Dog");
		pessoa.getDynamicFields().addValue("IdadeDoCachorro",30);
		pessoa.getDynamicFields().addValue("FotoDoCachorro",new byte[]{});

		connection.save(pessoa);


		pessoa = (Pessoa) connection.findOne(Pessoa.class, pessoa.getId());
		dynamicFields = pessoa.getDynamicFields();

		System.out.println("nome: " + pessoa.getNome());
		System.out.println("Nome do cachorro: " + dynamicFields.getValue("NomeDoCachoroo"));

	}

}
