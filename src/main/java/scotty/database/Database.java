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

package scotty.database;

import org.xml.sax.SAXException;
import scotty.database.parser.DbParserUtilities;
import scotty.database.parser.Parser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Scotty database, basically a context with a contained map of Types.
 */
public class Database extends Context {

    /**
     * Parse a collection of streams open to Type data to create a Database.
     *
     * @param inputStreams Collection of input streams
     * @return Database of types
     * @throws IOException                  issue reading from stream
     * @throws SAXException                 XML content error
     * @throws ParserConfigurationException XML parser instantiation issue
     */
    public static Database parse(InputStream... inputStreams) throws IOException, SAXException, ParserConfigurationException {
        Database database = new Database();

        if (inputStreams != null) {
            for (InputStream inputStream : inputStreams) {
                Type type = Parser.parse(inputStream);
                database.getContained().put(type.getName(), type);
            }
        }
        return database;
    }

    /**
     * Parse a collection of files containing Type data to create a Database.
     *
     * @param files collection of files
     * @return Database of types
     * @throws IOException                  issue reading from stream
     * @throws SAXException                 XML content error
     * @throws ParserConfigurationException XML parser instantiation issue
     */
    public static Database parse(String... files) throws ParserConfigurationException, SAXException, IOException {
        if (files == null || files.length == 0) {
            return null;
        }

        Database database = new Database();
        for (String file : files) {
            Type type = Parser.parse(file);
            database.getContained().put(type.getName(), type);
        }
        return database;
    }

    /**
     * Return a list of instances matching a given set of criteria.
     *
     * @param criteria the criteria to match
     * @return a list of instances in match rank order
     */
    public List<Context> query(String criteria) {
        return query(new Context(criteria));
    }

    /**
     * Return a list of instances matching a given set of criteria.
     *
     * @param criteria the criteria to match
     * @return a list of instances in match rank order
     */
    public List<Context> query(Context criteria) {
        return DbParserUtilities.query(this, criteria);
    }

    /**
     * Return a set, in sorted order, of the values of a specified attribute of all the instances
     * matching a criteria.
     *
     * @param criteria the criteria
     * @param attr     the attribute name
     * @return sorted set of values
     */
    public Set<String> query(String criteria, String attr) {
        return query(new Context(criteria), attr);
    }

    /**
     * Return a set, in sorted order, of the values of a specified attribute of all the instances
     * matching a criteria.
     *
     * @param criteria the criteria
     * @param attr     the attribute name
     * @return sorted set of values
     */
    public Set<String> query(Context criteria, String attr) {
        List<Context> matches = query(criteria);
        Set<String> attributeValues = new TreeSet<>();
        for (Context c : matches) {
            Value v = c.getValue(attr);
            if (v == null) {
                continue;
            }

            for (String s : v.values()) {
                attributeValues.add(s);
            }
        }
        return attributeValues;
    }

    /**
     * Print out the contents of a database, flattening all the inheritance to the instance level and
     * showing the similarity score with the context passed in.
     *
     * @param printStream the output stream
     * @param context     the Context to compare instances to.
     */
    public void print(final PrintStream printStream, Context context) {
        DbParserUtilities.print(this, printStream, context);
    }
}
