package com.david.simpletweets.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.david.simpletweets.R;
import com.david.simpletweets.TwitterApplication;
import com.david.simpletweets.TwitterClient;
import com.david.simpletweets.adapters.TweetsArrayAdapter;
import com.david.simpletweets.databinding.ActivityTimelineBinding;
import com.david.simpletweets.fragments.ComposeTweetFragment;
import com.david.simpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.david.simpletweets.models.Tweet;
import com.david.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.ComposeTweetListener {

    private Toolbar toolbar;
    private User currentUser;
    private TwitterClient client;
    private List<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView rvTweets;
    private Handler handler;

    private ActivityTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        currentUser = getIntent().getParcelableExtra("user");

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
                populateTimeline(Tweet.getOldestId() - 1, -1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.actCompose:
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment frag = ComposeTweetFragment.newInstance(currentUser);
//                frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                frag.show(fm, "fragment_compose");
                break;
        }

        return true;
    }

    @Override
    public void onTweet(Tweet tweet) {
        tweets.add(0, tweet);
        aTweets.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }
}
