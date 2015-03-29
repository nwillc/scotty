/*
 * Copyright (c) 2015, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package scotty.template;

import almost.functional.Optional;
import scotty.database.Context;
import scotty.database.Database;
import scotty.template.operator.*;

import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static almost.functional.utils.ArrayIterable.newIterable;
import static almost.functional.utils.Iterables.find;


/**
 * Parses Scotty templates, applies data from the Database considering the given context, and produces transformed
 * output.
 */
public final class Parser {
    private static final OperatorEvaluator DEFAULT_OP = new ScriptOperator();
    private static final OperatorEvaluator[] OPERATOR_EVALUATORS = {
			new ContextOperator(),
			new QueryOperator(),
			new ImportOperator(),
            new TypesOperator(),
			new LanguageOperator(),
			new OutputOperator(),
			new InContextOperator()};

    private Parser() {
    }

    /**
     * Parse a template, handling the annotations it contains, employing a database and runtime context.
     *
     * @param inputStream       the input stream of the template.
     * @param outputStream      where to send the output.
     * @param database          the database to run queries against
     * @param context           the runtime context of assignments
     * @param namedScriptEngine the script engine being used
     * @throws IOException     if one of the files can not be read.
     * @throws ScriptException if there is a syntax error in the bean shell
     */
    public static void parse(final InputStream inputStream, final OutputStream outputStream, final Database database,
                             final Context context, final NamedScriptEngine namedScriptEngine) throws IOException, ScriptException {
        ParsingContext parsingContext = new ParsingContext(namedScriptEngine, outputStream);
        parsingContext.export(database, context);
        while (true) {
            Optional<Markup> markupOptional = nextMarkup(inputStream, parsingContext);

            if (!markupOptional.isPresent()) {
                break;
            }

			Markup markup = markupOptional.get();
            find(newIterable(OPERATOR_EVALUATORS), new OperatorFor(markup)).orElse(DEFAULT_OP).evaluate(database, context, markup, parsingContext);
        }

        parsingContext.getOutputStream().flush();
    }

	/**
	 * Find the next markup in the input stream. Data preceding the next markup is copied to the output.
	 * @throws IOException
	 */
    private static Optional<Markup> nextMarkup(InputStream inputStream, ParsingContext parsingContext) throws IOException {
        if (!scanTo(Tokens.OPEN, inputStream, parsingContext.getOutputStream())) {
           return Optional.empty();
        }
        ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
        if (!scanTo(Tokens.CLOSE, inputStream, scriptOutput)) {
            throw new IllegalStateException("Unclosed Scotty markup.");
        }
        return Optional.of(new Markup(scriptOutput.toString()));
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
