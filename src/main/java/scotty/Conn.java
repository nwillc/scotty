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
import scotty.template.Parser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scotty.Cli.*;

/**
 * Scotty's conn.
 */
public final class Conn {
	private static final Logger LOGGER = Logger.getLogger(Conn.class.getName());

	private Conn() {
	}

	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();
		Options options = setupOptions();

		try {
			CommandLine commandLine = parser.parse(options, args);

			if (commandLine.hasOption(HELP)) {
				help(options, 0);
			}

			String templateFile = commandLine.getOptionValue(TEMPLATE);
			FileInputStream inputStream = new FileInputStream(templateFile);

			String outputFile = commandLine.getOptionValue(OUTPUT);
			FileOutputStream outputStream = new FileOutputStream(outputFile);

			String[] databaseFiles = commandLine.getOptionValue(DATABASE).split(",");
			Database database = Database.parse(databaseFiles);

			Context context;
			if (commandLine.hasOption(CONTEXT)) {
				context = new Context(commandLine.getOptionValue(CONTEXT));
			} else {
				context = new Context();
			}

			Parser.parse(inputStream, outputStream, database, context);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			help(options, 1);
		}
	}

	private static void help(Options options, int status) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(Conn.class.getCanonicalName(), options);
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
		option.setRequired(true);
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
		option.setRequired(true);
		options.addOption(option);

		option = new Option(OUTPUT.substring(0, 1), OUTPUT, true,
				"The name of the output file.");
		option.setRequired(true);
		option.setArgName("filename");
		option.setArgs(1);
		options.addOption(option);

		return options;
	}
}
