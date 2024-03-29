package org.dstadler.antlr.fuzz;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullPrintStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * This class provides a simple target for fuzzing antlr v4 with Jazzer
 *
 * It calls methods to parse a grammar and process it similar to what
 * the antlr cli "tool" does.
 */
public class Fuzz {
	private static final ANTLRToolListener EMPTY_LISTENER = new ANTLRToolListener() {
		@Override
		public void info(String msg) {
		}

		@Override
		public void error(ANTLRMessage msg) {
		}

		@Override
		public void warning(ANTLRMessage msg) {
		}
	};

	private static File tempDir;
	private static Field haveOutputDir;

	public static void fuzzerInitialize() throws IOException, NoSuchFieldException {
		tempDir = File.createTempFile("antlr-fuzz", "");
		FileUtils.delete(tempDir);
		FileUtils.forceMkdir(tempDir);

		// need to set this to true, but it is protected
		Class<?> clazz = Tool.class;
		haveOutputDir = clazz.getDeclaredField("haveOutputDir");
		haveOutputDir.setAccessible(true);
	}

	public static void fuzzerTearDown() throws IOException {
		FileUtils.deleteDirectory(tempDir);
	}

	public static void fuzzerTestOneInput(byte[] input) throws IllegalAccessException {
		if (input == null) {
			return;
		}

		PrintStream stdout = System.out;
		PrintStream stderr = System.err;
		try {
			Tool tool = new Tool();
			tool.outputDirectory = tempDir.getAbsolutePath();
			haveOutputDir.set(tool, true);

			// Use an empty listener to silence error-output
			tool.addListener(EMPTY_LISTENER);

			// avoid error messages via System.out which cannot be avoided otherwise
			System.setOut(NullPrintStream.INSTANCE);
			System.setErr(NullPrintStream.INSTANCE);

			ANTLRInputStream in = new ANTLRInputStream(new ByteArrayInputStream(input));
			GrammarRootAST t = tool.parse("fuzzing", in);
			if (t == null) {
				return;
			}

			final Grammar g = tool.createGrammar(t);
			g.fileName = "fuzzing";

			tool.process(g, false);
			tool.process(g, true);
		} catch (IOException e) {
			// expected here
		} catch (ClassCastException e) {
			// ignore one ClassCastException that I found until it is fixed in antlr
			if (!e.getMessage().contains("org.antlr.v4.tool.ast.GrammarASTErrorNode cannot be cast to class org.antlr.v4.tool.ast.AltAST") &&
					!e.getMessage().contains("org.antlr.v4.tool.ast.GrammarASTErrorNode cannot be cast to org.antlr.v4.tool.ast.AltAST") &&
					!e.getMessage().contains("org.antlr.v4.tool.ast.GrammarAST cannot be cast to class org.antlr.v4.tool.ast.GrammarASTWithOptions") &&
					!e.getMessage().contains("org.antlr.v4.tool.ast.GrammarAST cannot be cast to org.antlr.v4.tool.ast.GrammarASTWithOptions")) {
				throw e;
			}
		} catch (/*StringIndexOutOfBoundsException |*/ NullPointerException | /*ArrayIndexOutOfBoundsException |*/
				IndexOutOfBoundsException e) {
			// all these exceptions are thrown currently in special cases,
			// see the unit-tests for minimal reproducing grammar-snippets
		} finally {
			System.setOut(stdout);
			System.setErr(stderr);
		}
	}
}
