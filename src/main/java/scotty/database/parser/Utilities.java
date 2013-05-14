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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public final class Utilities {
	private Utilities() {
	}

	/**
	 * Checks if A is a superset of B. A must have all the attributes, with equal values of B.
	 */
	public static boolean superset(Context a, Context b) {
		if (b == null) {
			return true;
		}

		for (String key : b.keySet()) {
			String value = a.get(key);
			if (!b.get(key).equals(value)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * The similarity score is the count of all matching attributes between two contexts. If any attribute name is shared where
	 * the values differ the similarity is -1;
	 */
	public static int similarity(Context a, Context b) {
		int score = 0;
		if (a == null || b == null) {
			return score;
		}

		for (String key : b.keySet()) {
			if (!a.containsKey(key)) {
				continue;
			}

			if (!a.get(key).equals(b.get(key))) {
				return -1;
			}
			score++;
		}
		return score;
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
			int rank = similarity(context, criteria);
			if (rank > 0) {
				results.add(new Ranked<>(rank, context.getAge(), context));
			}
		}

		Collections.sort(results);
		return results;
	}

}
