/*
 * Copyright (c) 2013, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or
 * without fee is hereby granted, provided that the above copyright notice and this permission
 * notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT
 * SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
 * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

package scotty.template;

import bsh.EvalError;
import bsh.Interpreter;
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.parser.Utilities;

import java.io.*;
import java.util.List;

import static scotty.template.Tokens.*;

/**
 * Parses Scotty templates, applies data from the Database considering the given context, and produces transformed
 * output.
 */
public final class Parser {

	private Parser() {
	}

	public static void parse(final InputStream inputStream, final OutputStream outputStream, final Database database,
							 final Context context) throws IOException, EvalError {
		final Interpreter beanShell = new Interpreter();

		PrintStream printStream = new PrintStream(outputStream);

		beanShell.getNameSpace().importPackage(Database.class.getPackage().getName());
		beanShell.set("database", database);
		beanShell.set("context", context);
		beanShell.set("output", printStream);

		while (true) {
			if (!scanTo(Tokens.OPEN, inputStream, outputStream)) {
				break;
			}

			ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
			scanTo(Tokens.CLOSE, inputStream, scriptOutput);
			final String script = scriptOutput.toString();
			final char operator = script.charAt(0);
			final String body = script.substring(1).trim();
			String value;

			switch (operator) {
				case CONTEXT:
					outputStream.write(context.get(body, "").getBytes());
					break;
				case QUERY:
					final Context queryContext;
					final int endOfAttrName = body.indexOf(' ');
					final String attributeName;
					if (endOfAttrName == -1) {
						attributeName = body;
						queryContext = new Context(context);
					} else {
						attributeName = body.substring(0, endOfAttrName);
						queryContext = new Context(context, body.substring(endOfAttrName));
					}
					List<Context> matches = database.query(queryContext);
					if (matches.size() == 0) {
						break;
					}
					value = matches.get(0).get(attributeName);
					if (value != null) {
						outputStream.write(value.getBytes());
					}
					break;
				case IMPORT:
					final Context importContext;
					final int endOfFileName = body.indexOf(' ');
					final String fileName;
					if (endOfFileName == -1) {
						fileName = body;
						importContext = new Context(context);
					} else {
						fileName = body.substring(0, endOfFileName);
						importContext = new Context(context, body.substring(endOfFileName));
					}
					parse(Utilities.getResourceAsStream(fileName), outputStream, database, importContext);
					break;
				case TYPES:
					String[] types = body.split(",");
					for (String type : types) {
						if (!database.getContained().containsKey(type.trim())) {
							throw new IllegalStateException("Database lacks required type: " + type);
						}
					}
					break;
				case IN_CONTEXT:
					String[] keys = body.split(",");
					for (String key : keys) {
						if (!context.containsKey(key.trim())) {
							throw new IllegalStateException("Runtime context lacks required attribute: " + key);
						}
					}
					break;
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					beanShell.setOut(printStream);
					beanShell.eval(body);
					break;

				default:
					break;
			}
		}

		printStream.flush();
		outputStream.flush();
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

		outputStream.write(buffer.toString().getBytes());
		return false;
	}

}
