package scotty.template;

import java.io.OutputStream;

public class ParsingContext {
    private NamedScriptEngine scriptEngine;
    private OutputStream outputStream;

    ParsingContext(NamedScriptEngine scriptEngine, OutputStream outputStream) {
        this.scriptEngine = scriptEngine;
        this.outputStream = outputStream;
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
}
