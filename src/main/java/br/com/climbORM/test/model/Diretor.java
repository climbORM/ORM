package br.com.climbORM.test.model;

import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.mapping.Column;
import br.com.climbORM.framework.mapping.DynamicField;
import br.com.climbORM.framework.mapping.Entity;

@Entity(name = "tb_diretor")
public class Diretor extends PersistentEntity {

    @Column(name = "nome_diretor")
    private String nomeDiretor;

    @DynamicField
    private DynamicFields dynamicFields;

    public DynamicFields getDynamicFields() {
        return dynamicFields;
    }

    public void setDynamicFields(DynamicFields dynamicFields) {
        this.dynamicFields = dynamicFields;
    }

    public String getNomeDiretor() {
        return nomeDiretor;
    }

    public void setNomeDiretor(String nomeDiretor) {
        this.nomeDiretor = nomeDiretor;
    }


}
