package org.dstadler.antlr.fuzz.fuzz;

import org.apache.commons.io.FileUtils;
import org.dstadler.antlr.fuzz.Fuzz;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class FuzzTest {

	public static final String SAMPLE_GRAMMAR = "grammar Hello;\n" +
			"r  : 'hello' ID ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\t\\r\\n]+ -> skip ;";

	@BeforeAll
	public static void setUp() throws IOException, NoSuchFieldException {
		Fuzz.fuzzerInitialize();
	}

	@AfterAll
	public static void tearDown() throws IOException {
		Fuzz.fuzzerTearDown();
	}

	@Test
	public void testEmpty() throws IllegalAccessException {
		Fuzz.fuzzerTestOneInput("".getBytes(StandardCharsets.UTF_8));
		Fuzz.fuzzerTestOneInput(null);
	}

	@Test
	public void test() throws IllegalAccessException {
		Fuzz.fuzzerTestOneInput("abc".getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testWithGrammar() throws IllegalAccessException {
		Fuzz.fuzzerTestOneInput(SAMPLE_GRAMMAR.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testWithInvalidGrammar() throws IllegalAccessException {
		// this previously printed out an error the stdout, now it should
		// be prevented by redirecting stdout
		Fuzz.fuzzerTestOneInput((SAMPLE_GRAMMAR + "{").getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testClassCastException() throws IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput("R d=H|?#".getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testClassCastException2() throws IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput((
				"*%*\n" +
				"parser grammar a;\n" +
				"options {}"
		).getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testNullPointerException() throws IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput((
				"lexer grammar a;\n" +
				"FROM: R ;�REF: R;\n" +
				"fragment R:('r');"
		).getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testStringIndexOutOfBoundsException() throws IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput((
				"lexer grammar a;F:'\n" +
				";"
		).getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testArrayIndexOutOfBoundsException() throws IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput((
				"grammar a;\n" +
				"fragment V: '\\����F';"
		).getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testIndexOutOfBoundsException() throws IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput((
				"grammar a;\n" +
				"EOF : 'if' ;\n"
		).getBytes(StandardCharsets.UTF_8));
	}

	@Disabled("Used to reproduce a problem")
	@Test
	public void testCrashFile() throws IOException, IllegalAccessException {
		// this causes an Exception, we ignore this for now to continue fuzzing for other problems
		Fuzz.fuzzerTestOneInput(FileUtils.readFileToByteArray(new File("crash-849f6ba72aa68aabd7285a85802f9bdacebe72e6")));
	}

	@Disabled("Local test for verifying a slow run")
	@Test
	public void testSlowUnit() {
		//Fuzz.fuzzerTestOneInput(FileUtils.readFileToByteArray(new File("slow-unit-0a0b0ce97bb332cd9f8fde03e03840768a81d29d")));
	}
}