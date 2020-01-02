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

        dynamicFields.addValue("nome_do_gerente","Francisco antonio");
        dynamicFields.addValue("idade_empresa",30);
        dynamicFields.addValue("faturamento",36874.6985500);
        dynamicFields.addValue("dias_fechado",30);

        empresa.setDynamicFields(dynamicFields);
        connection.save(empresa);

        idEmpresa = empresa.getId();
        assertTrue(idEmpresa != null && idEmpresa > 0);

        connection.close();

        connection = factory.getConnection("localhost");

        empresa = (Empresa) connection.findOne(Empresa.class, idEmpresa);
        Double faturamento = (Double) empresa.getDynamicFields().getValue("faturamento");

        System.out.println(faturamento);

        assertTrue(faturamento.equals(36874.6985500));

    }

}
