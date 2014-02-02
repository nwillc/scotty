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

package scotty.template;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import scotty.database.Context;
import scotty.database.Database;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.fest.assertions.api.Assertions.assertThat;
import static scotty.ScottyUtilities.getResourceAsStream;

public class ParserTest {
    private Database database;

    @Before
    public void setup() throws Exception {
        database = Database.parse("./target/test-classes/host.xml");
    }

    @Test
    public void parse() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream("./target/test-classes/prop-template.scotty")) {
            Parser.parse(fileInputStream, System.out, database, new Context(), new NamedScriptEngine());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void badParse() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream("./target/test-classes/unclosed.scotty")) {
            Parser.parse(fileInputStream, System.out, database, new Context(), new NamedScriptEngine());
        }
    }

    @Test
    public void languageChanges() throws Exception {
        Optional<InputStream> inputStreamOptional  = getResourceAsStream("language_changes.scotty");
		assertThat(inputStreamOptional.isPresent()).isTrue();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream, true);
        Parser.parse(inputStreamOptional.get(), printStream, new Database(), new Context(), new NamedScriptEngine());
		assertThat(outputStream.toString()).isEqualTo("hello world!\nhello world!\n");
    }
}
