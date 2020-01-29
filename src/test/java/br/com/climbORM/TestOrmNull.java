package br.com.climbORM;

import br.com.climbORM.framework.ClimbORM;
import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.framework.interfaces.ResultIterator;
import br.com.climbORM.test.model.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestOrmNull {

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
        connection.save(cidade);

        idCidade = cidade.getId();
        assertTrue(idCidade != null && idCidade > 0);

        Endereco endereco = new Endereco();
        endereco.setCidade(cidade);
        connection.save(endereco);

        idEndereco = endereco.getId();
        assertTrue(idEndereco != null);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Taliba jose");
        pessoa.setAltura(10f);
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
        assertTrue(pessoa.getId() != null);
        assertTrue(pessoa.getEndereco() == null);

        connection.close();

        connection = factory.getConnection("localhost");
        ResultIterator iterator = connection.find(Pessoa.class, "where id = " + idPessoa.toString());

        assertTrue(iterator != null);

        while (iterator.next()) {
            Pessoa pessoa1 = (Pessoa) iterator.getObject();

            assertTrue(pessoa1 != null);
            assertTrue(pessoa1.getId() != null);
            assertTrue(pessoa1.getEndereco() == null);
        }

        ResultIterator resultIterator = connection.findWithQuery(RespostaQuery.class, "SELECT " +
                "p.nome, e.nome_da_rua, p.id_endereco, c.nome_da_cidade, p.lista_emails " +
                "FROM localhost.tb_pessoa p \n" +
                "INNER JOIN localhost.tb_endereco e on p.id_endereco = e.id\n" +
                "INNER JOIN localhost.tb_cidade c on e.id_cidade = c.id\n" +
                "where e.id_cidade > 1 and p.lista_emails is not null");

        while(resultIterator.next()) {
			RespostaQuery RespostaQuery = (RespostaQuery) resultIterator.getObject();
            assertTrue(RespostaQuery != null);
            assertTrue(RespostaQuery.getNome() != null);
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
        assertTrue(cidade.getId().equals(idCidade));

        Endereco endereco = (Endereco) connection.findOne(Endereco.class, idEndereco);
        endereco.setNomeDaRua("Rua Taliba");
        connection.update(endereco);
        assertTrue(endereco.getId().equals(idEndereco));

        Pessoa pessoa = (Pessoa) connection.findOne(Pessoa.class, idPessoa);
        pessoa.setNome("Maria update");
        pessoa.setEnderecoComercial("update");

        pessoa.setEmails(new ArrayList<>());
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
        assertTrue(pessoa.getEmails().size() == 1);
        assertTrue(pessoa.getId().equals(idPessoa));
        assertTrue(pessoa.getNome().equals("Maria update"));

        assertTrue(pessoa.getEnderecoComercial().equals("update"));
        assertTrue(pessoa.getEndereco() == null);

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
