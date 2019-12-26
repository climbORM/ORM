package br.com.climbORM.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.climbORM.api.interfaces.ClimbConnection;
import br.com.climbORM.api.interfaces.ManagerFactory;
import br.com.climbORM.teste.model.Email;
import br.com.climbORM.teste.model.Pessoa;

public class Main {

	public static void main(String[] args) throws IOException {

//		ObjectMapper mapper = new ObjectMapper();
//
//		List<Email> emails = new ArrayList<Email>();
//
//		Email email = new Email();
//		email.setEmail("teste@teste.com.br");
//		
//		emails.add(email);
//		
//		System.out.println(emails.getClass() == ArrayList.class);
//		
//		ArrayList templist = (ArrayList)emails;
//		
//		List<Object> objects = new ArrayList<>();
//		
//		for (Object object: templist) {
//			objects.add(objects);
//		}
//		
//		String json = mapper.writeValueAsString(templist);
////		System.out.println(json);
//		
//		Class cs = Email[].class;
//		ArrayList e = (ArrayList) mapper.readValue(json, cs);
//		
//		
//		
//		/////////////////
//		
//		String[] emails1 = json.substring(1, json.length() - 1).split(",");
//		
//		ArrayList temp = new ArrayList<>();
//		for (String emailx : emails1) {
//			System.out.println(email);
//			
//			Email e = mapper.readValue(emailx, Email.class);
//			System.out.println(e.getEmail());
//			
//			temp.add(e);
//		}
////		
////		
////		
//		Pessoa pessoa = new Pessoa();
//		pessoa.setEmails(e);
////		
////		for (Email emails11 : pessoa.getEmails()) {
////			System.out.println(emails11.getEmail());
////		}
//		
////		String jsonString = mapper.writeValueAsString(emails);
//		
//		
//		if (true) return;

		ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
		ClimbConnection connection = factory.getConnection("localhost");

		Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, 120l);
		
		for (Email email : pessoa.getEmails()) {
			System.out.println(email.getEmail());
		}
		
		
//		System.out.println("GetID: " + pessoa.getId());
//		System.out.println("Get Nome: " + pessoa.getNome());
//		System.out.println("Get foto: " + pessoa.getFoto());
////		
//		System.out.println("Get ID rua: " + pessoa.getEndereco().getId());
//		System.out.println("Get Nome rua: " + pessoa.getEndereco().getNomeDaRua());
//		
//		System.out.println("Get ID cidade: " + pessoa.getEndereco().getCidade().getId());
//		System.out.println("Get Nome cidade: " + pessoa.getEndereco().getCidade().getNomeDaCidade());
//		
//		connection.transactionStart();
//		
//		Cidade cidade = new Cidade();
//		cidade.setNomeDaCidade("Cacoal");
//		connection.save(cidade);
//		System.out.println(cidade.getId());
////		
//		Endereco endereco = new Endereco();
//		endereco.setNomeDaRua("Rua Ji-parana");
//		endereco.setCidade(cidade);
//		connection.save(endereco);
//		System.out.println(endereco.getId());
////		
//		Pessoa pessoa = new Pessoa();
//		pessoa.setNome("Maria antonia");
//		pessoa.setEnderecoComercial("Treta");
//		pessoa.setIdade(32l);
//		pessoa.setAltura(174.1325525544f);
//		pessoa.setQtdQuilos(145.6144d);
//		pessoa.setCasado(true);
//		pessoa.setEndereco(endereco);
//		pessoa.setFoto(new byte[] {});
//
		
//
//		pessoa.setEmails(emails);
//
//		connection.save(pessoa);
//		
//		System.out.println(pessoa.getId());
//		
//		connection.transactionCommit();

	}

}
