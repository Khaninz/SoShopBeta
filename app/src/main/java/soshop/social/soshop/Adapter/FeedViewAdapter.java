package soshop.social.soshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * Created by Ninniez on 1/18/2015.
 */
public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.ViewHolder> {

    //member variable of data set
    String[] mSenderNameSet;
    String[] mCreatedAtSet;
    String[] mCaptionSet;
    String[] mItemPriceSet;
    String[] mItemNameSet;
    int numberOfPosts;

    public FeedViewAdapter (List<ParseObject> soShopPosts){

        numberOfPosts = soShopPosts.size();

        mCaptionSet = new String[numberOfPosts];

        int i = 0;
        for (ParseObject soShopPost: soShopPosts){
            mCaptionSet[i] = soShopPost.getString(ParseConstants.KEY_SENDER_CAPTION);
            i++;
        }

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private  TextView mCaptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mCaptionTextView = (TextView) itemView.findViewById(R.id.captionText);

        }

        public TextView getCaptionTextView(){
            return mCaptionTextView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.soshop_feed_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.getCaptionTextView().setText(mCaptionSet[i]);
    }

    @Override
    public int getItemCount() {
        return numberOfPosts;
    }


}
