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
import scotty.util.Consumer;

import javax.script.ScriptException;
import java.io.IOException;

import static scotty.util.ArrayIterable.newIterable;
import static scotty.util.Iterables.forEach;

public class InContextOperator implements OperatorEvaluator {
    @Override
    public char getOperator() {
        return Tokens.IN_CONTEXT;
    }

    @Override
    public void eval(Database database, final Context context, Markup markup, ParsingContext parsingContext) throws IOException, ScriptException {
        String[] keys = markup.body.split(",");
        forEach(newIterable(keys), new Consumer<String>() {
            @Override
            public void accept(String key) {
                if (!context.containsKey(key.trim())) {
                    throw new IllegalStateException("Runtime context lacks required attribute: " + key);
                }
            }
        });
    }
}
