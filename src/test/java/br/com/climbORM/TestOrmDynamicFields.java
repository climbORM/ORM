package br.com.climbORM;
import br.com.climbORM.framework.ClimbORM;
import br.com.climbORM.framework.DynamicFieldsEntity;
import br.com.climbORM.framework.interfaces.ClimbConnection;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.interfaces.ManagerFactory;
import br.com.climbORM.framework.interfaces.ResultIterator;
import br.com.climbORM.framework.mapping.DynamicField;
import br.com.climbORM.test.model.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestOrmDynamicFields {

    public static Long idDiretor;
    public static Long idEmpresa;
    public static ManagerFactory factory;

    @Test
    @Order(0)
    void generateFactory() {
        factory = ClimbORM.createManagerFactory("climb.properties");
        assertTrue(factory != null);
    }

    @Test
    @Order(1)
    void createDyanamicFields() {

        ClimbConnection connection = factory.getConnection("localhost");

        Diretor diretor = new Diretor();
        diretor.setNomeDiretor("Afranio antonio da rocha");

        DynamicFields dynamicFields = DynamicFieldsEntity.create(Diretor.class);
        diretor.setDynamicFields(dynamicFields);

        dynamicFields.createField("salario", Float.class);
        dynamicFields.addValue("salario",128.36f);

        connection.save(diretor);
        idDiretor = diretor.getId();

        assertTrue(idDiretor != null && idDiretor > 0);
        connection.close();

        connection = factory.getConnection("localhost");

        diretor = (Diretor) connection.findOne(Diretor.class, idDiretor);
        assertTrue(diretor != null && diretor.getId() > 0);

        assertTrue(diretor.getDynamicFields() != null);

        Float salario = (Float) diretor.getDynamicFields().getValue("salario");
        assertTrue(salario.equals(128.36f));

        Empresa empresa = new Empresa();
        empresa.setDiretor(diretor);
        empresa.setNomeEmpresa("Etico software");

        dynamicFields = DynamicFieldsEntity.create(Empresa.class);
        dynamicFields.createField("nome_do_gerente",String.class);
        dynamicFields.createField("idade_empresa",Integer.class);
        dynamicFields.createField("faturamento",Double.class);
        dynamicFields.createField("dias_fechado",Long.class);
        dynamicFields.createField("distancia",Float.class);
        dynamicFields.createField("foto",byte[].class);


        dynamicFields.addValue("nome_do_gerente","Francisco antonio");
        dynamicFields.addValue("idade_empresa",3000);
        dynamicFields.addValue("faturamento",36874.6985500);
        dynamicFields.addValue("dias_fechado",300000000);
        dynamicFields.addValue("distancia",6985.987);
        dynamicFields.addValue("foto","taliba jose da silva".getBytes());

        empresa.setDynamicFields(dynamicFields);
        connection.save(empresa);

        idEmpresa = empresa.getId();
    }

    @Test
    @Order(2)
    void dataValidDynamicFields() {

        ClimbConnection connection = factory.getConnection("localhost");
        Empresa empresa = (Empresa) connection.findOne(Empresa.class, idEmpresa);

        idEmpresa = empresa.getId();
        assertTrue(idEmpresa != null && idEmpresa > 0);
        assertTrue(empresa.getDiretor() != null && empresa.getDiretor().getId() > 0);

        String nome = (String) empresa.getDynamicFields().getValue("nome_do_gerente");
        assertTrue(nome.equals("Francisco antonio"));

        Double faturamento = (Double) empresa.getDynamicFields().getValue("faturamento");
        assertTrue(faturamento.equals(36874.6985500));

        Integer idadeEmpresa = (Integer) empresa.getDynamicFields().getValue("idade_empresa");
        assertTrue(idadeEmpresa.equals(3000));

        Long diasFechado = (Long) empresa.getDynamicFields().getValue("dias_fechado");
        assertTrue(diasFechado.equals(300000000l));

        Float distancia = (Float) empresa.getDynamicFields().getValue("distancia");
        assertTrue(distancia.equals(6985.987f));

        byte[] foto = (byte[]) empresa.getDynamicFields().getValue("foto");
        assertTrue(new String(foto).equals("taliba jose da silva"));

        connection.close();

    }

    @Test
    @Order(3)
    void validDataWithSelectWhere() {

        ClimbConnection connection = factory.getConnection("localhost");
        ResultIterator iterator = connection.find(Empresa.class, "where id="+idEmpresa.toString());

        if(iterator.next());
        Empresa empresa = (Empresa) iterator.getObject();

        idEmpresa = empresa.getId();
        assertTrue(idEmpresa != null && idEmpresa > 0);
        assertTrue(empresa.getDiretor() != null && empresa.getDiretor().getId() > 0);

        String nome = (String) empresa.getDynamicFields().getValue("nome_do_gerente");
        assertTrue(nome.equals("Francisco antonio"));

        Double faturamento = (Double) empresa.getDynamicFields().getValue("faturamento");
        assertTrue(faturamento.equals(36874.6985500));

        Integer idadeEmpresa = (Integer) empresa.getDynamicFields().getValue("idade_empresa");
        assertTrue(idadeEmpresa.equals(3000));

        Long diasFechado = (Long) empresa.getDynamicFields().getValue("dias_fechado");
        assertTrue(diasFechado.equals(300000000l));

        Float distancia = (Float) empresa.getDynamicFields().getValue("distancia");
        assertTrue(distancia.equals(6985.987f));

        byte[] foto = (byte[]) empresa.getDynamicFields().getValue("foto");
        assertTrue(new String(foto).equals("taliba jose da silva"));

        connection.close();
    }

    @Test
    @Order(4)
    void validDataWithSelectQuery() {

    }

    @Test
    @Order(5)
    void validUpdateDynamicFields() {

        ClimbConnection connection = factory.getConnection("localhost");
        Empresa empresa = (Empresa) connection.findOne(Empresa.class, idEmpresa);

        DynamicFields dynamicFields = empresa.getDynamicFields();

        dynamicFields.addValue("nome_do_gerente","Francisco antonio update");
        dynamicFields.addValue("idade_empresa",4000);
        dynamicFields.addValue("faturamento",46874.6985500);
        dynamicFields.addValue("dias_fechado",400000000);
        dynamicFields.addValue("distancia",7985.987);
        dynamicFields.addValue("foto","taliba jose da silva update".getBytes());

        connection.update(empresa);
        connection.close();

        connection = factory.getConnection("localhost");
        empresa = (Empresa) connection.findOne(Empresa.class, idEmpresa);

        idEmpresa = empresa.getId();
        assertTrue(idEmpresa != null && idEmpresa > 0);
        assertTrue(empresa.getDiretor() != null && empresa.getDiretor().getId() > 0);

        String nome = (String) empresa.getDynamicFields().getValue("nome_do_gerente");
        assertTrue(nome.equals("Francisco antonio update"));

        Double faturamento = (Double) empresa.getDynamicFields().getValue("faturamento");
        assertTrue(faturamento.equals(46874.6985500));

        Integer idadeEmpresa = (Integer) empresa.getDynamicFields().getValue("idade_empresa");
        assertTrue(idadeEmpresa.equals(4000));

        Long diasFechado = (Long) empresa.getDynamicFields().getValue("dias_fechado");
        assertTrue(diasFechado.equals(400000000l));

        Float distancia = (Float) empresa.getDynamicFields().getValue("distancia");
        assertTrue(distancia.equals(7985.987f));

        byte[] foto = (byte[]) empresa.getDynamicFields().getValue("foto");
        assertTrue(new String(foto).equals("taliba jose da silva update"));

        connection.close();

    }

    @Test
    @Order(6)
    void validDeleteDynamicFields() {
        ClimbConnection connection = factory.getConnection("localhost");
        connection.delete(Empresa.class, "where id > 0");

        ResultIterator iterator = connection.find(Empresa.class, "Where id > 0");

        if (iterator.next()) {
            assertTrue(false);
        } else {
            assertTrue(true);
        }

        Diretor diretor = (Diretor) connection.findOne(Diretor.class, idDiretor);
        connection.delete(diretor);

        diretor = (Diretor) connection.findOne(Diretor.class, idDiretor);
        assertTrue(diretor == null);

    }

}
