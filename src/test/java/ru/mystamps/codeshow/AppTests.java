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
	// TODO: @RequestMapping(method) may have multiple values
	// TODO: @RequestMapping(value|path) may have multiple values
	// TODO: try to resolve a constant from other class (Url.TEST_URL)
	// TODO: add tests for other methods/annotations for the case of resolving constants from the same class

	@Test
	void shouldHandleGetMapping() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.web.bind.annotation.*;\n" +

			"@RestController\n" +
			"public class Test {\n" +

			// @GetMapping()
			"    @GetMapping(\"/get/1\")\n" +
			"    public void get1() {}\n" +

			"    String GET_2 = \"/get/2\";\n" +
			"    @GetMapping(GET_2)\n" +
			"    public void test2() {}\n" +

			"    @GetMapping(GET_3)\n" +
			"    public void test3() {}\n" +
			
			// @GetMapping(path = )
			"    @GetMapping(path = \"/get/100\")\n" +
			"    public void get100() {}\n" +

			"    String GET_101 = \"/get/101\";\n" +
			"    @GetMapping(path = GET_101)\n" +
			"    public void get101() {}\n" +

			"    @GetMapping(path = GET_102)\n" +
			"    public void test102() {}\n" +
			
			// @GetMapping(value = )
			"    @GetMapping(value = \"/get/200\")\n" +
			"    public void get200() {}\n" +
			
			"    String GET_201 = \"/get/201\";\n" +
			"    @GetMapping(value = GET_201)\n" +
			"    public void test201() {}\n" +
			
			"    @GetMapping(value = GET_202)\n" +
			"    public void test202() {}\n" +

			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"GET /get/1",
			"GET /get/2",
			"GET GET_3",
			"GET /get/100",
			"GET /get/101",
			"GET GET_102",
			"GET /get/200",
			"GET /get/201",
			"GET GET_202"
			);
	}
	
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
			"GET /request"
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
			"GET /request"
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
			"GET /request"
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
			"GET /request"
		);
	}

	@Test
	void shouldDetectMappingsWithStringAsPathAttribute() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.web.bind.annotation.*;\n" +
			"\n" +
			"@RestController\n" +
			"public class Test {\n" +
			"\n" +
			"    @PutMapping(path = \"/put\")\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(path = \"/post\")\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(path = \"/patch\")\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(path = \"/delete\")\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(path = \"/request\")\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"GET /request"
		);
	}

	@Test
	void shouldDetectMappingsWithConstantAsPathAttribute() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.web.bind.annotation.*;\n" +
			"\n" +
			"@RestController\n" +
			"public class Test {\n" +
			"\n" +
			"    @PutMapping(path = PUT_URL)\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(path = POST_URL)\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(path = PATCH_URL)\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(path = DELETE_URL)\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(path = REQUEST_URL)\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"PUT PUT_URL",
			"POST POST_URL",
			"PATCH PATCH_URL",
			"DELETE DELETE_URL",
			"GET REQUEST_URL"
		);
	}

	@Test
	void shouldDetectMappingsWithValueAttribute() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.web.bind.annotation.*;\n" +
			"\n" +
			"@RestController\n" +
			"public class Test {\n" +
			"\n" +
			"    @PutMapping(value = \"/put\")\n" +
			"    public void put() {}\n" +
			"\n" +
			"    @PostMapping(value = \"/post\")\n" +
			"    public void post() {}\n" +
			"\n" +
			"    @PatchMapping(value = \"/patch\")\n" +
			"    public void patch() {}\n" +
			"\n" +
			"    @DeleteMapping(value = \"/delete\")\n" +
			"    public void delete() {}\n" +
			"\n" +
			"    @RequestMapping(value = \"/request\")\n" +
			"    public void request() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactlyInAnyOrder(
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"GET /request"
		);
	}


}
