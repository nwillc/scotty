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

package scotty.util.function;

import java.util.NoSuchElementException;

import static scotty.util.Preconditions.checkNotNull;

/**
 * An Optional implementation.
 */
public final class Optional<T> {
    private static final Optional EMPTY = new Optional<>();
    private final T optional;

    /**
     * Instantiate an Optional of a given non-null value.
     *
     * @param optional the value.
     */
    private Optional(T optional) {
		checkNotNull(optional, "Optionals may not contain null value");
        this.optional = optional;
    }

    /**
     * Instantiate an empty Optional.
     */
    private Optional() {
        optional = null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> empty() {
        return EMPTY;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public T get() throws NoSuchElementException {
        if (!isPresent()) {
            throw new NoSuchElementException("Attempting to get an empty Optional");
        }
        return optional;
    }

    public boolean isPresent() {
        return optional != null;
    }

	/**
	 * If the optional is present, consume it.
	 * @param consumer function to consume the optional if present
	 */
	public void ifPresent(Consumer<? super T> consumer){
		if (isPresent()) {
			consumer.accept(get());
		}
	}

	/**
	 * If the optional is empty return another value.
	 * @param other the other value
	 * @return the optional if present or other if empty
	 */
    public T orElse(T other) {
        if (isPresent()) {
            return get();
        }

        return other;
    }

	/**
	 * Transform the optional to and optional of another type via a function.
	 * @param function
	 * @param <V>
	 * @return
	 */
	public <V> Optional<V> transform(Function<T,V> function){
		if (isPresent()) {
			return Optional.of(function.apply(get()));
		} else {
			return Optional.empty();
		}
	}
}
