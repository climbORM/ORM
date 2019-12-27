package br.com.climbORM.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.test.model.*;

public class Main {

	public static void main(String[] args) throws IOException {

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		//retorna apenas uma pessoa
		Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, 136l);

		//retorna uma lista de pessoas
		List<Pessoa> pessoas = connection.find(Pessoa.class, "WHERE ID > 0");

		//retorna atraves de query
		List<RespostaQuery> respostaQuerys = connection.findWithQuery(RespostaQuery.class, "SELECT " +
				"p.nome, e.nome_da_rua, p.id_endereco, c.nome_da_cidade, p.lista_emails " +
				"FROM localhost.tb_pessoa p \n" +
				"INNER JOIN localhost.tb_endereco e on p.id_endereco = e.id\n" +
				"INNER JOIN localhost.tb_cidade c on e.id_cidade = c.id\n" +
				"where e.id_cidade > 1 and p.lista_emails is not null");

		for (RespostaQuery resp : respostaQuerys) {
			System.out.println(resp.getNome());
			System.out.println(resp.getEndereco().getNomeDaRua());

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
