package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.simpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private List<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView rvTweets;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        handler = new Handler();

        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        rvTweets.setAdapter(aTweets);

        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("DEBUG", "scrolling to page " + (page));
                populateTimeline(Tweet.getOldestId(), -1);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        client = TwitterApplication.getRestClient();    //singleton client
        populateTimeline(-1, -1);
    }

    //send api request to get timeline json
    //fill listview by creating the tweet objects from json
    private void populateTimeline(final long oldestId, final long newestId) {
        client.getHomeTimeline(oldestId, newestId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.d("DEBUG", "success! " + response.toString());
                int curSize = aTweets.getItemCount();
                List<Tweet> newItems = Tweet.fromJSONArray(response);
                tweets.addAll(newItems);
                aTweets.notifyItemRangeInserted(curSize, newItems.size());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "failure " + errorResponse.toString());
                //handle rate limit and try again later
                if (statusCode == 88) {
                    Log.d("DEBUG", "rate limit reached, will try again in 30 seconds.");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            populateTimeline(oldestId, newestId);
                        }
                    };
                    handler.postDelayed(runnable, 30000);
                }
            }
        });
    }
}
