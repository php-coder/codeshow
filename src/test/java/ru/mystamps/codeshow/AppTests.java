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
			"    @GetMapping(path = \"/get\")\n" +
			"    public void get() {}\n" +
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
			"GET /get",
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
			"    @GetMapping(path = GET_URL)\n" +
			"    public void get() {}\n" +
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
			"GET GET_URL",
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
			"    @GetMapping(value = \"/get\")\n" +
			"    public void get() {}\n" +
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
			"GET /get",
			"PUT /put",
			"POST /post",
			"PATCH /patch",
			"DELETE /delete",
			"GET /request"
		);
	}

	// TODO: try to resolve a constant from other class (Url.TEST_URL)
	// TODO: add tests for other methods/annotations
	@Test
	void shouldDetectGetMappingAnnotationWithAConstant() {
		// given
		CompilationUnit cu = JavaParser.parse("" +
			"import org.springframework.stereotype.Controller;\n" +
			"import org.springframework.web.bind.annotation.GetMapping;\n" +
			"\n" +
			"@Controller\n" +
			"public class Test {\n" +
			"    String TEST_URL1 = \"/get1\";\n" +
			"    String TEST_URL2 = \"/get2\";\n" +
			"    String TEST_URL3 = \"/get3\";\n" +
			"\n"+
			"    @GetMapping(TEST_URL1)\n" +
			"    public void test1() {}\n" +
			"\n"+
			"    @GetMapping(value = TEST_URL2)\n" +
			"    public void test2() {}\n" +
			"\n"+
			"    @GetMapping(path = TEST_URL3)\n" +
			"    public void test3() {}\n" +
			"}"
		);
		// when
		List<String> endpoints = App.collectEndpoints(cu);
		// then
		assertThat(endpoints).containsExactly("GET /get1", "GET /get2", "GET /get3");
	}

}
