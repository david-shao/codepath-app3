package com.codepath.apps.simpletweets.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by David on 3/23/2017.
 */
// taking Tweet objects and turning them into Views displayed in the list
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvName;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
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
        View view = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
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
        User user = tweet.getUser();
        viewHolder.tvName.setText(user.getName());
        viewHolder.tvUserName.setText(user.getScreenName());
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvDate.setText(tweet.getRelativeTimeAgo());

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent); //clear out old image for recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .into(viewHolder.ivProfileImage);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.tweets.size();
    }

}
