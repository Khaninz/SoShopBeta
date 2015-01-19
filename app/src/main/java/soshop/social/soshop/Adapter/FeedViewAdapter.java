package soshop.social.soshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
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
    int[] mSoShopNumberSet;
    List<ParseObject> mSoShopPosts;

    //member for Parse
    ParseUser mCurrentUser;


    //MAIN CONSTRUCTOR
    public FeedViewAdapter (List<ParseObject> soShopPosts){

        mSoShopPosts = soShopPosts;
        numberOfPosts = soShopPosts.size();


        mCaptionSet = new String[numberOfPosts];
        mSoShopNumberSet = new int[numberOfPosts];

        int i = 0;
        for (ParseObject soShopPost: soShopPosts){
            mCaptionSet[i] = soShopPost.getString(ParseConstants.KEY_SENDER_CAPTION);
            mSoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
            i++;
        }

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mCaptionTextView;
        private TextView mSoShopNumberTextView;
        private Button mSoShopButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mCaptionTextView = (TextView) itemView.findViewById(R.id.captionText);
            mSoShopNumberTextView = (TextView) itemView.findViewById(R.id.totalSoShop);
            mSoShopButton = (Button) itemView.findViewById(R.id.soShopButton);
        }

        public TextView getCaptionTextView(){
            return mCaptionTextView;
        }
        public TextView getSoShopNumberTextView(){
            return mSoShopNumberTextView;
        }
        public Button getSoShopButton(){
            return mSoShopButton;

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.soshop_feed_item, viewGroup, false);

        mCurrentUser = ParseUser.getCurrentUser();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.getCaptionTextView().setText(mCaptionSet[i]);
        viewHolder.getSoShopNumberTextView().setText("("+mSoShopNumberSet[i]+")");


        //START: initialize SoShop Button, check voted and setOnClickListener.
        final ParseObject soShopPost = mSoShopPosts.get(i);
        final ParseRelation<ParseUser> isVotedSoShopRelation = soShopPost.getRelation(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION);

        try {
            ArrayList<ParseUser> usersVoted = (ArrayList<ParseUser>) isVotedSoShopRelation.getQuery().find();
            ArrayList<String> usersVotedIds = new ArrayList<>(usersVoted.size()); //create ArrayList String to be used in queries below
            int subIndex = 0;
            for (ParseUser userVoted : usersVoted) { //get each friend id to the list
                usersVotedIds.add(subIndex, userVoted.getObjectId());
                subIndex++;
            }

            final Boolean isContained = usersVotedIds.contains(mCurrentUser.getObjectId()); //check if user is contained in the relation of the post

            if(isContained){
                //if user already voted, change button color to blue
                viewHolder.getSoShopButton().setText("Voted!");
            } else {
                viewHolder.getSoShopButton().setText("SoShop");
            }

            viewHolder.getSoShopButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isContained){
                        //if the user is already voted. remove the vote
                        mSoShopNumberSet[i]--;
                        int newTotalSoShop = mSoShopNumberSet[i];

                        isVotedSoShopRelation.remove(mCurrentUser); //remove user from relation
                        soShopPost.put(ParseConstants.KEY_TOTAL_SOSHOP, newTotalSoShop); //add updated vote
                        soShopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if ( e ==null) {
                                    viewHolder.getSoShopNumberTextView().setText("(" + mSoShopNumberSet[i] + ")");
                                } else {

                                }
                            }
                        });

                    } else {
                        // not yet vote add the vote; increase vote and add user to relation in post
                        mSoShopNumberSet[i]++;
                        int newTotalSoShop = mSoShopNumberSet[i];

                        isVotedSoShopRelation.add(mCurrentUser);//add user to relation
                        soShopPost.put(ParseConstants.KEY_TOTAL_SOSHOP, newTotalSoShop);//add updated vote
                        soShopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if ( e ==null) {
                                    viewHolder.getSoShopNumberTextView().setText("(" + mSoShopNumberSet[i] + ")");
                                } else {

                                }
                            }
                        });
                    }

                }
            });




        } catch (ParseException e) {
            e.printStackTrace();
        }
        //END: initialize SoShop Button, check voted and setOnClickListener.






    }

    @Override
    public int getItemCount() {
        return numberOfPosts;
    }


}
