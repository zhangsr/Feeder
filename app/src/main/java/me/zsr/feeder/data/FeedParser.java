package me.zsr.feeder.data;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.zsr.feeder.dao.FeedSource;

/**
 * @description:
 * @author: Match
 * @date: 8/29/15
 */
public class FeedParser {

    public FeedSource parse(InputStream is) throws FeedReadException, SAXException,
            ParserConfigurationException, IOException {
        // Since SAXParserFactory implementations are not guaranteed to be
        // thread-safe, a new local object is instantiated.
        SAXParserFactory factory = SAXParserFactory.newInstance();

        // Support Android 1.6 (see Issue 1)
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        SAXParser parser = factory.newSAXParser();

        return parse(parser, is);
    }

    private FeedSource parse(SAXParser parser, InputStream is) throws FeedReadException, SAXException, IOException {
        if (parser == null) {
            throw new FeedReadException(0, "RSS parser must not be null.");
        } else if (is == null) {
            throw new FeedReadException(0, "RSS feed must not be null.");
        }

        // SAX automatically detects the correct character encoding from the stream
        // See also http://www.w3.org/TR/REC-xml/#sec-guessing
        InputSource source = new InputSource(is);
        XMLReader xmlreader = parser.getXMLReader();
        FeedHandler handler = new FeedHandler();

        xmlreader.setContentHandler(handler);
        xmlreader.parse(source);

        return handler.getFeedSource();
    }
}
