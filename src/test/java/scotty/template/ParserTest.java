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

package scotty.template;

import org.junit.Test;
import scotty.database.Context;
import scotty.database.Database;

import java.io.FileInputStream;

/**
 *
 */
public class ParserTest {

    @Test
    public void parse() throws Exception {
        Database database = Database.parse("./target/test-classes/host.xml");
        try (FileInputStream fileInputStream = new FileInputStream("./target/test-classes/prop-template.scotty")) {
            Parser.parse(fileInputStream, System.out, database, new Context(), new NamedScriptEngine());
        }
    }
}
