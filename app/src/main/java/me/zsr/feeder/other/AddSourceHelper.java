package me.zsr.feeder.other;

import com.avos.avoscloud.AVObject;

import me.zsr.feeder.data.FeedlyResult;

/**
 * @description:
 * @author: Match
 * @date: 11/30/15
 */
public class AddSourceHelper {
    public static void upload(FeedlyResult result) {
        AVObject feedSourceObj = new AVObject("Subscription");
        feedSourceObj.put("deliciousTags", result.deliciousTags);
        feedSourceObj.put("feedId", result.feedId);
        feedSourceObj.put("language", result.language);
        feedSourceObj.put("title", result.title);
        feedSourceObj.put("velocity", result.velocity);
        feedSourceObj.put("subscribers", result.subscribers);
        feedSourceObj.put("lastUpdated", result.lastUpdated);
        feedSourceObj.put("website", result.website);
        feedSourceObj.put("score", result.score);
        feedSourceObj.put("coverage", result.coverage);
        feedSourceObj.put("coverageScore", result.coverageScore);
        feedSourceObj.put("estimatedEngagement", result.estimatedEngagement);
        feedSourceObj.put("hint", result.hint);
        feedSourceObj.put("scheme", result.scheme);
        feedSourceObj.put("description", result.description);
        feedSourceObj.put("contentType", result.contentType);
        feedSourceObj.put("coverUrl", result.coverUrl);
        feedSourceObj.put("iconUrl", result.iconUrl);
        feedSourceObj.put("partial", result.partial);
        feedSourceObj.put("twitterScreenName", result.twitterScreenName);
        feedSourceObj.put("visualUrl", result.visualUrl);
        feedSourceObj.put("coverColor", result.coverColor);
        feedSourceObj.put("twitterFollowers", result.twitterFollowers);
        feedSourceObj.put("facebookUsername", result.facebookUsername);
        feedSourceObj.put("facebookLikes", result.facebookLikes);
        feedSourceObj.put("art", result.art);
        feedSourceObj.saveInBackground();
    }
}
