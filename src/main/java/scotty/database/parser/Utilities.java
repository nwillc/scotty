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
 * Utility methods used by the database portions of scotty.
 */
public final class Utilities {
	private Utilities() {
	}

	public static String find(Context context, String name) {
		String[] parts = name.split("\\.");


		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			if ((i == parts.length - 1) || !context.isContainer()) {
				return context.get(part);
			}

			context = context.getContained().get(part);
			if (context == null) {
				return null;
			}
		}
		return null;
	}

	public static List<Context> query(Context context, Context criteria) {
		List<Context> contexts = new LinkedList<>();
		List<Ranked<Context>> results = rankedQuery(context, criteria);
		for (Ranked<Context> ranked : results) {
			contexts.add(ranked.getData());
		}
		return contexts;
	}

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
