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

import almost.functional.Optional;
import almost.functional.utils.Preconditions;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Logger;

import static almost.functional.utils.LogFactory.getLogger;


public class ScottyUtilities {
    private static final Logger LOGGER = getLogger();

    private ScottyUtilities() {
    }

    public static String join(CharSequence delimeter, Iterable iterable) {
        Preconditions.checkNotNull(iterable, "Can't join a null iterable");
        Preconditions.checkNotNull(delimeter, " Null is not a valid delimeter");

        Iterator iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(iterator.next().toString());

        while (iterator.hasNext()) {
            stringBuilder.append(delimeter);
            stringBuilder.append(iterator.next().toString());
        }
        return stringBuilder.toString();
    }

    /**
     * Look up a resource as either file or failing that a resource from the jar.
     *
     * @param resource item name to look for
     * @return return input stream or null if not found
     */
    public static Optional<InputStream> getResourceAsStream(String resource) {
        try {
            return Optional.of((InputStream) new FileInputStream(resource));
        } catch (FileNotFoundException e) {
            LOGGER.fine(resource + " not found as file");
        }

        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            LOGGER.info("Failed to find " + resource + " as file or resource.");
            return Optional.empty();
        }
        return Optional.of(inputStream);
    }

    public static Optional<OutputStream> getPath(final String folder, final String filename) {
        if ("-".equals(folder)) {
            return Optional.of((OutputStream) System.out);
        }
        final Path path;
        if (folder == null) {
            path = FileSystems.getDefault().getPath(filename);
        } else {
            path = FileSystems.getDefault().getPath(folder, filename);
        }
        try {
            return Optional.of((OutputStream) new FileOutputStream(path.toString()));
        } catch (FileNotFoundException e) {
            LOGGER.warning("Path " + folder + "/" + filename + " not found");
            return Optional.empty();
        }
    }

}
