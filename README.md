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
**Exemplo: **
```
@Entity(name = "contato")
public class Contato extends PersistentEntity{
@Relation
@Column(name = “id_cli”)
private Cliente cliente;
}
```
