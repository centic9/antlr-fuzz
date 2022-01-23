package org.dstadler.antlr.fuzz.fuzz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dstadler.antlr.fuzz.Fuzz;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class FuzzTest {

	public static final String SAMPLE_GRAMMAR = "grammar Hello;\n" +
			"r  : 'hello' ID ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\t\\r\\n]+ -> skip ;";

	@BeforeAll
	public static void setUp() throws IOException {
		Fuzz.fuzzerInitialize();
	}

	@AfterAll
	public static void tearDown() throws IOException {
		Fuzz.fuzzerTearDown();
	}

	@Test
	public void testEmpty() {
		Fuzz.fuzzerTestOneInput("".getBytes(StandardCharsets.UTF_8));
		Fuzz.fuzzerTestOneInput(null);
	}

	@Test
	public void test() {
		Fuzz.fuzzerTestOneInput("abc".getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testWithGrammar() {
		Fuzz.fuzzerTestOneInput(SAMPLE_GRAMMAR.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testLog() {
		// should not be logged
		Logger LOG = LogManager.getLogger(FuzzTest.class);
		LOG.atError().log("Test log output which should not be visible -----------------------");
	}

	@Disabled("Local test for verifying a slow run")
	@Test
	public void testSlowUnit() {
		//Fuzz.fuzzerTestOneInput(FileUtils.readFileToByteArray(new File("slow-unit-0a0b0ce97bb332cd9f8fde03e03840768a81d29d")));
	}
}