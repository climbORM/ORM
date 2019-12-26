package br.com.climbORM.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.teste.model.Cidade;
import br.com.climbORM.teste.model.Email;
import br.com.climbORM.teste.model.Endereco;
import br.com.climbORM.teste.model.Pessoa;

public class Main {

	public static void main(String[] args) throws IOException {

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, 123l);

		System.out.println(pessoa.getEmails().size());
		for (Email email : pessoa.getEmails()) {
			System.out.println(email.getEmail());
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
		connection.getTransaction().start();
//		
		Cidade cidade = new Cidade();
		cidade.setNomeDaCidade("Cacoal");
		connection.save(cidade);
		System.out.println(cidade.getId());
//
		Endereco endereco = new Endereco();
		endereco.setNomeDaRua("Rua Ji-parana");
		endereco.setCidade(cidade);
		connection.save(endereco);
		System.out.println(endereco.getId());
//
		pessoa = new Pessoa();
		pessoa.setNome("Maria antonia");
		pessoa.setEnderecoComercial("Treta");
		pessoa.setIdade(32l);
		pessoa.setAltura(174.1325525544f);
		pessoa.setQtdQuilos(145.6144d);
		pessoa.setCasado(true);
		pessoa.setEndereco(endereco);
		pessoa.setFoto(new byte[] {});


		List<Email> emails = new ArrayList<>();
		Email email = new Email();
		email.setEmail("taliba@jose.com.br");
		emails.add(email);

		pessoa.setEmails(emails);
//
		connection.save(pessoa);
//		
		System.out.println(pessoa.getId());
//		
		connection.getTransaction().commit();

	}

}
