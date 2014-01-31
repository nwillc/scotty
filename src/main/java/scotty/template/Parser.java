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
     * @param scriptEngine the script engine being used
     * @throws IOException     if one of the files can not be read.
     * @throws ScriptException if there is a syntax error in the bean shell
     */
    public static void parse(final InputStream inputStream, OutputStream outputStream, final Database database,
                             final Context context, NamedScriptEngine scriptEngine) throws IOException, ScriptException {
        export(scriptEngine, database, context, outputStream);
        while (true) {
            if (!scanTo(Tokens.OPEN, inputStream, outputStream)) {
                break;
            }

            ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
            if (!scanTo(Tokens.CLOSE, inputStream, scriptOutput)) {
                throw new IllegalStateException("Unclosed Scotty markup.");
            }
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
                    NamedScriptEngine newScriptEngine = new NamedScriptEngine(scriptEngine.getLanguageName(), fileName);
                    Optional<InputStream> streamOptional = getResourceAsStream(fileName);
                    if (streamOptional.isPresent()) {
                        parse(streamOptional.get(), outputStream, database, importContext, newScriptEngine);
                    }
                    break;
                case TYPES:
                    String[] types = body.split(",");
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
                    scriptEngine = new NamedScriptEngine(body, scriptEngine.getScriptName());
                    export(scriptEngine, database, context, outputStream);
                    break;
                case OUTPUT:
                    outputStream.flush();
                    outputStream.close();
                    Optional<OutputStream> outputStreamOptional = getPath(database.get(Cli.FOLDER), body);
                    if (outputStreamOptional.isPresent()) {
                        outputStream = outputStreamOptional.get();
                        export(scriptEngine, database, context, outputStream);
                    }
                    break;
                case IN_CONTEXT:
                    String[] keys = body.split(",");
                    forEach(newArrayIterable(keys), new Consumer<String>() {
                        @Override
                        public void accept(String key) {
                            if (!context.containsKey(key.trim())) {
                                throw new IllegalStateException("Runtime context lacks required attribute: " + key);
                            }
                        }
                    });
                    break;
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    try {
                        scriptEngine.eval(body);
                    } catch (ScriptException scriptException) {
                        LOGGER.severe(scriptException.getFileName() + ": [" + scriptException.getLineNumber() + "] " + scriptException.getMessage());
                        throw scriptException;
                    }
                    break;
            }
        }

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

    private static void export(NamedScriptEngine scriptEngine, Database database, Context context, OutputStream outputStream) throws ScriptException {
        try {
            scriptEngine.put("database", database);
            scriptEngine.put("context", context);
            scriptEngine.put("output", new PrintStream(outputStream));
        } catch (ScriptException scriptException) {
            LOGGER.severe(scriptException.toString());
            throw scriptException;
        }
    }
}
