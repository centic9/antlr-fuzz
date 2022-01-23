package org.dstadler.antlr.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * This class provides a simple target for fuzzing antlr v4 with Jazzer
 *
 * It calls methods to parse a grammar and process it similar to what
 * the antlr cli "tool" does.
 */
public class Fuzz {
	private static File tempDir;

	public static void fuzzerInitialize() throws IOException {
		tempDir = File.createTempFile("antlr-fuzz", "");
		FileUtils.delete(tempDir);
		FileUtils.forceMkdir(tempDir);
	}

	public static void fuzzerTearDown() throws IOException {
		FileUtils.deleteDirectory(tempDir);
	}

	public static void fuzzerTestOneInput(FuzzedDataProvider data) {
		try {
			Tool tool = new Tool();
			tool.outputDirectory = tempDir.getAbsolutePath();

			ANTLRInputStream in = new ANTLRInputStream(new ByteArrayInputStream(data.consumeRemainingAsBytes()));
			GrammarRootAST t = tool.parse("fuzzing", in);
			if (t == null) {
				return;
			}

			final Grammar g = tool.createGrammar(t);
			g.fileName = "fuzzing";

			tool.process(g, false);
		} catch (IOException e) {
			// expected here
		}
	}
}
