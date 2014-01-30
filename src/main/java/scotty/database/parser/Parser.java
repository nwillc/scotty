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

import com.google.common.base.Optional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import scotty.database.Context;
import scotty.database.Instance;
import scotty.database.Type;
import scotty.database.Value;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scotty.ScottyUtilities.getResourceAsStream;
import static scotty.database.parser.Attributes.NAME;
import static scotty.database.parser.Attributes.VALUE;
import static scotty.database.parser.Elements.*;

/**
 * Sax parser for SCoTTY database documents.
 */
public class Parser extends DefaultHandler {
    private final static Logger LOGGER = Logger.getLogger(Parser.class.getName());
    private final static SAXParserFactory FACTORY = SAXParserFactory.newInstance();
    private final Deque<Context> contexts = new ArrayDeque<>();
    private Type type;
    private Value value;
    private StringBuilder stringBuilder = null;

    public static Optional<Type> parse(InputStream inputStream) {
		try {
        	SAXParser saxParser = FACTORY.newSAXParser();
        	Parser typeHandler = new Parser();
        	saxParser.parse(inputStream, typeHandler);
			return Optional.of(typeHandler.type);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed parsing inputStream", e);
			return Optional.absent();
		}
    }

    public static Optional<Type> parse(String file) {
		Optional<InputStream> inputStreamOptional = getResourceAsStream(file);
		if (inputStreamOptional.isPresent()) {
			return parse(inputStreamOptional.get());
		}
		return Optional.absent();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final String name;
        switch (qName) {
            case CONTEXT:
                Context context = new Context(contexts.peek());
                contexts.push(context);
                break;
            case INSTANCE:
                name = attributes.getValue(NAME);
                if (name != null && type.getContained().containsKey(name)) {
                    LOGGER.warning("Replacing type " + type.getName() + " instance " + name);
                }
                Instance instance = new Instance(contexts.peek(), name);
                type.getContained().put(instance.getName(), instance);
                contexts.push(instance);
                break;
            case ATTRIBUTE:
                name = attributes.getValue(NAME);
                if (name == null) {
                    throw new SAXException("Attributes require a name.");
                }
                value = new Value(attributes.getValue(VALUE));
                contexts.peek().put(name, value);
                break;
            case TYPE:
                name = attributes.getValue(NAME);
                if (name == null) {
                    throw new SAXException("Types require a name");
                }
                type = new Type(name);
                contexts.push(type);
                break;
            case VALUE:
                stringBuilder = new StringBuilder();
                break;
            default:
                LOGGER.info("Unknown Start element: " + qName);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (stringBuilder != null) {
            stringBuilder.append(ch, start, length);
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
            case VALUE:
                value.add(stringBuilder.toString());
                stringBuilder = null;
                break;
        }
    }
}
