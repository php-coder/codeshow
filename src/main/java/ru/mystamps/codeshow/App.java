package ru.mystamps.codeshow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;

public class App {

	private static final String PKG_STEREOTYPE = "org.springframework.stereotype";
	private static final String PKG_BIND_ANNOTATION = "org.springframework.web.bind.annotation";

	private static final String CONTROLLER_NAME = "Controller";
	private static final String REST_CONTROLLER_NAME = "RestController";
	private static final String GET_MAPPING_NAME = "GetMapping";
	private static final String PUT_MAPPING_NAME = "PutMapping";
	private static final String POST_MAPPING_NAME = "PostMapping";
	private static final String PATCH_MAPPING_NAME = "PatchMapping";
	private static final String DELETE_MAPPING_NAME = "DeleteMapping";
	private static final String REQUEST_MAPPING_NAME = "RequestMapping";

	private static final String CONTROLLER_FULL_NAME = "org.springframework.stereotype.Controller";
	private static final String REST_CONTROLLER_FULL_NAME = "org.springframework.web.bind.annotation.RestController";
	private static final String GET_MAPPING_FULL_NAME = "org.springframework.web.bind.annotation.GetMapping";
	private static final String PUT_MAPPING_FULL_NAME = "org.springframework.web.bind.annotation.PutMapping";
	private static final String POST_MAPPING_FULL_NAME = "org.springframework.web.bind.annotation.PostMapping";
	private static final String PATCH_MAPPING_FULL_NAME = "org.springframework.web.bind.annotation.PatchMapping";
	private static final String DELETE_MAPPING_FULL_NAME = "org.springframework.web.bind.annotation.DeleteMapping";
	private static final String REQUEST_MAPPING_FULL_NAME = "org.springframework.web.bind.annotation.RequestMapping";


	// TODO: add logging
	public static void main(String[] args) throws FileNotFoundException {
		// TODO: handle missing argument and show a usage
		FileInputStream in = new FileInputStream(args[0]);

		CompilationUnit cu = JavaParser.parse(in);

		List<String> endpoints = collectEndpoints(cu);
		endpoints.stream().forEach(System.out::println);

	}

	//
	// The rules:
	// - class should be annotated with
	//   * @Controller and import Controller class or its package
	//   * @RestController and import RestController or its package
	// - method should be annotated with @(Get|Put|Post|Patch|DeleteRequest)Mapping and import annotation class or its package
	//
	static List<String> collectEndpoints(CompilationUnit cu) {
		List<String> result = new ArrayList<>();

		Map<String, Boolean> classImports = new HashMap<>();
		classImports.put(CONTROLLER_FULL_NAME, Boolean.FALSE);
		classImports.put(REST_CONTROLLER_FULL_NAME, Boolean.FALSE);
		classImports.put(GET_MAPPING_FULL_NAME, Boolean.FALSE);
		classImports.put(PUT_MAPPING_FULL_NAME, Boolean.FALSE);
		classImports.put(POST_MAPPING_FULL_NAME, Boolean.FALSE);
		classImports.put(PATCH_MAPPING_FULL_NAME, Boolean.FALSE);
		classImports.put(DELETE_MAPPING_FULL_NAME, Boolean.FALSE);
		classImports.put(REQUEST_MAPPING_FULL_NAME, Boolean.FALSE);

		Map<String, Boolean> wildcardImports = new HashMap<>();
		wildcardImports.put(PKG_STEREOTYPE, Boolean.FALSE);
		wildcardImports.put(PKG_BIND_ANNOTATION, Boolean.FALSE);

		NodeList<ImportDeclaration> imports = cu.getImports();
		for (ImportDeclaration importDeclaration : imports) {
			String importName = importDeclaration.getName().toString();
			if (importDeclaration.isAsterisk()) {
				if (wildcardImports.containsKey(importName)) {
					wildcardImports.put(importName, Boolean.TRUE);
				}
				continue;
			}
			if (classImports.containsKey(importName)) {
				classImports.put(importName, Boolean.TRUE);
			}
		}

		NodeList<TypeDeclaration<?>> types = cu.getTypes();
		for (TypeDeclaration<?> type : types) {
			boolean hasController = type.getAnnotationByName(CONTROLLER_NAME).isPresent();
			boolean importsController = classImports.getOrDefault(CONTROLLER_FULL_NAME, Boolean.FALSE);
			boolean importsControllerPackage = wildcardImports.getOrDefault(PKG_STEREOTYPE, Boolean.FALSE);

			boolean hasRestController = type.getAnnotationByName(REST_CONTROLLER_NAME).isPresent();
			boolean importsRestController = classImports.getOrDefault(REST_CONTROLLER_FULL_NAME, Boolean.FALSE);
			boolean importsRestControllerPackage = wildcardImports.getOrDefault(PKG_BIND_ANNOTATION, Boolean.FALSE);

			boolean controller = hasController && (importsController || importsControllerPackage);
			boolean restController = hasRestController && (importsRestController || importsRestControllerPackage);

			if (!controller && !restController) {
				continue;
			}

			NodeList<BodyDeclaration<?>> members = type.getMembers();
			for (BodyDeclaration<?> member : members) {
				if (!(member instanceof MethodDeclaration)) {
					continue;
				}

				MethodDeclaration method = member.asMethodDeclaration();

				addIfNotNull(result, inspectMethodForAnnotation("GET", GET_MAPPING_NAME, GET_MAPPING_FULL_NAME, type, method, classImports, wildcardImports));
				addIfNotNull(result, inspectMethodForAnnotation("PUT", PUT_MAPPING_NAME, PUT_MAPPING_FULL_NAME, type, method, classImports, wildcardImports));
				addIfNotNull(result, inspectMethodForAnnotation("POST", POST_MAPPING_NAME, POST_MAPPING_FULL_NAME, type, method, classImports, wildcardImports));
				addIfNotNull(result, inspectMethodForAnnotation("PATCH", PATCH_MAPPING_NAME, PATCH_MAPPING_FULL_NAME, type, method, classImports, wildcardImports));
				addIfNotNull(result, inspectMethodForAnnotation("DELETE", DELETE_MAPPING_NAME, DELETE_MAPPING_FULL_NAME, type, method, classImports, wildcardImports));

				// TODO: what method it should have? GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE?
				addIfNotNull(result, inspectMethodForAnnotation("ANY?", REQUEST_MAPPING_NAME, REQUEST_MAPPING_FULL_NAME, type, method, classImports, wildcardImports));
			}
		}

		return result;
	}

	private static void addIfNotNull(List<String> list, String value) {
		if (value != null) {
			list.add(value);
		}
	}

	private static String inspectMethodForAnnotation(String httpMethod, String annotationName, String annotationFullName, TypeDeclaration<?> type, MethodDeclaration method, Map<String, Boolean> classImports, Map<String, Boolean> wildcardImports) {
		Optional<AnnotationExpr> mappingAnnotation = method.getAnnotationByName(annotationName);
		boolean hasMapping = mappingAnnotation.isPresent();
		boolean importsMapping = classImports.getOrDefault(annotationFullName, Boolean.FALSE);
		boolean importsMappingsPackage = wildcardImports.getOrDefault(PKG_BIND_ANNOTATION, Boolean.FALSE);
		if (hasMapping && (importsMapping || importsMappingsPackage)) {
			String url = extractAnnotationValue(type, mappingAnnotation);
			if (url != null) {
				return httpMethod + " " + url;
			}
		}
		return null;
	}

	private static String extractAnnotationValue(TypeDeclaration<?> type, Optional<AnnotationExpr> annotationExpr) {
		AnnotationExpr annotation = annotationExpr.get();
		if (annotation.isSingleMemberAnnotationExpr()) {
			Expression value = annotation.asSingleMemberAnnotationExpr().getMemberValue();
			return extractExpressionValue(type, value, true);
		}
		if (annotation.isNormalAnnotationExpr()) {
			return annotation.asNormalAnnotationExpr()
				.getPairs()
				.stream()
				.filter(App::isPathAttribute)
				.findFirst()
				.map(MemberValuePair::getValue)
				.map(expr -> extractExpressionValue(type, expr, true))
				.orElse(null);
		}
		return null;
	}

	private static String extractExpressionValue(TypeDeclaration<?> type, Expression expression, boolean tryToResolve) {
		if (expression.isLiteralStringValueExpr()) {
			return expression.asLiteralStringValueExpr().getValue();
		}
		if (!tryToResolve) {
			return expression.toString();
		}
		String constantName = expression.toString();
		Optional<FieldDeclaration> fieldDeclaration = type.getFieldByName(constantName);
		if (fieldDeclaration.isPresent()) {
			return fieldDeclaration.get()
				.getVariables()
				.stream()
				.filter(var -> var.getName().getIdentifier().equals(constantName))
				.filter(var -> var.getInitializer().isPresent())
				.map(var -> var.getInitializer().get())
				.map(expr -> extractExpressionValue(type, expr, false))
				.findFirst()
				.orElse(constantName);
		}
		return constantName;
	}
	
	private static boolean isPathAttribute(MemberValuePair pair) {
		String attrName = pair.getName().asString();
		return "path".equals(attrName) || "value".equals(attrName);
	}
	
}
