/*
 * Copyright (c) 2015, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package scotty.database;

import almost.functional.Consumer;
import scotty.database.parser.DbParserUtilities;
import scotty.database.parser.Parser;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static almost.functional.utils.ArrayIterable.newIterable;
import static almost.functional.utils.Iterables.forEach;

/**
 * A Scotty database, basically a context with a contained map of Types.
 */
public class Database extends Context {

	/**
	 * Parse a collection of streams open to Type data to create a Database.
	 *
	 * @param inputStreams Collection of input streams
	 * @return Database of types
	 */
	public static Database parse(InputStream... inputStreams) {
		final Database database = new Database();
		forEach(newIterable(inputStreams), new Consumer<InputStream>() {
			@Override
			public void accept(InputStream stream) {
				Parser.parse(stream).ifPresent(new Consumer<Type>() {
					@Override
					public void accept(Type type) {
						database.getContained().put(type.getName(), type);
					}
				});
			}
		});
		return database;
	}

	/**
	 * Parse a collection of files containing Type data to create a Database.
	 *
	 * @param files collection of files
	 * @return Database of types
	 */
	public static Database parse(String... files) {
		final Database database = new Database();
		forEach(newIterable(files), new Consumer<String>() {
			@Override
			public void accept(String file) {
				Parser.parse(file).ifPresent(new Consumer<Type>() {
					@Override
					public void accept(Type type) {
						database.getContained().put(type.getName(), type);
					}
				});
			}
		});
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
	public Set<String> query(final Context criteria, final String attr) {
		final Set<String> attributeValues = new TreeSet<>();
		forEach(query(criteria), new Consumer<Context>() {
			@Override
			public void accept(Context context) {
				context.getValue(attr).ifPresent(new Consumer<Value>() {
					@Override
					public void accept(Value value) {
						forEach(value.values(), new Consumer<String>() {
							@Override
							public void accept(String s) {
								attributeValues.add(s);
							}
						});
					}
				});
			}
		});
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
