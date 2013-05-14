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

package scotty.database.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import scotty.database.Context;
import scotty.database.Instance;
import scotty.database.Type;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

import static scotty.database.parser.Attributes.NAME;
import static scotty.database.parser.Attributes.VALUE;
import static scotty.database.parser.Elements.*;

/**
 * Sax parser handler for SCoTTY database documents.
 */
public class TypeHandler extends DefaultHandler {
	private final static Logger LOGGER = Logger.getLogger(TypeHandler.class.getName());
	private final static SAXParserFactory FACTORY = SAXParserFactory.newInstance();
	private final Deque<Context> contexts = new ArrayDeque<>();
	private Type type;

	public static Type parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		SAXParser saxParser = FACTORY.newSAXParser();
		TypeHandler typeHandler = new TypeHandler();
		saxParser.parse(inputStream, typeHandler);
		return typeHandler.type;
	}

	public static Type parse(String file) throws IOException, ParserConfigurationException, SAXException {
		try (InputStream inputStream = new FileInputStream(file)) {
			return parse(inputStream);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
			case CONTEXT:
				Context context = new Context(contexts.peek());
				contexts.push(context);
				break;
			case INSTANCE:
				Instance instance = new Instance(contexts.peek(), attributes.getValue(NAME));
				type.getContained().put(instance.getName(), instance);
				contexts.push(instance);
				break;
			case ATTRIBUTE:
				contexts.peek().put(attributes.getValue(NAME), attributes.getValue(VALUE));
				break;
			case TYPE:
				type = new Type(attributes.getValue(NAME));
				contexts.push(type);
				break;
			default:
				LOGGER.info("Unknown Start element: " + qName);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
			case CONTEXT:
				contexts.pop();
				break;
			case INSTANCE:
				contexts.pop();
				break;
		}
	}
}
