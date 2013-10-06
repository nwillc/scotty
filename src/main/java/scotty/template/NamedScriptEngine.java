/*
 * Copyright (c) 2013, nwillc@gmail.com
 *
 *     Permission to use, copy, modify, and/or distribute this software for any purpose with or
 *     without fee is hereby granted, provided that the above copyright notice and this permission
 *     notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 *     THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT
 *     SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
 *     ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 *     CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *     OR PERFORMANCE OF THIS SOFTWARE.
 */

package scotty.template;

import bsh.ExternalNameSpace;
import bsh.NameSpace;
import bsh.engine.ScriptContextEngineView;
import scotty.database.Database;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * ScriptEngine support wrapper.
 */
public class NamedScriptEngine implements Cloneable {
	private static final String ENGINE_NAME_SPACE_KEY = "org_beanshell_engine_namespace";
	private static final String BEANSHELL = "beanshell";
	private static final String SCRIPT_CONTEXT = "javax_script_context";
	private final String languageName;
	private final ScriptEngine scriptEngine;
	private String scriptName = null;

	public NamedScriptEngine() {
		this(BEANSHELL, null);
	}

	public NamedScriptEngine(String languageName, String scriptName) {
		this.languageName = languageName;
		setScriptName(scriptName);
		ScriptEngineManager factory = new ScriptEngineManager();
		scriptEngine = factory.getEngineByName(languageName);
		if (languageName.toLowerCase().equals(BEANSHELL)) {
			// beanshell can share namespaces with Java
			ScriptContext scriptContext = scriptEngine.getContext();
			NameSpace ns = (NameSpace) scriptContext.getAttribute(ENGINE_NAME_SPACE_KEY, ScriptContext.ENGINE_SCOPE);
			if (ns == null) {
				ns = new ExternalNameSpace(null, SCRIPT_CONTEXT, new ScriptContextEngineView(scriptContext));
				scriptContext.setAttribute(ENGINE_NAME_SPACE_KEY, ns, ScriptContext.ENGINE_SCOPE);
			}
			ns.importPackage(Database.class.getPackage().getName());
		}
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public void export(String name, Object object) throws ScriptException {
		scriptEngine.put(name, object);
	}

	public void eval(String script) throws ScriptException {
		try {
			scriptEngine.eval(script);
		} catch (ScriptException se) {
			// Add the script languageName to the exception
			throw new ScriptException(se.getMessage(), scriptName, se.getLineNumber());
		}
	}

	public String getScriptName() {
		return scriptName;
	}

	public String getLanguageName() {
		return languageName;
	}

}
