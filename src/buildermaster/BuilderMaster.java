package buildermaster;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuilderMaster {

	Set<String> listaImports;
	
	public BuilderMaster() {
		listaImports = new HashSet<String>();
		listaImports.add("import java.util.List;");
		listaImports.add("import java.util.Collections;");
		listaImports.add("import java.util.ArrayList;");
	}

	@SuppressWarnings("rawtypes")
	public void gerarCodigoClasse(Class classe) {
		
		
		String nomeClasse = classe.getSimpleName() + "Builder";
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("public class ").append(nomeClasse).append(" {\n");
		builder.append("\tprivate ").append(classe.getSimpleName()).append(" elemento;\n");

		builder.append("\tprivate ").append(nomeClasse).append("(){}\n\n");

		builder.append("\tpublic static ").append(nomeClasse).append(" um").append(classe.getSimpleName()).append("() {\n");
		builder.append("\t\t").append(nomeClasse).append(" builder = new ").append(nomeClasse).append("();\n");
		builder.append("\t\tinicializarDadosPadroes(builder);\n");
		builder.append("\t\treturn builder;\n");
		builder.append("\t}\n\n");

		builder.append("\tpublic static void inicializarDadosPadroes(").append(nomeClasse).append(" builder) {\n");
		builder.append("\t\tbuilder.elemento = new ").append(classe.getSimpleName()).append("();\n");
		builder.append("\t\t").append(classe.getSimpleName()).append(" elemento = builder.elemento;\n");
		builder.append("\n\t\t\n");
		
		List<Field> declaredFields = getClassFields(classe);
		for(Field campo: declaredFields) {
			if(campo.getName().equals("serialVersionUID"))
				continue;
			if(Modifier.isStatic(campo.getModifiers()))
				continue;
			builder.append("\t\telemento.set").append(campo.getName().substring(0, 1).toUpperCase()).append(campo.getName().substring(1)).append("(").append(getDefaultParameter(campo)).append(");\n");
				
		}
		builder.append("\t}\n\n");		
		
		for(Field campo: declaredFields) {
			if(campo.getName().equals("serialVersionUID"))
				continue;
			if(Modifier.isStatic(campo.getModifiers()))
				continue;
			if(campo.getType().getSimpleName().equals("List")) {
				ParameterizedType stringListType = (ParameterizedType) campo.getGenericType();
				builder.append("\tpublic ")
					.append(nomeClasse)
					.append(" comLista").append(campo.getName().substring(0, 1).toUpperCase()).append(campo.getName().substring(1))
					.append("(").append(((Class)stringListType.getActualTypeArguments()[0]).getSimpleName()).append("... params) {\n");
//				List<elemType> lista = new ArrayList<elemType>();
				builder.append("\t\tList<").append(((Class)stringListType.getActualTypeArguments()[0]).getSimpleName()).append("> lista = new ArrayList<")
					.append(((Class)stringListType.getActualTypeArguments()[0]).getSimpleName()).append(">();\n");
				registrarImports(((Class)stringListType.getActualTypeArguments()[0]).getName());
//				Collections.addAll(lista, args);
				builder.append("\t\tCollections.addAll(lista, params);\n");
//				elemento.setelemTypes(lista);
				builder.append("\t\telemento.set").append(campo.getName().substring(0, 1).toUpperCase()).append(campo.getName().substring(1)).append("(lista);\n");
				
				builder.append("\t\treturn this;\n");
				builder.append("\t}\n\n");
			} else {
				builder.append("\tpublic ")
					.append(nomeClasse)
					.append(" com").append(campo.getName().substring(0, 1).toUpperCase()).append(campo.getName().substring(1))
					.append("(").append(campo.getType().getSimpleName()).append(" param) {\n");
				registrarImports(campo.getType().getCanonicalName());
				builder.append("\t\telemento.set")
					.append(campo.getName().substring(0, 1).toUpperCase()).append(campo.getName().substring(1))
					.append("(param);\n");
				builder.append("\t\treturn this;\n");
				builder.append("\t}\n\n");
			}
		}

		builder.append("\tpublic ").append(classe.getSimpleName()).append(" agora() {\n");
		builder.append("\t\treturn elemento;\n");
		builder.append("\t}\n");

		builder.append("}");
		
		for(String str: listaImports) {
			System.out.println(str);
		}
		System.out.println("import " + classe.getCanonicalName() + ";");
		System.out.println("\n");
		System.out.println(builder.toString());
	}
	
	@SuppressWarnings("rawtypes")
	public List<Field> getClassFields(Class classe) {
		List<Field> fields = new ArrayList<Field>(); 
		fields.addAll(Arrays.asList(classe.getDeclaredFields()));
		Class superClass = classe.getSuperclass();
		if(superClass != Object.class) {
			List<Field> fieldsSC = Arrays.asList(superClass.getDeclaredFields()); 
			fields.addAll(fieldsSC);
		}
		return fields;
	}
	
	public String getDefaultParameter(Field campo) {
		String tipo = campo.getType().getSimpleName();
		if(tipo.equals("int") || tipo.equals("Integer")){
			return "0";
		}
		if(tipo.equals("long") || tipo.equals("Long")){
			return "0L";
		}
		if(tipo.equals("double") || tipo.equals("Double")){
			return "0.0";
		}
		if(tipo.equals("boolean") || tipo.equals("Boolean")){
			return "false";
		}
		if(tipo.equals("String")){
			return "\"\"";
		}
		return "null";
	}
	
	public void registrarImports(String classe) {
		if(classe.contains("."))
			listaImports.add("import " + classe + ";");
	}
	
}
