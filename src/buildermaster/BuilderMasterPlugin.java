package buildermaster;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class BuilderMasterPlugin implements ClipboardOwner {

	Set<String> listaImports;
	
	public BuilderMasterPlugin() {
		listaImports = new HashSet<String>();
		listaImports.add("import java.util.List;");
		listaImports.add("import java.util.Collections;");
		listaImports.add("import java.util.ArrayList;");
	}

	@SuppressWarnings("rawtypes")
	public void gerarCodigoClasse(ICompilationUnit unit) throws JavaModelException {
		
		
		String nomeObjeto = unit.getElementName().substring(0,  unit.getElementName().length() - 5);
		String nomeClasse = nomeObjeto + "Builder";
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("public class ").append(nomeClasse).append(" {\n");
		builder.append("\tprivate ").append(nomeObjeto).append(" elemento;\n");

		builder.append("\tprivate ").append(nomeClasse).append("(){}\n\n");

		builder.append("\tpublic static ").append(nomeClasse).append(" um").append(nomeObjeto).append("() {\n");
		builder.append("\t\t").append(nomeClasse).append(" builder = new ").append(nomeClasse).append("();\n");
		builder.append("\t\tinicializarDadosPadroes(builder);\n");
		builder.append("\t\treturn builder;\n");
		builder.append("\t}\n\n");

		builder.append("\tpublic static void inicializarDadosPadroes(").append(nomeClasse).append(" builder) {\n");
		builder.append("\t\tbuilder.elemento = new ").append(nomeObjeto).append("();\n");
		builder.append("\t\t").append(nomeObjeto).append(" elemento = builder.elemento;\n");
		builder.append("\n\t\t\n");
		
		List<IField> declaredFields = getClassFields(unit);
		for(IField campo: declaredFields) {
			if(getIFieldName(campo).equals("serialVersionUID"))
				continue;
			if(Flags.isStatic(campo.getFlags()))
				continue;
			builder.append("\t\telemento.set").append(getIFieldName(campo).substring(0, 1).toUpperCase()).append(getIFieldName(campo).substring(1)).append("(").append(getDefaultParameter(campo)).append(");\n");
				
		}
		builder.append("\t}\n\n");		
		
		for(IField campo: declaredFields) {
			String nomeCampo = getIFieldName(campo);
			if(nomeCampo.equals("serialVersionUID"))
				continue;
			if(Flags.isStatic(campo.getFlags()))
				continue;
			if(getSimpleIFieldType(campo).contains("List")) {
				builder.append("\tpublic ")
					.append(nomeClasse)
					.append(" comLista").append(nomeCampo.substring(0, 1).toUpperCase()).append(nomeCampo.substring(1))
					.append("(").append(getFieldGenericsName(campo)).append("... params) {\n");
				builder.append("\t\tList<").append(getFieldGenericsName(campo)).append("> lista = new ArrayList<")
					.append(getFieldGenericsName(campo)).append(">();\n");
				registrarImports(getFullFieldGenericsName(campo));
				builder.append("\t\tCollections.addAll(lista, params);\n");
				builder.append("\t\telemento.set").append(nomeCampo.substring(0, 1).toUpperCase()).append(nomeCampo.substring(1)).append("(lista);\n");
				
				builder.append("\t\treturn this;\n");
				builder.append("\t}\n\n");
			} else {
				builder.append("\tpublic ")
					.append(nomeClasse)
					.append(" com").append(nomeCampo.substring(0, 1).toUpperCase()).append(nomeCampo.substring(1))
					.append("(").append(getSimpleIFieldType(campo)).append(" param) {\n");
				registrarImports(getIFieldType(campo));
				builder.append("\t\telemento.set")
					.append(nomeCampo.substring(0, 1).toUpperCase()).append(nomeCampo.substring(1))
					.append("(param);\n");
				builder.append("\t\treturn this;\n");
				builder.append("\t}\n\n");
			}
		}

		builder.append("\tpublic ").append(nomeObjeto).append(" agora() {\n");
		builder.append("\t\treturn elemento;\n");
		builder.append("\t}\n");

		builder.append("}");
		
		StringBuilder importBuilder = new StringBuilder();
		for(String str: listaImports) {
			System.out.println(str);
			importBuilder.append(str).append("\n");
		}
//		System.out.println("import " + classe.getCanonicalName() + ";");
		System.out.println("\n");
		importBuilder.append("\n");
		System.out.println(builder.toString());
		
		
		StringSelection stringSelection = new StringSelection(importBuilder.toString() + builder.toString());
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, this);
	}
	
	@SuppressWarnings("rawtypes")
	public List<IField> getClassFields(ICompilationUnit unit) {
		List<IField> fields = new ArrayList<IField>();
		try {
			fields.addAll(Arrays.asList(unit.getTypes()[0].getFields()));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		try {
			ITypeHierarchy hierarchy = unit.getTypes()[0].newSupertypeHierarchy(new NullProgressMonitor());
			IType[] superTypes =  hierarchy.getAllSupertypes(unit.getTypes()[0]);
			if(superTypes.length > 1) {
				for(int i = 0; i < superTypes.length; i++) {
					IType classe = superTypes[i];
					fields.addAll(Arrays.asList(classe.getFields()));
				}
			}
			System.out.println("asdasd");
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return fields;
	}
	
	public String getIFieldName(IField field) {
		return field.getElementName();
	}
	
	public String getIFieldType(IField field) {
		try {
			String[][] type = field.getDeclaringType().resolveType(field.getTypeSignature().substring(1,field.getTypeSignature().length() - 1));
			return type[0][0] + "." + type[0][1];
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getSimpleIFieldType(IField field) {
		try {
			String[][] type = field.getDeclaringType().resolveType(field.getTypeSignature().substring(1,field.getTypeSignature().length() - 1));
			return type[0][1];
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getFieldGenericsName(IField field) {
		try {
			return field.getTypeSignature().substring(field.getTypeSignature().indexOf('<') + 2, field.getTypeSignature().indexOf('>') - 1);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getFullFieldGenericsName(IField field) {
		String[][] type;
		try {
			type = field.getDeclaringType().resolveType(getFieldGenericsName(field));
			return type[0][0] + "." + type[0][1];
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getDefaultParameter(IField campo) {
		String tipo = getSimpleIFieldType(campo);
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

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}
	
}
