package org.dstadler.antlr.fuzz.fuzz;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dstadler.antlr.fuzz.Fuzz;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.IOException;

class FuzzTest {
	FuzzedDataProvider provider = mock(FuzzedDataProvider.class);

	@BeforeAll
	public static void setUp() throws IOException {
		Fuzz.fuzzerInitialize();
	}

	@AfterAll
	public static void tearDown() throws IOException {
		Fuzz.fuzzerTearDown();
	}

	@Test
	public void test() {
		Fuzz.fuzzerTestOneInput(provider);

		when(provider.consumeRemainingAsString()).thenReturn("abc");

		Fuzz.fuzzerTestOneInput(provider);
	}

	@Test
	public void testWithGrammar() {
		when(provider.consumeRemainingAsString()).thenReturn(
			"grammar Hello;\n" +
			"r  : 'hello' ID ;\n" +
			"ID : [a-z]+ ;\n" +
			"WS : [ \\t\\r\\n]+ -> skip ;");

		Fuzz.fuzzerTestOneInput(provider);
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