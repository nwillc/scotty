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

import scotty.database.Context;

/**
 * A Context that has a fixed name attribute.
 */
public abstract class NamedContext extends Context implements Comparable<Context> {

    protected NamedContext(Context parentContext, String name) {
        super(parentContext);
        if (name == null ) {
            name = getElementType() + "-" + getAge();
        }
        put(getElementType(), name);
    }

    /**
     * Indicate which attribute is considered to be the name.
     *
     * @return the name attribute
     */
    protected abstract String getElementType();

    /**
     * Get the name attributes value.
     *
     * @return the value of the name attribute.
     */
    public String getName() {
        return get(getElementType());
    }

    @Override
    public int compareTo(Context o) {
        return getName().compareTo(((NamedContext) o).getName());
    }
}
