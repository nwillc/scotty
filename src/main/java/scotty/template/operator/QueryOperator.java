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

import scotty.database.Context;
import scotty.database.Database;
import scotty.template.Markup;
import scotty.template.ParsingContext;
import scotty.template.Tokens;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;

public class QueryOperator implements OperatorEvaluator {
    @Override
    public char getOperator() {
        return Tokens.QUERY;
    }

    @Override
    public void evaluate(Database database, Context context, Markup markup, ParsingContext parsingContext) throws IOException, ScriptException {
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
            return;
        }
        String value = matches.get(0).get(attributeName);
        if (value != null) {
            parsingContext.getOutputStream().write(value.getBytes());
        }
    }
}
