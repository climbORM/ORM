package br.com.climbORM.test.model;

import java.util.List;

import br.com.climbORM.framework.interfaces.DynamicFields;
import br.com.climbORM.framework.PersistentEntity;
import br.com.climbORM.framework.mapping.*;

@Entity(name = "tb_pessoa")
public class Pessoa extends PersistentEntity {
	
	@Column
	private String nome;
	
	@Column(name = "endereco_comercial")
	private String enderecoComercial;
	
	@Column
	private Long idade;
	
	@Column
	private Float altura;
	
	@Column(name = "quantidade_quilos")
	private Double qtdQuilos;
	
	@Column
	private Boolean casado;
	
	@Relation
	@Column(name = "id_endereco")
	private Endereco endereco;
	
	@Column
	private byte[] foto;

	@Json(typeJson = Json.JSON)
	@Column(name = "lista_emails")
	private List<Email> emails;

	@DynamicField
	private DynamicFields dynamicFields;

	public String getEnderecoComercial() {
		return enderecoComercial;
	}

	public void setEnderecoComercial(String enderecoComercial) {
		this.enderecoComercial = enderecoComercial;
	}

	public Long getIdade() {
		return idade;
	}

	public void setIdade(Long idade) {
		this.idade = idade;
	}

	public Float getAltura() {
		return altura;
	}

	public void setAltura(Float altura) {
		this.altura = altura;
	}

	public Double getQtdQuilos() {
		return qtdQuilos;
	}

	public void setQtdQuilos(Double qtdQuilos) {
		this.qtdQuilos = qtdQuilos;
	}

	public Boolean getCasado() {
		return casado;
	}

	public void setCasado(Boolean casado) {
		this.casado = casado;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	public DynamicFields getDynamicFields() {
		return dynamicFields;
	}

	public void setDynamicFields(DynamicFields dynamicFields) {
		this.dynamicFields = dynamicFields;
	}
}
