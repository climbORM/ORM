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

		//cria o campo o campo dinanico
		DynamicFields dynamicFields = DynamicFieldsEntity.create(Empresa.class);
		dynamicFields.createField("nome_do_nome2", String.class);
		connection.createDynamicField(dynamicFields);

		connection.close();

		connection = factory.getConnection("localhost");

		Empresa empresa = new Empresa();
		dynamicFields = DynamicFieldsEntity.create(Empresa.class);
		dynamicFields.addValue("nome_do_nome2","Taliba andrade");
		empresa.setDynamicFields(dynamicFields);

		connection.save(empresa);
		connection.close();

		connection = factory.getConnection("localhost");

		//apaga o campo dinamico somente
		dynamicFields = DynamicFieldsEntity.create(Empresa.class);
		dynamicFields.dropField("nome_do_nome2");
		connection.dropDynamicField(dynamicFields);

		empresa = (Empresa) connection.findOne(Empresa.class, empresa.getId());

		boolean apagou = true;
		for (String fieldName : empresa.getDynamicFields().getValueFields().keySet()) {
			if (fieldName.equals("nome_do_nome2")) {
				apagou = false;
				break;
			}
		}

		System.out.println(apagou);

	}

}
