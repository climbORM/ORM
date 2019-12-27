package br.com.climbORM.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.test.model.Cidade;
import br.com.climbORM.test.model.Email;
import br.com.climbORM.test.model.Endereco;
import br.com.climbORM.test.model.Pessoa;

public class Main {

	public static void main(String[] args) throws IOException {

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		List<Pessoa> pessoas = connection.find(Pessoa.class, "WHERE ID>0"); //findOne(Pessoa.class, 121l);


		for (Pessoa pessoa : pessoas) {
			System.out.println(pessoa.getNome());
//			System.out.println(pessoa.getEndereco().getNomeDaRua());

			System.out.println(pessoa.getFoto());
		}



		if (true) {
			return;
		}
//
//		System.out.println("GetID: " + pessoa.getId());
//		System.out.println("Get Nome: " + pessoa.getNome());
//		System.out.println("Get Endereço: " + pessoa.getEndereco().getId());
//
//		pessoa.setNome("Maria update");
//		connection.update(pessoa);

//		System.out.println("Get foto: " + pessoa.getFoto());
////		
//		System.out.println("Get ID rua: " + pessoa.getEndereco().getId());
//		System.out.println("Get Nome rua: " + pessoa.getEndereco().getNomeDaRua());
//		
//		System.out.println("Get ID cidade: " + pessoa.getEndereco().getCidade().getId());
//		System.out.println("Get Nome cidade: " + pessoa.getEndereco().getCidade().getNomeDaCidade());
//

		System.out.println("******************************");


	}

}
