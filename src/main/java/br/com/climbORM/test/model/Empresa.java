package br.com.climbORM.test.model;

import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.mapping.Column;
import br.com.climbORM.framework.mapping.DynamicField;
import br.com.climbORM.framework.mapping.Entity;
import br.com.climbORM.framework.mapping.Relation;

@Entity(name = "tb_empresa")
public class Empresa extends PersistentEntity {

    @Column(name = "nome_empresa")
    private String nomeEmpresa;

    @Relation
    @Column(name = "id_diretor")
    private Diretor diretor;

    @DynamicField
    private DynamicFields dynamicFields;


    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public DynamicFields getDynamicFields() {
        return dynamicFields;
    }

    public void setDynamicFields(DynamicFields dynamicFields) {
        this.dynamicFields = dynamicFields;
    }

    public Diretor getDiretor() {
        return diretor;
    }

    public void setDiretor(Diretor diretor) {
        this.diretor = diretor;
    }
}
