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

package scotty.database;

import org.xml.sax.SAXException;
import scotty.database.parser.TypeHandler;
import scotty.database.parser.Utilities;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A SCoTTy database, basically a context with a contained map of Types.
 */
public class Database extends Context {

	public static Database parse(InputStream... inputStreams) throws IOException, SAXException, ParserConfigurationException {
		Database database = new Database();

		if (inputStreams != null) {
			for (InputStream inputStream : inputStreams) {
				Type type = TypeHandler.parse(inputStream);
				database.getContained().put(type.getName(), type);
			}
		}
		return database;
	}

	public static Database parse(String... files) throws ParserConfigurationException, SAXException, IOException {
		if (files == null || files.length == 0) {
			return null;
		}

		Database database = new Database();
		for (String file : files) {
			Type type = TypeHandler.parse(file);
			database.getContained().put(type.getName(), type);
		}
		return database;
	}

	public String find(String name) {
		return Utilities.find(this, name);
	}

	public List<Context> query(Context criteria) {
		return Utilities.query(this, criteria);
	}

}
