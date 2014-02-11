/*
 * Copyright (c) 2013-2014, nwillc@gmail.com
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

import com.google.common.base.Optional;
import scotty.Cli;
import scotty.database.Context;
import scotty.database.Database;
import scotty.util.Consumer;

import javax.script.ScriptException;
import java.io.*;
import java.util.List;
import java.util.logging.Logger;

import static scotty.ScottyUtilities.getPath;
import static scotty.ScottyUtilities.getResourceAsStream;
import static scotty.template.Tokens.*;
import static scotty.util.ArrayIterable.newArrayIterable;
import static scotty.util.Iterables.forEach;

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
	 * @param inputStream  the input stream of the template.
	 * @param outputStream where to send the output.
	 * @param database     the database to run queries against
	 * @param context      the runtime context of assignments
	 * @param namedScriptEngine the script engine being used
	 * @throws IOException     if one of the files can not be read.
	 * @throws ScriptException if there is a syntax error in the bean shell
	 */
	public static void parse(final InputStream inputStream, final OutputStream outputStream, final Database database,
							 final Context context, final NamedScriptEngine namedScriptEngine) throws IOException, ScriptException {
		ParsingContext parsingContext = new ParsingContext(namedScriptEngine, outputStream);
		export(database, context, parsingContext);
		while (true) {
			if (!scanTo(Tokens.OPEN, inputStream, parsingContext.getOutputStream())) {
				break;
			}

			ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
			if (!scanTo(Tokens.CLOSE, inputStream, scriptOutput)) {
				throw new IllegalStateException("Unclosed Scotty markup.");
			}
			final Markup markup = new Markup(scriptOutput.toString());
			String value;

			switch (markup.operator) {
				case CONTEXT:
					parsingContext.getOutputStream().write(context.get(markup.body, "").getBytes());
					break;
				case QUERY:
					final Context queryContext;
					final int endOfAttrName = markup.body.indexOf(' ');
					final String attributeName;
					if (endOfAttrName == -1) {
						attributeName = markup.body;
						queryContext = new Context(context);
					} else {
						attributeName = markup.body.substring(0, endOfAttrName);
						queryContext = new Context(context, markup.body.substring(endOfAttrName));
					}
					List<Context> matches = database.query(queryContext);
					if (matches.size() == 0) {
						break;
					}
					value = matches.get(0).get(attributeName);
					if (value != null) {
						parsingContext.getOutputStream().write(value.getBytes());
					}
					break;
				case IMPORT:
					final Context importContext;
					final int endOfFileName = markup.body.indexOf(' ');
					final String fileName;
					if (endOfFileName == -1) {
						fileName = markup.body;
						importContext = new Context(context);
					} else {
						fileName = markup.body.substring(0, endOfFileName);
						importContext = new Context(context, markup.body.substring(endOfFileName));
					}
					NamedScriptEngine newScriptEngine = new NamedScriptEngine(parsingContext.getLanguageName(), fileName);
					Optional<InputStream> streamOptional = getResourceAsStream(fileName);
					if (streamOptional.isPresent()) {
						parse(streamOptional.get(), parsingContext.getOutputStream(), database, importContext, newScriptEngine);
					}
					break;
				case TYPES:
					String[] types = markup.body.split(",");
					forEach(newArrayIterable(types), new Consumer<String>() {
						@Override
						public void accept(String type) {
							if (!database.getContained().containsKey(type.trim())) {
								throw new IllegalStateException("Database lacks required type: " + type);
							}
						}
					});
					break;
				case LANGUAGE:
					parsingContext.setScriptEngine(new NamedScriptEngine(markup.body, parsingContext.getScriptName()));
					export(database, context, parsingContext);
					break;
				case OUTPUT:
					parsingContext.getOutputStream().flush();
					parsingContext.getOutputStream().close();
					Optional<OutputStream> outputStreamOptional = getPath(database.get(Cli.FOLDER), markup.body);
					if (outputStreamOptional.isPresent()) {
						parsingContext.setOutputStream(outputStreamOptional.get());
						export(database, context, parsingContext);
					}
					break;
				case IN_CONTEXT:
					String[] keys = markup.body.split(",");
					forEach(newArrayIterable(keys), new Consumer<String>() {
						@Override
						public void accept(String key) {
							if (!context.containsKey(key.trim())) {
								throw new IllegalStateException("Runtime context lacks required attribute: " + key);
							}
						}
					});
					break;
				default:
					try {
						parsingContext.getScriptEngine().eval(markup.body);
					} catch (ScriptException scriptException) {
						LOGGER.severe(scriptException.getFileName() + ": [" + scriptException.getLineNumber() + "] " + scriptException.getMessage());
						throw scriptException;
					}
					break;
			}
		}

		parsingContext.getOutputStream().flush();
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

	private static void export(Database database, Context context, ParsingContext parsingContext) throws ScriptException {
		try {
			parsingContext.getScriptEngine().put("database", database);
			parsingContext.getScriptEngine().put("context", context);
			parsingContext.getScriptEngine().put("output", new PrintStream(parsingContext.getOutputStream()));
		} catch (ScriptException scriptException) {
			LOGGER.severe(scriptException.toString());
			throw scriptException;
		}
	}

}
