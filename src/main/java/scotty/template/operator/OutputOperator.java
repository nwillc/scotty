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

package scotty.template.operator;


import almost.functional.Optional;
import scotty.Cli;
import scotty.database.Context;
import scotty.database.Database;
import scotty.template.Markup;
import scotty.template.ParsingContext;
import scotty.template.Tokens;

import javax.script.ScriptException;
import java.io.IOException;
import java.io.OutputStream;

import static scotty.util.ScottyUtilities.getPath;

public class OutputOperator implements OperatorEvaluator {
    @Override
    public char getOperator() {
        return Tokens.OUTPUT;
    }

    @Override
    public void evaluate(Database database, Context context, Markup markup, ParsingContext parsingContext) throws IOException, ScriptException {
        parsingContext.getOutputStream().flush();
        parsingContext.getOutputStream().close();
        Optional<OutputStream> outputStreamOptional = getPath(database.get(Cli.FOLDER), markup.body);
        if (outputStreamOptional.isPresent()) {
            parsingContext.setOutputStream(outputStreamOptional.get());
            parsingContext.export(database, context);
        }
    }
}
