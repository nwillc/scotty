package scotty.template;

import bsh.EvalError;
import bsh.Interpreter;
import scotty.database.Context;
import scotty.database.Database;

import java.io.*;

/**
 * Parses SCoTTY templates, applies data from the Database considering the given context, and produces transformed output.
 */
public final class Parser {

	private Parser() {
	}

	public static void parse(final InputStream inputStream, final OutputStream outputStream, final Database database, final Context context) throws IOException, EvalError {
		final Interpreter beanShell = new Interpreter();

		beanShell.getNameSpace().importPackage("scotty.database");
		beanShell.set("database", database);
		beanShell.set("context", context);

		try (PrintStream output = new PrintStream(outputStream)) {
			beanShell.set("output", output);

			while (true) {
				if (!scanTo(Tokens.OPEN, inputStream, outputStream)) {
					break;
				}

				ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
				scanTo(Tokens.CLOSE, inputStream, scriptOutput);
				final String script = scriptOutput.toString();
				final char operator = script.charAt(0);
				final String scriptBody = script.substring(1).trim();
				String value = null;

				switch (operator) {

					case Tokens.FIND:
						value = database.find(scriptBody);
						if (value != null) {
							outputStream.write(value.getBytes());
						}
						break;

					case Tokens.QUERY:
						final Context queryContext;
						final int endOfAttrName = scriptBody.indexOf(' ');
						final String attributeName;
						if (endOfAttrName == -1) {
							attributeName = scriptBody;
							queryContext = new Context(context);
						} else {
							attributeName = scriptBody.substring(0, endOfAttrName);
							queryContext = new Context(context, scriptBody.substring(endOfAttrName));
						}
						value = database.match(attributeName, queryContext);
						if (value != null) {
							outputStream.write(value.getBytes());
						}
						break;

					case ' ':
					case '\t':
					case '\n':
					case '\r':
						beanShell.setOut(output);
						beanShell.eval(scriptBody);
						break;

					default:
						break;
				}
			}

			outputStream.flush();
		}
	}

	private static boolean scanTo(String patternStr, InputStream inputStream, OutputStream outputStream) throws IOException {
		final CharQueue pattern = new CharQueue(patternStr, null);
		final CharQueue buffer = new CharQueue(patternStr.length(), outputStream);

		int ch;
		while ((ch = inputStream.read()) > -1) {
			buffer.add((char) ch);
			if (pattern.compareTo(buffer) == 0) {
				return true;
			}
		}
		return false;
	}

}
