package com.david.simpletweets.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.david.simpletweets.R;
import com.david.simpletweets.databinding.ActivityTweetDetailsBinding;
import com.david.simpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class TweetDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvBody;
    private TextView tvName;
    private TextView tvDate;

    private Tweet tweet;
    private int position;
    private ActivityTweetDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_details);

        tweet = getIntent().getParcelableExtra("tweet");
        position = getIntent().getIntExtra("pos", 0);

        setupViews();
    }

    private void setupViews() {
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        //turn on back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivProfileImage = binding.ivProfileImage;
        tvUserName = binding.tvUserName;
        tvBody = binding.tvBody;
        tvName = binding.tvName;
        tvDate = binding.tvDate;

        binding.setTweet(tweet);
        binding.executePendingBindings();

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}
