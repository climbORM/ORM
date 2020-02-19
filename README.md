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
**Exemplo:**
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

![alt text](https://github.com/climbORM/ORM/blob/criar_arquivo_md/POMXMLTUTORIAL.PNG)

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
Como no exemplo acima, não é necessário a declaração do atributo **“id”** com a extensão da classe **“PersistentEntity”**, pois a mesma já conta com tais declarações, assim como getter e setter para o atributo.
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
}
```
## Inserindo registros
Com o modelo de código de operação CRUD, agora pode-se executar as inserções no banco de dados relacional, como no exemplo abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();		
            Pessoa pessoa = new Pessoa();
            pessoa.setNomePessoa(“Carlos”);
            rep.save(pessoa);
            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```

Como pode-se ver, não é necessário inserir o valor do **“id”** do objeto que será inserido no banco de dados, isso se dá pelo framework já tratar e utilizar auto incremento automaticamente nos identificadores dos objetos.

## **Deletando registros**
Seguindo o mesmo padrão de inserção, o código de exclusão de um registro é extremamente simples:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
         ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();		
            Pessoa pessoa = new Pessoa();
            pessoa = (Pessoa) rep.findOne(Contato.class, 1L);
            rep.delete(pessoa);
            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```

## **Atualizando registros**
Atualizar registros é algo natural em toda aplicação, assim, a fermenta permite atualizações de maneiras simples eficientes, como no exemplo do código abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();		
            Pessoa pessoa = new Pessoa();
            pessoa = (Pessoa) rep.findOne(Contato.class, 1L);
            pessoa.setNomePessoa(“Carlos - Update”);
            rep.update (pessoa);
            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}

```
## **Buscando registros**
Diferente dos códigos acima, as buscas no banco de dados não fecham as transações, ao invés disso, permanecem abertas durante toda a execução da aplicação, por isso, fechar a conexão dentro de um método de busca implicará em um erro de execução.
Para buscas de apenas um registro, usa-se o exemplo abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");
    
    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{		
            Pessoa pessoa = new Pessoa();
            pessoa = (Pessoa) rep.findOne(Contato.class, 1L);
        }catch{
            e.printStackTrace();
        }
    }
}
```
Para buscas de mais de um registro é usado o método **find**, como no exemplo abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{		
            List<Pessoa> pessoas = new ArrayList<Pessoa>();
            ResultIterator result = rep.find(Contato.class, “where id > 0”);
            while(result.next()){
            Pessoa pessoa = (Pessoa) result.getObject()
            pessoas.add(pessoa);
        }
        }catch{
            e.printStackTrace();
        }
    }
}
```
Usando o método **find**, deve-se passar a classe do objeto que será retornado e a condição (where) para o retorno, assim, as buscas são dinâmicas e podem ser variáveis as condições de retorno.
 
## **Campos dinâmicos**
Além de poder trabalhar como um framework ORM comum, a ferramenta ClimbORM permite que o projeto conte com o uso de campos dinâmicos nas entidades já criadas através da anotação **“@DynamicFiled”**.
Esse modo de campos dinâmicos permitem o usuário criar atributos, escolher o tipo do conteúdo e salvar valores nos novos atributos de forma fácil e dinâmica.

## **Declarando campo dinâmico na classe**
Como dito acima, para a declaração do uso de campos dinâmicos na classe Java, deve-se usar a anotação **“@DynamicFiled”**, e logo abaixo declarar um atributo do tipo **DynamicFields**, como no exemplo abaixo:
```
@Entity(name = "contato")
public class Contato extends PersistentEntity{

    @Column(name =”nome_pessoa”)
    private String nomePessoa;

    @DynamicField
    private DynamicFields dynamicFields;
	
    //GETTER E SETTER OMITIDOS
}
```
## **Criando um campo dinâmico**
Com a classe Java em questão já possuindo a anotação de campos dinâmicos, a criação de um novo campo na entidade se torna algo simples, como no exemplo abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();		
            DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
            dynamicField.createField(“Nome_do_atributo”, String.classs);
            rep.createDynamicField(dynamicField);
            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```
O nome do atributo que será criado não pode possuir espaços (Ex: o atributo “nome do pai” deve ser inserido como “nome_do_pai”). Para substituir espaços vazios na string use o método replace:
```
String nomeAtributo = “nome do pai”;
DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
nomeAtributo = nomeAtributo.replace(“ “, “_”);
dynamicField.createField(nomeAtributo, String.classs);
rep.createDynamicField(dynamicField);
```
Além disso, usando o método **createField**, pode-se usar todos os tipos de dados Java para os novos atributos dinâmicos. Como no exemplo de alguns casos:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();	

            DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
            dynamicField.createField(“Nome_da_String”, String.classs);
            dynamicField.createField(“Nome_do_Integer”, Integer.classs);
            dynamicField.createField(“Nome_do_Float”, Float.classs);
            dynamicField.createField(“Nome_do_Double”, Double.classs);
            rep.createDynamicField(dynamicField);

            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```

## **Inserindo valores em um campo dinâmico**
Para inserir valores nos atributos dinâmicos é extremamente simples, com o método **addValue** é passado o nome do atributo e o valor, fazendo uma inserção simples. Como no exemplo abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();

            Pessoa pessoa = new Pessoa();
            Pessoa.setNomePessoa(“Pedro”);	
            DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
            dynamicField.addValue(“Nome_da_String”, “Thiago”);
            dynamicField.addValue (“Nome_do_Integer”, 14);
            dynamicField.addValue (“Nome_do_Float”, 89.36);
            pessoa.setDynamicField(dynamicField);
            rep.save(pessoa);

            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```

## **Atualizando valores em um campo dinâmico**
Assim como no método tradicional, realizar um **update** em algum registro com campos dinâmicos é uma tarefa simples, porém muito importante para o funcionamento de tais aplicações.
Seguindo o modelo de update abaixo, para que seja possível atualizar os valores é necessário passar o nome do atributo que será alterado junto com o valor do mesmo no método **addValue*8:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

    public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{
            rep.getTransaction().start();
            Pessoa pessoa = (Pessoa) rep.findOne(Contato.class, 1L);
            DynamicFields dynamicFields = DynamicFieldsEntity.create(Pessoa.class);
            dynamicField.addValue(“Nome_da_String”, “Thiago - Update”);
            dynamicField.addValue (“Nome_do_Integer”, 99);
            dynamicField.addValue (“Nome_do_Float”, 99.99);
            pessoa.setDynamicField(dynamicField);
            rep.update(pessoa);

            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```

## **Excluindo um campo dinâmico**
Caso um atributo criado não tenha mais relevância para a aplicação, é possível através do método **dropDynamicField** exclui-lo, passando como parâmetro o nome do atributo desejado, como no exemplo abaixo:
```
public class PessoaService{
        ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");

        public static void main (String... args){
            ClimbConnection rep = factory.getConnection("public");
            try{
            rep.getTransaction().start();

            rep.dropDynamicField(“Nome_da_String”);

            rep.getTransaction().commit();
        }catch{
            rep.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            rep.close();
        }
    }
}
```
## **Buscando valor de um campo dinâmico**
Assim como nas classes normais, é possível buscar o valor de um único atributo ou a lista de todos os atributos com o uso dos métodos **getValue** e **getValueFields**, respctivamente, como no código abaixo:
```
public class PessoaService{
    ManagerFactory factory = ClimbORM.createManagerFactory("application.properties");
        public static void main (String... args){
        ClimbConnection rep = factory.getConnection("public");
        try{		
            Pessoa pessoa = new Pessoa();
            pessoa = (Pessoa) rep.findOne(Contato.class, 1L);		
            System.out.println(pessoa.getDynamicField().getValue(“Nome_String));
                    
            //RETORNO DE LISTA DE ATRIBUTOS
            System.out.println(pessoa.getDynamicField().getValueFields());
        }catch{
            e.printStackTrace();
        }
    }
}
```


