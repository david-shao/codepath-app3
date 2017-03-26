package com.david.simpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.david.simpletweets.R;
import com.david.simpletweets.activities.TimelineActivity;
import com.david.simpletweets.activities.TweetDetailsActivity;
import com.david.simpletweets.databinding.ItemTweetBinding;
import com.david.simpletweets.models.Tweet;

import java.util.List;

/**
 * Created by David on 3/23/2017.
 */
// taking Tweet objects and turning them into Views displayed in the list
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemTweetBinding binding;
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvName;
        public TextView tvDate;

        public ViewHolder(ItemTweetBinding itemView) {
            super(itemView.getRoot());

            this.binding = itemView;

            ivProfileImage = binding.ivProfileImage;
            tvName = binding.tvName;
            tvUserName = binding.tvUserName;
            tvBody = binding.tvBody;
            tvDate = binding.tvDate;

            itemView.getRoot().setOnClickListener(this);
        }

        public void bindTweet(Tweet tweet) {
            binding.setTweet(tweet);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Tweet tweet = tweets.get(position);
                Intent i = new Intent(context, TweetDetailsActivity.class);
                i.putExtra("tweet", tweet);
                i.putExtra("pos", position);
                i.putExtra("currentUser", ((TimelineActivity) context).getCurrentUser());
                ((AppCompatActivity) context).startActivityForResult(i, TimelineActivity.REQUEST_CODE_DETAILS);
            }
        }

    }

    // Store a member variable for the tweet
    private List<Tweet> tweets;
    // Store the context for easy access
    private Context context;

    public TweetsArrayAdapter(@NonNull Context context, @NonNull List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return this.context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemTweetBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = this.tweets.get(position);
        configureViewHolder(viewHolder, tweet);
    }

    private void configureViewHolder(TweetsArrayAdapter.ViewHolder viewHolder, Tweet tweet) {
        // populate data into subviews
        viewHolder.bindTweet(tweet);

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent); //clear out old image for recycled view
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.ivProfileImage);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.tweets.size();
    }

}
