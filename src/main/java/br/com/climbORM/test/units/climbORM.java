package br.com.climbORM.test.units;

import br.com.climbORM.framework.ClimbORM;
import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.framework.interfaces.ResultIterator;
import br.com.climbORM.test.model.Cidade;
import br.com.climbORM.test.model.Email;
import br.com.climbORM.test.model.Endereco;
import br.com.climbORM.test.model.Pessoa;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class climbORM {

    public static Long idPessoa;
    public static Long idCidade;
    public static Long idEndereco;
    public static ManagerFactory factory;

    @Test
    @Order(0)
    void generateFactory() {
        factory = ClimbORM.createManagerFactory("climb.properties");
        assertTrue(factory != null);
    }

    @Test
    @Order(1)
    void insert() {

        ClimbConnection connection = factory.getConnection("localhost");

        connection.getTransaction().start();

        Cidade cidade = new Cidade();
        cidade.setNomeDaCidade("Cacoal");
        connection.save(cidade);

        idCidade = cidade.getId();
        assertTrue(idCidade != null);

        Endereco endereco = new Endereco();
        endereco.setNomeDaRua("Rua Ji-parana");
        endereco.setCidade(cidade);
        connection.save(endereco);

        idEndereco = endereco.getId();
        assertTrue(idEndereco != null);

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
        connection.close();
    }

    @Test
    @Order(2)
    void printaID() {
        System.out.println("ID PESSOA: " + idPessoa);
        System.out.println("ID CIDADE: " + idCidade);
        System.out.println("ID ENDERECO: " + idCidade);
    }

    @Test
    @Order(3)
    void select() {
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
        ResultIterator iterator = connection.find(Pessoa.class, "where id = " + idPessoa.toString());

        assertTrue(iterator != null);

        while (iterator.next()) {
            Pessoa pessoa1 = (Pessoa) iterator.getObject();

            assertTrue(pessoa1 != null);
            assertTrue(pessoa1.getEmails().size() > 0);
            assertTrue(pessoa1.getId() != null);
            assertTrue(pessoa1.getEndereco() != null);
            assertTrue(pessoa1.getEndereco().getId() != null);
            assertTrue(pessoa1.getEndereco().getCidade() != null);
            assertTrue(pessoa1.getEndereco().getCidade().getId() != null);
        }


        connection.close();

    }

    @Test
    @Order(4)
    void update() {
        ClimbConnection connection = factory.getConnection("localhost");

        connection.getTransaction().start();

        Cidade cidade = (Cidade) connection.findOne(Cidade.class, idCidade);
        cidade.setNomeDaCidade("Jipa");
        connection.update(cidade);
        assertTrue(cidade.getId() == idCidade);

        Endereco endereco = (Endereco) connection.findOne(Endereco.class, idEndereco);
        endereco.setNomeDaRua("Rua Taliba");
        connection.update(endereco);
        assertTrue(endereco.getId() == idEndereco);

        Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, idPessoa);
        pessoa.setNome("Maria update");
        pessoa.setEnderecoComercial("update");

        Email email = new Email();
        email.setEmail("update@update.com.br");
        pessoa.getEmails().add(email);
        connection.update(pessoa);

        System.out.println("Pessoa id: " + idPessoa);
        System.out.println("Pessoa id: " + pessoa.getId());

        assertTrue(pessoa.getId().equals(idPessoa));

        connection.getTransaction().commit();
        connection.close();

        connection = factory.getConnection("localhost");

        pessoa = (Pessoa) connection.findOne(Pessoa.class, idPessoa);

        System.out.println("size: " + pessoa.getEmails().size());

        assertTrue(pessoa != null);
        assertTrue(pessoa.getEmails().size() == 2);
        assertTrue(pessoa.getId().equals(idPessoa));
        assertTrue(pessoa.getNome().equals("Maria update"));
        assertTrue(pessoa.getIdade().equals(32l));
        assertTrue(pessoa.getAltura().equals(174.1325525544f));

        assertTrue(pessoa.getEnderecoComercial().equals("update"));
        assertTrue(pessoa.getEndereco() != null);
        assertTrue(pessoa.getEndereco().getId().equals(idEndereco));
        assertTrue(pessoa.getEndereco().getNomeDaRua().equals("Rua Taliba"));

        assertTrue(pessoa.getEndereco().getCidade() != null);
        assertTrue(pessoa.getEndereco().getCidade().getId().equals(idCidade));
        assertTrue(pessoa.getEndereco().getCidade().getNomeDaCidade().equals("Jipa"));

        connection.close();

    }

    @Test
    @Order(5)
    void deleteAllBase() {
        ClimbConnection connection = factory.getConnection("localhost");

        connection.getTransaction().start();
        connection.delete(Pessoa.class, "where id = " + idPessoa.toString());
        connection.delete(Cidade.class, "where id = " + idCidade.toString());
        connection.delete(Endereco.class, "where id = " + idEndereco.toString());

        connection.getTransaction().commit();
        connection.close();

        connection = factory.getConnection("localhost");

        assertTrue(connection.findOne(Pessoa.class, idPessoa) == null);
        assertTrue(connection.findOne(Cidade.class, idCidade) == null);
        assertTrue(connection.findOne(Endereco.class, idEndereco) == null);

        connection.close();

    }

}
