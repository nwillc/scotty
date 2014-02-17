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

package scotty.database.parser;

import static scotty.util.Preconditions.checkNotNull;

/**
 * A comparable container to hold data and ranking. The rank is a combination of the "score" and the "age".
 * The natural order of the ranks are scores high to low, and then age low to high.
 */
public final class Ranked<T> implements Comparable<Ranked> {
    private final Float score;
    private final Integer age;
    private final T data;

    public Ranked(Float score, Integer age, T data) {
		checkNotNull(score, "Ranked must have a valid score");
		checkNotNull(age, "Ranked must have a valid age");
        this.score = score;
        this.age = age;
        this.data = data;
    }

    public T getData() {
        return data;
    }


    @Override
    public int compareTo(Ranked o) {
        int scoreCompare = o.score.compareTo(score);

        return scoreCompare != 0 ? scoreCompare : age.compareTo(o.age);
    }
}
