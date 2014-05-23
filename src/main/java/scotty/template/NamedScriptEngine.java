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
public class NamedScriptEngine {
    public static final String DEFAULT_LANGUAGE = "beanshell";
	private static final String ENGINE_NAME_SPACE_KEY = "org_beanshell_engine_namespace";
	private static final String BEANSHELL = DEFAULT_LANGUAGE;
	private static final String SCRIPT_CONTEXT = "javax_script_context";
	private final String languageName;
	private final ScriptEngine scriptEngine;
	private String scriptName = null;

	/**
	 * Basic constructor. Instantiates a beanshell engine with no script name.
	 */
	public NamedScriptEngine() {
		this(BEANSHELL, null);
	}

	/**
	 * Instantiate a script engine of a specified language type and with a specified script name.
	 *
	 * @param languageName the language
	 * @param scriptName   the script
	 */
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

	/**
	 * Set the script name the engine will be evaluating, used solely to enhance error messages.
	 *
	 * @param scriptName the name
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	/**
	 * Put an object into the languages top level context with an associated name. A way to expose variables.
	 *
	 * @param name   The name
	 * @param object the object
	 * @throws ScriptException it the object can not be put into the script context
	 */
	public void put(String name, Object object)  {
		scriptEngine.put(name, object);
	}

	/**
	 * Evaluate a script
	 *
	 * @param script th script
	 * @throws ScriptException if script fails to evaluate
	 */
	public void eval(String script) throws ScriptException {
		try {
			scriptEngine.eval(script);
		} catch (ScriptException se) {
			if (scriptName == null) {
				throw se;
			}
			// Add the script name to the exception
			throw new ScriptException(se.getMessage(), scriptName, se.getLineNumber());
		}
	}

	/**
	 * Get the script name the engine is working on.
	 *
	 * @return the name
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * Get the language name of this engine.
	 *
	 * @return the language
	 */
	public String getLanguageName() {
		return languageName;
	}

}
