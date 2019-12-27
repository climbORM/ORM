package br.com.climbORM.test.units;

import br.com.climbORM.framework.ClimbORM;
import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.test.model.Cidade;
import br.com.climbORM.test.model.Email;
import br.com.climbORM.test.model.Endereco;
import br.com.climbORM.test.model.Pessoa;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class climbORM {

    public static Long idPessoa;
    public static Long idCidade;
    public static Long idEndereco;

    @Test
    void insert() {

        ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
        ClimbConnection connection = factory.getConnection("localhost");

        connection.getTransaction().start();

        Cidade cidade = new Cidade();
        cidade.setNomeDaCidade("Cacoal");
        connection.save(cidade);

        assertTrue(cidade.getId() != null);

        Endereco endereco = new Endereco();
        endereco.setNomeDaRua("Rua Ji-parana");
        endereco.setCidade(cidade);
        connection.save(endereco);

        assertTrue(endereco.getId() != null);

        Pessoa pessoa = new Pessoa();
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

        connection.save(pessoa);

        idPessoa = pessoa.getId();
        assertTrue(idPessoa != null);

        connection.getTransaction().commit();
    }

    @Test
    void printaID() {
        System.out.println("ID PESSOA: " + idPessoa);
    }

    @Test
    void select() {
        ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
        ClimbConnection connection = factory.getConnection("localhost");

        Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, idPessoa);
        assertTrue(pessoa != null);
        assertTrue(pessoa.getEmails().size() > 0);
        assertTrue(pessoa.getId() != null);
        assertTrue(pessoa.getEndereco() != null);
        assertTrue(pessoa.getEndereco().getId() != null);
        assertTrue(pessoa.getEndereco().getCidade() != null);
        assertTrue(pessoa.getEndereco().getCidade().getId() != null);

        connection.close();

        connection = factory.getConnection("localhost");
        List<Pessoa> pessoas = connection.find(Pessoa.class, "where id = " + idPessoa.toString());

        assertTrue(pessoas != null);
        assertTrue(pessoas.size() > 0);

        for(Pessoa pessoa1 : pessoas) {
            assertTrue(pessoa1 != null);
            assertTrue(pessoa1.getEmails().size() > 0);
            assertTrue(pessoa1.getId() != null);
            assertTrue(pessoa1.getEndereco() != null);
            assertTrue(pessoa1.getEndereco().getId() != null);
            assertTrue(pessoa1.getEndereco().getCidade() != null);
            assertTrue(pessoa1.getEndereco().getCidade().getId() != null);
        }

    }

    void update() {

        ManagerFactory factory = ClimbORM.createManagerFactory("climb.properties");
        ClimbConnection connection = factory.getConnection("localhost");

        connection.getTransaction().start();

        Cidade cidade = new Cidade();
        cidade.setNomeDaCidade("Cacoal");
        connection.save(cidade);

        assertTrue(cidade.getId() != null);

        Endereco endereco = new Endereco();
        endereco.setNomeDaRua("Rua Ji-parana");
        endereco.setCidade(cidade);
        connection.save(endereco);

        assertTrue(endereco.getId() != null);

        Pessoa pessoa = new Pessoa();
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

        connection.save(pessoa);

        idPessoa = pessoa.getId();
        assertTrue(idPessoa != null);

    }

}
