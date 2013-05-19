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

/**
 * Pair a score with some data item. Scores natural sort order is high to low, then age low to high.
 */
public final class Ranked<T> implements Comparable<Ranked> {
	private final Float score;
	private final Integer age;
	private final T data;

	public Ranked(Float score, Integer age, T data) {
		if (score == null || age == null) {
			throw new IllegalArgumentException("Scores and ages must have a non-null to rank objects.");
		}
		this.score = score;
		this.age = age;
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public Float getScore() {
		return score;
	}

	@Override
	public int compareTo(Ranked o) {
		int scoreCompare = o.score.compareTo(score);

		return scoreCompare != 0 ? scoreCompare : age.compareTo(o.age);
	}
}
