package me.zsr.feeder.data;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 8/29/15
 */
public class FeedReadException extends Exception {

    public FeedReadException(int status, String message) {
        super(message);
    }
}
