/*
 * Copyright (c) 2013, nwillc@gmail.com
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

package scotty;

import org.apache.commons.cli.*;
import scotty.database.Context;
import scotty.database.Database;
import scotty.template.NamedScriptEngine;

import javax.script.ScriptException;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scotty.Cli.*;
import static scotty.ScottyUtilities.getPath;
import static scotty.ScottyUtilities.getResourceAsStream;
import static scotty.database.parser.DbParserUtilities.print;
import static scotty.template.Parser.parse;

/**
 * Scotty's conn, where it all gets put together driven by command line.
 */
public final class Conn {
	private static final Logger LOGGER = Logger.getLogger(Conn.class.getName());

	private Conn() {
	}

	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();
		Options options = setupOptions();
		String templateFile;
		String scriptEngineName = NamedScriptEngine.DEFAULT_LANGUAGE;

		try {
			CommandLine commandLine = parser.parse(options, args);

			if (commandLine.hasOption(HELP)) {
				help(options, 0);
			}

			if (!commandLine.hasOption(DATABASE)) {
				LOGGER.severe("A database must be defined");
				help(options, 3);
			}

			String[] databaseFiles = commandLine.getOptionValue(DATABASE).split(",");
			Database database = Database.parse(databaseFiles);

			if (commandLine.hasOption(FOLDER)) {
				final String outputFolder = commandLine.getOptionValue(FOLDER);
				if ("-".equals(outputFolder)) {
					database.put(FOLDER, outputFolder);
				} else {
					final File file = new File(outputFolder);

					if (!(file.exists() && file.isDirectory() && file.canWrite())) {
						LOGGER.severe("Folder must exist, be a directory and be writable.");
						help(options, 4);
					}
					database.put(FOLDER, file.getAbsolutePath());
				}
			}

			if (commandLine.hasOption(PRINT)) {
				print(database, System.out);
				System.exit(0);
			}

			InputStream inputStream;
			if (commandLine.hasOption(TEMPLATE)) {
				templateFile = commandLine.getOptionValue(TEMPLATE);
				inputStream = getResourceAsStream(templateFile);
			} else {
				templateFile = "stdin";
				inputStream = System.in;
			}

			final OutputStream outputStream;
			if (commandLine.hasOption(OUTPUT)) {
				outputStream = getPath(database.get(FOLDER), commandLine.getOptionValue(OUTPUT));
			} else {
				outputStream = new FileOutputStream(FileDescriptor.out);
			}

			Context context;
			if (commandLine.hasOption(CONTEXT)) {
				context = new Context(commandLine.getOptionValue(CONTEXT));
			} else {
				context = new Context();
			}

			if (commandLine.hasOption(LANGUAGE)) {
				scriptEngineName = commandLine.getOptionValue(LANGUAGE);
			}

			NamedScriptEngine scriptEngine = new NamedScriptEngine(scriptEngineName, templateFile);
			parse(inputStream, outputStream, database, context, scriptEngine);
		} catch (ScriptException evalError) {
			LOGGER.severe("Exit on script error.");
			System.exit(2);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			System.exit(3);
		}
	}

	private static void help(Options options, int status) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar scotty.jar [options]", options);
		System.exit(status);
	}

	private static Options setupOptions() {
		Option option;

		Options options = new Options();

		option = new Option(HELP.substring(0, 1), HELP, false, "print this message.");
		options.addOption(option);

		option = new Option(DATABASE.substring(0, 1), DATABASE, true,
				"Comma separated of XML files containing types in database.");
		option.setArgName("filenames");
		option.setArgs(1);
		option.setRequired(false);
		options.addOption(option);

		option = new Option(CONTEXT.substring(0, 1), CONTEXT, true,
				"Comma separated list of assignments to act as the context.");
		option.setArgName("assignments");
		option.setArgs(1);
		options.addOption(option);

		option = new Option(TEMPLATE.substring(0, 1), TEMPLATE, true,
				"The name of the template file.");
		option.setArgName("filename");
		option.setArgs(1);
		option.setRequired(false);
		options.addOption(option);

		option = new Option(OUTPUT.substring(0, 1), OUTPUT, true,
				"The name of the output file.");
		option.setRequired(false);
		option.setArgName("filename");
		option.setArgs(1);
		options.addOption(option);

		option = new Option(FOLDER.substring(0, 1), FOLDER, true,
				"The folder to put output files in. Use \"-\" for standard out.");
		option.setRequired(false);
		option.setArgName("directory");
		option.setArgs(1);
		options.addOption(option);

		option = new Option(PRINT.substring(0, 1), PRINT, false,
				"Print the database out as a flattened text file.");
		option.setRequired(false);
		options.addOption(option);

		option = new Option(LANGUAGE.substring(0, 1), LANGUAGE, true,
				"Define an alternative scripting language. BeanShell used by default.");
		option.setRequired(false);
		option.setArgName("language");
		option.setArgs(1);
		options.addOption(option);

		return options;
	}
}
