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

package scotty.database.parser;

import scotty.database.Context;
import scotty.database.Database;
import scotty.database.Instance;
import scotty.database.Type;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility methods used by the database portions of Scotty.
 */
public final class Utilities {
	private Utilities() {
	}

    /**
     * Query a context for all the contexts within it that match a criteria.
     * @param context The context to query.
     * @param criteria The criteria to match.
     * @return the list of matches in ranked order
     */
    public static List<Context> query(Context context, Context criteria) {
		List<Context> contexts = new LinkedList<>();
		List<Ranked<Context>> results = rankedQuery(context, criteria);
		for (Ranked<Context> ranked : results) {
			contexts.add(ranked.getData());
		}
		return contexts;
	}

    /**
     * Query a context for all the contexts within it that match a criteria.
     * @param context The context to query
     * @param criteria the criteria to match
     * @return the results, with their associated ranks in ranked order
     */
	public static List<Ranked<Context>> rankedQuery(Context context, Context criteria) {
		List<Ranked<Context>> results = new LinkedList<>();

		if (context.isContainer()) {
			for (Context child : context.getContained().values()) {
				results.addAll(rankedQuery(child, criteria));
			}
		} else {
			float rank = context.similarity(criteria);
			if (rank > Similarity.NOT_SIMILAR) {
				results.add(new Ranked<>(rank, context.getAge(), context));
			}
		}

		Collections.sort(results);
		return results;
	}

    /**
     * Print out the contents of a database, flattening all the inheritance to the instance level.
     * @param database the database
     * @param printStream the output stream
     */
	public static void print(Database database, PrintStream printStream) {
		List<Context> types = new LinkedList<>(database.getContained().values());
		Collections.sort(types);
		for (Context type : types) {
			printStream.format("Type: %s\n", ((Type) type).getName());
			List<Context> instances = new LinkedList<>(type.getContained().values());
			Collections.sort(instances);
			for (Context instance : instances) {
				printStream.format("\tInstance: %s\n", ((Instance) instance).getName());
				List<String> attrNames = new LinkedList<>(instance.keySet());
				Collections.sort(attrNames);
				for (String key : attrNames) {
					printStream.format("\t\t%20s: %s\n", key, instance.get(key));
				}
			}
		}
	}
}
