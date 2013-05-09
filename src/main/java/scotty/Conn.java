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
public class Conn {
    private static final Logger LOGGER = Logger.getLogger(Conn.class.getName());

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

            Context context = new Context();
            if (commandLine.hasOption(CONTEXT)) {
                String[] assignments = commandLine.getOptionValue(CONTEXT).split(",");
                for (String assignment : assignments) {
                    String[] pair = assignment.split("=");
                    context.put(pair[0], pair[1]);
                }
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
