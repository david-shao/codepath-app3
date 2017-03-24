package com.codepath.apps.simpletweets.models;

import com.codepath.apps.simpletweets.utils.SimpleDateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 3/23/2017.
 */

//parse json and store data, encapsulate state logic or display logic
public class Tweet {
    //list out attributes
    private String body;
    private long uid; //unique id for tweet
    private User user;
    private String createdAt;

    private static long oldestId = Long.MAX_VALUE;
    private static long newestId = Long.MIN_VALUE;

    //deserialize json and build Tweet object
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        //extract values from json
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //return tweet object
        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();

        //iterate json array and create tweets
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                //if the id is the same as what we have already, it's a dupe
                if (tweet.uid == newestId || tweet.uid == oldestId) {
                    continue;
                }
                //keep oldest and newest ids around for pagination
                if (tweet.uid > newestId) {
                    newestId = tweet.uid;
                }
                if (tweet.uid < oldestId) {
                    oldestId = tweet.uid;
                }
                tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tweets;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static long getOldestId() {
        return oldestId;
    }

    public static long getNewestId() {
        return newestId;
    }

    public String getRelativeTimeAgo() {
        String relativeDate = SimpleDateUtils.getRelativeTimeAgo(this.getCreatedAt());
        return relativeDate;
    }
}
