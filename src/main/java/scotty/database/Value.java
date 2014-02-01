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

package scotty.database;

import com.google.common.base.Predicate;
import scotty.database.parser.Similarity;

import java.util.Collection;
import java.util.LinkedList;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.contains;
import static scotty.util.ArrayIterable.newArrayIterable;
import static scotty.util.Iterables.forEach;

/**
 * The values of a Scotty attribute. A value contains 1 to n strings. The value is
 * considered a "Multi Value" if it contains more than a single string.
 */
public class Value implements Similarity<Value> {
    private final Collection<String> contents = new LinkedList<>();

    public Value() {
    }

    public Value(String ... values) {
        addAll(values);
    }

	public void addAll(String ... values) {
        forEach(newArrayIterable(values), new scotty.util.Consumer<String>() {
            @Override
            public void accept(String s) {
                add(s);
            }
        });
	}

    /**
     * Add a string value to this Value. Must be non null.
     *
     * @param value to add
     */
    public void add(String value) {
        if (value != null) {
            contents.add(value);
        }
    }

    /**
     * Return the collection of string values this Value represents.
     *
     * @return the values
     */
    public Collection<String> values() {
        return contents;
    }

    /**
     * Is this Value representing multiple values?
     *
     * @return true if the value represents multiple values
     */
    public boolean isMultiValue() {
        return contents.size() > 1;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String value : contents) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(',');
            }
            stringBuilder.append(value);
        }
        return stringBuilder.toString();
    }

    @Override
    public float similarity(Value b) {
        if (b == null) {
            return NOT_SIMILAR;
        }

        float score = SIMILAR;

        if (isMultiValue()) {
            score -= DOWN_GRADE;
        }

        if (b.isMultiValue()) {
            score -= DOWN_GRADE;
        }

		return any(b.values(), new Predicate<String>() {
			@Override
			public boolean apply(String s) {
				return contains(values(), s);
			}
		}) ? score : NOT_SIMILAR;
    }
}
