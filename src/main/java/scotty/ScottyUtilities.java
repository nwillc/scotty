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

package scotty;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 *
 */
public class ScottyUtilities {
    private static final Logger LOGGER = Logger.getLogger(ScottyUtilities.class.getName());

    private ScottyUtilities() {
    }

    /**
     * Look up a resource as either file or failing that a resource from the jar.
     *
     * @param resource item name to look for
     * @return return input stream or null if not found
     */
    public static InputStream getResourceAsStream(String resource) {
        try {
            return new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            LOGGER.fine("File not found: " + e);
        }

        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            LOGGER.info("Failed to find " + resource + " as resource or file.");
        }
        return inputStream;
    }

    public static OutputStream getPath(final String folder, final String filename) throws FileNotFoundException {
        if ("-".equals(folder)) {
            return System.out;
        }
        final Path path;
        if (folder == null) {
            path = FileSystems.getDefault().getPath(filename);
        } else {
            path = FileSystems.getDefault().getPath(folder, filename);
        }
        return new FileOutputStream(path.toString());
    }

}
