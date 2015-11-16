package me.zsr.feeder.util;

import java.io.InputStream;

/**
 * @description:
 * @author: Match
 * @date: 15-7-17
 */
public class IOUtil {

    /**
     * http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
     */
    static private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
