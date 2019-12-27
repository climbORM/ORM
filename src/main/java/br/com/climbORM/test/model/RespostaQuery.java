package br.com.climbORM.test.model;

import br.com.climbORM.framework.mapping.Column;
import br.com.climbORM.framework.mapping.QueryResult;
import br.com.climbORM.framework.mapping.Relation;

import java.util.List;

@QueryResult
public class RespostaQuery {

    @Column
    private String nome;

    @Column(name = "nome_da_rua")
    private String nomeDaRua;

    @Column(name = "nome_da_cidade")
    private String nomeDaCidade;

    @Column(name = "lista_emails")
    private List<Email> emails;

    @Relation
    @Column(name = "id_endereco")
    private Endereco endereco;

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeDaRua() {
        return nomeDaRua;
    }

    public void setNomeDaRua(String nomeDaRua) {
        this.nomeDaRua = nomeDaRua;
    }

    public String getNomeDaCidade() {
        return nomeDaCidade;
    }

    public void setNomeDaCidade(String nomeDaCidade) {
        this.nomeDaCidade = nomeDaCidade;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }
}
