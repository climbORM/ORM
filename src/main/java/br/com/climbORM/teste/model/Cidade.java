package br.com.climbORM.teste.model;

import br.com.climbORM.api.PersistentEntity;
import br.com.climbORM.api.mapping.Column;
import br.com.climbORM.api.mapping.Entity;

@Entity(name = "tb_cidade")
public class Cidade extends PersistentEntity {
	
	@Column(name = "nome_da_cidade")
	private String nomeDaCidade;

	public String getNomeDaCidade() {
		return nomeDaCidade;
	}

	public void setNomeDaCidade(String nomeDaCidade) {
		this.nomeDaCidade = nomeDaCidade;
	}
	
	
}
