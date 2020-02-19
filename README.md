# Tutotial ClimbORM
## **Mapeamento Objeto Relacional**
O mapeamento objeto relacional é representação de uma tabela de um banco de dados relacional através de classes em Java.

Banco de dados | Linguagem Orientada a Objetos
:------------: | :------------:
Tabela   | Classe
Coluna   | Atributo
Registro | Objeto

Como no banco de dados temos Tabelas (Entidades), Colunas e Registros, na linguagem orientada a objetos, como o caso da linguagem Java, temos o equivalente a Classes, Atributos e Objetos, respectivamente. 
Além destas equivalências, para que seja possível o completo manuseio de bancos de dados relacionais, assim como em outros frameworks, é necessário o uso de anotações nas classes.
As mais usadas são:
## **@Entity:**
Essa anotação é usada antes da criação de uma classe para fazer referência a qual tabela no banco de dados relacionais está associada.
**Exemplo:**
```
@Entity(name = "contato")
public class Contato extends PersistentEntity{
}
```

## **@Column**
Essa anotação é usada antes da declaração de um atributo de uma classe para fazer referência a qual coluna da tabela no banco de dados está relacionada.
**Exemplo:**

```
@Entity(name = "contato")
public class Contato extends PersistentEntity{
@Column
private String nome;
@Column(name = “telefone_pessoa”)
private String telefonePessoa;
}
```

## **@Relation**
Essa anotação é usada antes da declaração de um atributo de uma classe para fazer referência ao relacionamento de duas tabelas (entidades).
**Exemplo:**
```
@Entity(name = "contato")
public class Contato extends PersistentEntity{
@Relation
@Column(name = “id_cli”)
private Cliente cliente;
}
```
## **@ID**
Essa anotação é usada antes da declaração de um atributo identificador de uma classe para fazer referência da coluna de chave primaria de uma tabela no banco de dados relacional (Essa referência já está implementada na extensão da classe PersistentEntity).
**Exemplo:**

```
public abstract class PersistentEntity {
		@ID
private Long id;
}
```

## **@DynamicField**
Essa anotação é usada antes de declaração do atributo do tipo DynamicFields, para que possa ocorrer o uso de campos dinâmicos na classe.
**Exemplo: **
```
@Entity(name = "contato")
public class Contato extends PersistentEntity{
@Column
private String nome;
@DynamicField
private DynamicFields dynamicField;
}
```
## **Primeiros passos com ClimORM**
Para que seja possível utilizar o FrameWork ClimbORM, é necessário que sejam feitas alterações para a importação do mesmo no arquivo “pom.xml”, dentro do projeto Spring, como indicado na figura abaixo:
IMAGEM AQUIIIIIIII
Após aberto o arquivo “pom.xml”, deve-se inserir as seguintes linhas de código para a importação das dependências e do repositório da ferramenta com as seguintes tags, como no exemplo a seguir:
> Para importar a dependência:
```
<dependencies>  
    	<!--  Climb ORM -->
    	<dependency>
		<groupId>com.climbORM</groupId>
		<artifactId>climbORM</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>
</dependencies>
```
> Para importar o repositório:
```
<repositories>
		<repository>
			<id>bobboyms-climbORM</id>
		<url>https://packagecloud.io/bobboyms/climbORM/maven2</url>
			<releases>
				<enabled>true</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
</repositories>
```
## **Configuração do banco de dados**
Para declarar quais são as configurações do banco de dados relacional que será usado na aplicação do projeto, deve-se criar um documento na pasta usando a extensão “.properties”, com o conteúdo do arquivo seguindo a estrutura como no exemplo (Mude apenas os valores para as especificações do seu banco de dados):
```
url=127.0.0.1
user=postgres
password=postgres
database=bdagenda
port=5432
ssl=false
```
## **Criando classes para o mapeamento**
Para seguir com o exemplo, será usado o código de criação de uma Tabela (Entidade) genérica no banco de dados relacional:
```
create table Pessoa(
id SERIAL PRIMARY KEY,
nome_pessoa VARCHAR(30) NOT NULL
);
```
Para que seja feito o mapeamento desta tabela para uma classe Java, deve-se seguir a estrutura como no exemplo de classe a seguir, com os getters e setters omitidos:
```
@Entity(name = "contato")
public class Contato extends PersistentEntity{
@Column(name =”nome_pessoa”)
private String nomePessoa;
}
```
Como no exemplo acima, não é necessário a declaração do atributo “id” com a extensão da classe “PersistentEntity”, pois a mesma já conta com tais declarações, assim como getter e setter para o atributo.
O uso da anotação @Entity(name = “XXX”) é obrigatório para especificar a qual Tabela a classe Java está ligada.
## **Criando o ManagerFactory**
Para que sejam feitas todas as ações relacionadas ao banco de dados relacional é necessário o uso de uma classe controladora para as ações CRUD. Dentro desta classe, deve-se declarar no início o ManagerFactory, e passar como parâmetro no método createManagerFactory(“XXXX”) o nome do arquivo criado com as especificações do banco de dados, como no exemplo abaixo:
```
public class PessoaService{
ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");
}
```
## **Operações CRUD**
	Com o ManagerFactory criado com sucesso, segue-se então para a criação da conexão do tipo ClimbConnection dentro de cada operação CRUD, como no exemplo:
```
public class PessoaService{
ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

public static void main (String... args){
	ClimbConnection rep = factory.getConnection("public");
	try{
rep.getTransaction().start();		
		//TODO CÓDIGO CRUD AQUI
rep.getTransaction().commit();
}catch{
rep.getTransaction().rollback();
e.printStackTrace();
}finally{
rep.close();
}
}
```
Nas operações de busca não se faz o uso desta estrutura pelo uso do LazyLoader usado no framework, deixando o código de busca em tal estrutura:
```
public static void main (String... args){
ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");
	ClimbConnection rep = factory.getConnection("public");
	try{		
		//TODO CÓDIGO DE BUSCA AQUI
}catch{
e.printStackTrace();
}

```

