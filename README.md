# BuilderMaster

## Forma de uso

BuilderMaster é uma lib que vai criar Builders para as suas entidades. Utiliza conceitos como *DataBuilder*, *Method Chaining* e *Fluent Interface*.

Para gerar os códigos para uma classe, adicione o [jar](https://github.com/wcaquino/BuilderMaster/blob/master/builderMaster.jar) no classpath do seu código e faça a seguinte chamada:

```
new BuilderMaster().gerarCodigoClasse(SuaClasse.class);
```

O código será impresso no console. Feito isso, basta copiar todo o código gerado. Criar uma classe com o nome *SuaClasseBuilder* e colar o código gerado deixando apenas a declaração do pacote. 

## Exemplo

Como exemplo, segue o código de uma entidade:
```
package br.ce.wcaquino.entidades;

public class Usuario {

	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
```

E agora, o código do builder gerado para a entidade acima:
```
package br.ce.wcaquino.entidades; //Única linha que não foi gerada pelo BuilderMaster

import java.util.Arrays;
import java.lang.String;
import br.ce.wcaquino.entidades.Usuario;


public class UsuarioBuilder {
	private Usuario elemento;
	private UsuarioBuilder(){}

	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		inicializarDadosPadroes(builder);
		return builder;
	}

	public static void inicializarDadosPadroes(UsuarioBuilder builder) {
		builder.elemento = new Usuario();
		Usuario elemento = builder.elemento;

		
		elemento.setNome("");
	}

	public UsuarioBuilder comNome(String param) {
		elemento.setNome(param);
		return this;
	}

	public Usuario agora() {
		return elemento;
	}
}

```

## Organização dos arquivos

Esse projeto possui mais classe que o necessário. A única classe para essa geração é a `br.ce.wcaquino.buildermaster.BuilderMaster`. 

As demais classes fazem parte de um projeto para um plugin do Eclipse que você poderia utilizá-la apenas selecionando a classe na árvore estrutural do projeto, e solicitando a geração dos códigos para o builder da entidade selecionada. O código já ficaria disponível na área de transferência.

Porém, para esse trabalho, não era possível utilizar o Reflection do JAVA (`java.lang.reflect.*`), foi necessário utilizar a lib jdt do eclipse (`org.eclipse.jdt.core.*`). Essa lib é bem mais complexa que o Reflection com a documentação mais escassa. Consegui fazer funcionar o básico do builder mas não encontrei portar todas as funcionalidades da versão do reflection para o jdt. Por questões de tempo, acabei deixando esse projeto de lado.
Abri o projeto atualmente, e não está nem compilando. Não perdi tempo tentando consertá-la, mas deixei o código mesmo assim para fins didáticos... ou caso alguém queira levar o projeto para frente.
