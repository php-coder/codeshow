package ru.mystamps.codeshow;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

class AppTests {

	// TODO: recognize all annotations on a class level only
	// TODO: recognize all annotations on a class level and method level
	// TODO: recognize fully-qualified form @org.springframework.stereotype.Controller
	// TODO: recognize annotations from an interface

	@Test
	void shouldDetectFullyImportedMappingsInsideController() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.stereotype.Controller;\n" +
			"import org.springframework.web.bind.annotation.GetMapping;\n" +
			"import org.springframework.web.bind.annotation.PutMapping;\n" +
			"import org.springframework.web.bind.annotation.PostMapping;\n" +
			"import org.springframework.web.bind.annotation.PatchMapping;\n" +
			"import org.springframework.web.bind.annotation.DeleteMapping;\n" +
			"import org.springframework.web.bind.annotation.RequestMapping;\n" +
			"\n" +
			"@Controller\n" +
			"public class Test {\n" +
			"\n" +
			"    @GetMapping(\"/get\")\n" +
			"    public void get() {}\n" +
			"\n" +
			"    @PutMapping(\"/put\")\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(\"/post\")\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(\"/patch\")\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(\"/delete\")\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(\"/request\")\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"GET /get",
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"ANY? /request"
		);
	}

	@Test
	void shouldDetectWildcardImportedMappingsInsideController() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.stereotype.Controller;\n" +
			"import org.springframework.web.bind.annotation.*;\n" +
			"\n" +
			"@Controller\n" +
			"public class Test {\n" +
			"\n" +
			"    @GetMapping(\"/get\")\n" +
			"    public void get() {}\n" +
			"\n" +
			"    @PutMapping(\"/put\")\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(\"/post\")\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(\"/patch\")\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(\"/delete\")\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(\"/request\")\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"GET /get",
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"ANY? /request"
		);
	}

	@Test
	void shouldDetectFullyImportedMappingsInsideRestController() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.web.bind.annotation.RestController;\n" +
			"import org.springframework.web.bind.annotation.GetMapping;\n" +
			"import org.springframework.web.bind.annotation.PutMapping;\n" +
			"import org.springframework.web.bind.annotation.PostMapping;\n" +
			"import org.springframework.web.bind.annotation.PatchMapping;\n" +
			"import org.springframework.web.bind.annotation.DeleteMapping;\n" +
			"import org.springframework.web.bind.annotation.RequestMapping;\n" +
			"\n" +
			"@RestController\n" +
			"public class Test {\n" +
			"\n" +
			"    @GetMapping(\"/get\")\n" +
			"    public void get() {}\n" +
			"\n" +
			"    @PutMapping(\"/put\")\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(\"/post\")\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(\"/patch\")\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(\"/delete\")\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(\"/request\")\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).contains(
			"GET /get",
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"ANY? /request"
		);
	}

	@Test
	void shouldDetectWildcardImportedMappingsInsideRestController() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.web.bind.annotation.*;\n" +
			"\n" +
			"@RestController\n" +
			"public class Test {\n" +
			"\n" +
			"    @GetMapping(\"/get\")\n" +
			"    public void get() {}\n" +
			"\n" +
			"    @PutMapping(\"/put\")\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(\"/post\")\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(\"/patch\")\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(\"/delete\")\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(\"/request\")\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"GET /get",
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"ANY? /request"
		);
	}

	// TODO: try to resolve a constant from the same class
	// TODO: try to resolve a constant from other class (Url.TEST_URL)
	@Test
	void shouldDetectGetMappingAnnotationWithAConstant() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.stereotype.Controller;\n" +
			"import org.springframework.web.bind.annotation.GetMapping;\n" +
			"\n" +
			"@Controller\n" +
			"public class Test {\n" +
			"    @GetMapping(TEST_URL)\n" +
			"    public void test() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactly("GET TEST_URL");
	}

}
