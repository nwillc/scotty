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

package contracts;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

@SuppressWarnings("unchecked")
public abstract class ComparableContract<T extends Comparable> {

	protected abstract T getValue();
	protected abstract T getEqualToValue();
	protected abstract T getLessThanValue();
	protected abstract T getGreaterThanValue();

	@Test
	public void shouldThrowExceptionForNull() throws Exception {
		final T value = getValue();
		assertThat(value).isNotNull();
		try {
			value.compareTo(null);
			failBecauseExceptionWasNotThrown(NullPointerException.class);
		} catch (NullPointerException e) {}
	}

	@Test
	public void shouldThrowExceptionForBadCast() throws Exception {
		final T value = getValue();
		assertThat(value).isNotNull();
		try {
			value.compareTo(new Object());
			failBecauseExceptionWasNotThrown(ClassCastException.class);
		} catch (ClassCastException e) {}
	}

	@Test
	public void shouldReturnZeroOnEquality() throws Exception {
		final T value = getValue();
		final T equal = getEqualToValue();

		assertThat(value).isNotNull();
		assertThat(equal).isNotNull();
		assertThat(value.compareTo(equal)).isEqualTo(0);
	}

	@Test
	public void shouldReturnNegativeOnLessThan() throws Exception {
		final T value = getValue();
		final T lessThan = getGreaterThanValue();

		assertThat(value).isNotNull();
		assertThat(lessThan).isNotNull();
		assertThat(value.compareTo(lessThan)).isLessThan(0);
	}

	@Test
	public void shouldReturnPositiveOnGreaterThan() throws Exception {
		final T value = getValue();
		final T greaterThan = getLessThanValue();

		assertThat(value).isNotNull();
		assertThat(greaterThan).isNotNull();
		assertThat(value.compareTo(greaterThan)).isGreaterThan(0);
	}
}
