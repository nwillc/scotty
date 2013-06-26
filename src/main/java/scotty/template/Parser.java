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
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.parser.Utilities;

import javax.script.ScriptException;
import java.io.*;
import java.util.List;
import java.util.logging.Logger;

import static scotty.template.Tokens.*;

/**
 * Parses Scotty templates, applies data from the Database considering the given context, and produces transformed
 * output.
 */
public final class Parser {
	private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

	private Parser() {
	}

	/**
	 * Parse a template, handling the annotations it contains, employing a database and runtime context.
	 *
	 * @param sourceName   the template name if available.
	 * @param inputStream  the input stream of the template.
	 * @param outputStream where to send the output.
	 * @param database     the database to run queries against
	 * @param context      the runtime context of assignments
	 * @throws IOException if one of the files can not be read.
	 * @throws EvalError   if there is a syntax error in the bean shell
	 */
	public static void parse(String sourceName, final InputStream inputStream, final OutputStream outputStream, final Database database,
							 final Context context) throws IOException, ScriptException {
		parse(inputStream, outputStream, database, context, new BshScriptEngine(sourceName));
	}

	public static void parse(final InputStream inputStream, final OutputStream outputStream, final Database database,
							 final Context context, ScriptEngine scriptEngine) throws IOException, ScriptException {
		PrintStream printStream = new PrintStream(outputStream);


		try {
			scriptEngine.export("database", database);
			scriptEngine.export("context", context);
			scriptEngine.export("output", printStream);
		} catch (ScriptException scriptException) {
			LOGGER.severe(scriptException.toString());
			throw scriptException;
		}

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
						scriptEngine.setScriptName(fileName);
					}
					parse(Utilities.getResourceAsStream(fileName), outputStream, database, importContext, scriptEngine);
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
					try {
						scriptEngine.eval(body);
					} catch (ScriptException scriptException) {
						LOGGER.severe(scriptException.getFileName() + ": [" + scriptException.getFileName() + "] " + scriptException.getMessage());
						throw scriptException;
					}
					break;

				default:
					break;
			}
		}

		printStream.flush();
		outputStream.flush();
	}

	/**
	 * Scan for a pattern in a stream, shunting data that is passed by to an output stream.
	 *
	 * @param patternStr   the pattern to search in
	 * @param inputStream  the stream to read from
	 * @param outputStream the stream to send data we have passed to
	 * @return if the there was a match
	 * @throws IOException
	 */
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
