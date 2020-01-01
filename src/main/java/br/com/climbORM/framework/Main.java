package br.com.climbORM.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.test.model.*;

public class Main {

	public static void main(String[] args) throws IOException {

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		Pessoa pessoa = new Pessoa();
		pessoa.setNome("Maria antonia");
		pessoa.setEnderecoComercial("Treta");
		pessoa.setIdade(32l);
		pessoa.setAltura(174.1325525544f);
		pessoa.setQtdQuilos(145.6144d);
		pessoa.setCasado(true);
		pessoa.setFoto(new byte[] {});

		List<Email> emails = new ArrayList<>();
		Email email = new Email();
		email.setEmail("taliba@jose.com.br");
		emails.add(email);
		pessoa.setEmails(emails);

		DynamicFields dynamicFields = FieldManager.create(Pessoa.class);
		pessoa.setDynamicFields(dynamicFields);

		pessoa.getDynamicFields().createField("cnpj", String.class);
		pessoa.getDynamicFields().createField("peso", Long.class);

		pessoa.getDynamicFields().addValue("cnpj", "2522554425555");
		pessoa.getDynamicFields().addValue("peso", 78);

		connection.save(pessoa);

	}

}
