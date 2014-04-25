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

package scotty.template.operator;

import almost.functional.Optional;
import scotty.database.Context;
import scotty.database.Database;
import scotty.template.*;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;

import static scotty.util.ScottyUtilities.getResourceAsStream;

public class ImportOperator implements OperatorEvaluator {
    @Override
    public char getOperator() {
        return Tokens.IMPORT;
    }

    @Override
    public void evaluate(Database database, Context context, Markup markup, ParsingContext parsingContext) throws IOException, ScriptException {
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
            Parser.parse(streamOptional.get(), parsingContext.getOutputStream(), database, importContext, newScriptEngine);
        }
    }
}
