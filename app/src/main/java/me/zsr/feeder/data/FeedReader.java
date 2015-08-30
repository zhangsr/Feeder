package me.zsr.feeder.data;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import me.zsr.feeder.dao.FeedSource;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 8/28/15
 */
public class FeedReader implements Closeable {
    private HttpClient mHttpClient = new DefaultHttpClient();
    private FeedParser mParser = new FeedParser();

    public FeedReader() {

    }

    public FeedSource load(String url) throws FeedReadException {
        HttpGet httpGet = new HttpGet(url);

        InputStream is = null;
        try {
            HttpResponse response = mHttpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                throw new FeedReadException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            FeedSource feedSource = mParser.parse(is);
            feedSource.setUrl(url);

            return feedSource;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            // TODO: 8/29/15 define status code
            throw new FeedReadException(0, e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    // TODO: 8/30/15 call me
    @Override
    public void close() throws IOException {
        mHttpClient.getConnectionManager().shutdown();
    }
}
