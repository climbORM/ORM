package br.com.climbORM.teste.model;

import br.com.climbORM.api.PersistentEntity;
import br.com.climbORM.api.mapping.Column;
import br.com.climbORM.api.mapping.Entity;
import br.com.climbORM.api.mapping.Relation;

@Entity(name = "tb_endereco")
public class Endereco extends PersistentEntity {
	
	@Column(name = "nome_da_rua")
	private String nomeDaRua;
	
	@Relation
	@Column(name = "id_cidade")
	private Cidade cidade;
	
	public String getNomeDaRua() {
		return nomeDaRua;
	}

	public void setNomeDaRua(String nomeDaRua) {
		this.nomeDaRua = nomeDaRua;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	
}
