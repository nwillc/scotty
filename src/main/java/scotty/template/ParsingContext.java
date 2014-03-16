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

import scotty.database.Context;
import scotty.database.Database;

import javax.script.ScriptException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

import static almost.functional.utils.LogFactory.getLogger;

public class ParsingContext {
    private static final Logger LOGGER = getLogger();

    private NamedScriptEngine scriptEngine;
    private OutputStream outputStream;

    ParsingContext(NamedScriptEngine scriptEngine, OutputStream outputStream) {
        this.scriptEngine = scriptEngine;
        this.outputStream = outputStream;
    }

    public void export(Database database, Context context) throws ScriptException {
        try {
            getScriptEngine().put("database", database);
            getScriptEngine().put("context", context);
            getScriptEngine().put("output", new PrintStream(getOutputStream()));
        } catch (ScriptException scriptException) {
            LOGGER.severe(scriptException.toString());
            throw scriptException;
        }
    }

    public NamedScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(NamedScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String getScriptName() {
        return getScriptEngine().getScriptName();
    }

    public String getLanguageName() {
        return getScriptEngine().getLanguageName();
    }

    public void eval(String script) throws ScriptException {
        getScriptEngine().eval(script);
    }
}
