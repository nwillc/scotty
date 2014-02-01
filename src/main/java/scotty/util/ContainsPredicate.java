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

package scotty.util;


import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.contains;

public class ContainsPredicate<T,I extends Iterable<T>> implements Predicate<T> {
	final private I iterable;

	public ContainsPredicate(I iterable) {
		this.iterable = iterable;
	}

	public static <T,I extends Iterable<T>> ContainsPredicate<T,I> newContainsPredicate(I i) {
		return new ContainsPredicate<>(i);
	}

	@Override
	public boolean apply(T t) {
		return contains(iterable, t);
	}
}
