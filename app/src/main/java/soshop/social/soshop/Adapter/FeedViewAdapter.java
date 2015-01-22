package soshop.social.soshop.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

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
    int[] mNoShopNumberSet;
    List<ParseObject> mSoShopPosts;
    Context mContext;


    //member for Parse
    ParseUser mCurrentUser;
    ParseRelation<ParseObject> mCurrentUserVoteSoShopRelation;
    ArrayList<ParseObject> mVotedSoShopByUser;
    ArrayList<String> mVotedSoShopByUserIds;
    ParseRelation<ParseObject> mCurrentUserVoteNoShopRelation;
    ArrayList<ParseObject> mVotedNoShopByUser;
    ArrayList<String> mVotedNoShopByUserIds;



    //MAIN CONSTRUCTOR
    public FeedViewAdapter (List<ParseObject> soShopPosts, Context context){

        mContext = context;
        mSoShopPosts = soShopPosts;
        numberOfPosts = soShopPosts.size();


        mCaptionSet = new String[numberOfPosts];
        mSoShopNumberSet = new int[numberOfPosts];
        mNoShopNumberSet = new int[numberOfPosts];

//        int i = 0;
//        for (ParseObject soShopPost: soShopPosts){
//            mCaptionSet[i] = soShopPost.getString(ParseConstants.KEY_SENDER_CAPTION);
//            mSoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
//            mNoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
//
//        }

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mCaptionTextView;
        private TextView mSoShopNumberTextView;
        private TextView mNoShopNumberTextView;
        private Button mSoShopButton;
        private Button mNoShopButton;
        private ImageView mImageViewI;

        public ViewHolder(View itemView) {
            super(itemView);

            mCaptionTextView = (TextView) itemView.findViewById(R.id.captionText);
            mSoShopNumberTextView = (TextView) itemView.findViewById(R.id.totalSoShop);
            mNoShopNumberTextView = (TextView) itemView.findViewById(R.id.totalNoShop);
            mSoShopButton = (Button) itemView.findViewById(R.id.soShopButton);
            mNoShopButton = (Button) itemView.findViewById(R.id.noShopButton);
            mImageViewI = (ImageView) itemView.findViewById(R.id.itemImage1);
        }

        public TextView getCaptionTextView(){
            return mCaptionTextView;
        }
        public TextView getSoShopNumberTextView(){
            return mSoShopNumberTextView;
        }
        public TextView getNoShopNumberTextView(){
            return mNoShopNumberTextView;
        }
        public Button getSoShopButton(){
            return mSoShopButton;
        }
        public Button getNoShopButton(){
            return mNoShopButton;
        }
        public ImageView getImageViewI() {
            return mImageViewI;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.soshop_feed_item, viewGroup, false);

        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_SOSHOP_VOTE_RELATION);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_NOSHOP_VOTE_RELATION);
        try {
            //get relation for SoShop
            mVotedSoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteSoShopRelation.getQuery().find();
            mVotedSoShopByUserIds = new ArrayList<>(mVotedSoShopByUser.size());
            if (mVotedSoShopByUser != null){
                int index = 0;
                for(ParseObject votedSoShop: mVotedSoShopByUser){
                    mVotedSoShopByUserIds.add(index,votedSoShop.getObjectId());
                    index++;
                }
            }
            //get relation for NoShop
            mVotedNoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteNoShopRelation.getQuery().find();
            mVotedNoShopByUserIds = new ArrayList<>(mVotedNoShopByUser.size());
            if (mVotedNoShopByUser != null){
                int index = 0;
                for(ParseObject votedNoShop: mVotedNoShopByUser){
                    mVotedNoShopByUserIds.add(index,votedNoShop.getObjectId());
                    index++;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        final ParseObject soShopPost = mSoShopPosts.get(i);


//        viewHolder.getCaptionTextView().setText(mCaptionSet[i]);
//        viewHolder.getSoShopNumberTextView().setText("("+mSoShopNumberSet[i]+")");
//        viewHolder.getNoShopNumberTextView().setText("("+mNoShopNumberSet[i]+")");

        String caption = (String) soShopPost.get(ParseConstants.KEY_CAPTION);
        viewHolder.getCaptionTextView().setText(caption);

        mSoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
        viewHolder.getSoShopNumberTextView().setText("("+mSoShopNumberSet[i]+")");

        mNoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
        viewHolder.getNoShopNumberTextView().setText("("+mNoShopNumberSet[i]+")");

        //START: add image from Parse to imageView using tool from Picasso
        ParseFile file = soShopPost.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
        Picasso.with(mContext).load(fileUri.toString()).into(viewHolder.getImageViewI());
        //END: add image from Parse to imageView using tool from Picasso

        //START: SET ACTION and VIEW for SOSHOP BUTTON
        final ParseRelation<ParseUser> isVotedSoShopRelation = soShopPost.getRelation(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION);
        if (mVotedSoShopByUserIds.contains(soShopPost.getObjectId())){
            viewHolder.getSoShopButton().setText("Voted!");
            viewHolder.getNoShopButton().setEnabled(false);
        }

        viewHolder.getSoShopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Check if user is already voted.
                    ArrayList<ParseUser> votedUsers = (ArrayList<ParseUser>) isVotedSoShopRelation.getQuery().find();
                    ArrayList<String> votedUsersIds = new ArrayList<>(votedUsers.size()); //create ArrayList String to be used in queries below
                    int subIndex = 0;
                    for (ParseUser votedUser : votedUsers) { //get each friend id to the list
                        votedUsersIds.add(subIndex, votedUser.getObjectId());
                        subIndex++;
                    }

                    Boolean isContained = votedUsersIds.contains(mCurrentUser.getObjectId());

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

                                    mCurrentUserVoteSoShopRelation.remove(soShopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            viewHolder.getSoShopNumberTextView().setText("(" + mSoShopNumberSet[i] + ")");
                                            viewHolder.getSoShopButton().setText("SoShop");
                                            viewHolder.getNoShopButton().setEnabled(true);
                                        }
                                    });

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
                                    mCurrentUserVoteSoShopRelation.add(soShopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            viewHolder.getSoShopNumberTextView().setText("(" + mSoShopNumberSet[i] + ")");
                                            viewHolder.getSoShopButton().setText("Voted!");
                                            viewHolder.getNoShopButton().setEnabled(false);
                                        }
                                    });

                                } else {

                                }
                            }
                        });
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        //END: SET ACTION and VIEW for SOSHOP BUTTON

        //START: SET ACTION and VIEW for NOSHOP BUTTON
        final ParseRelation<ParseUser> isVotedNoShopRelation = soShopPost.getRelation(ParseConstants.KEY_IS_VOTE_NOSHOP_RELATION);

        if (mVotedNoShopByUserIds.contains(soShopPost.getObjectId())){
            viewHolder.getNoShopButton().setText("Voted!");
            viewHolder.getSoShopButton().setEnabled(false);
        }

        viewHolder.getNoShopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Check if user is already voted.
                    ArrayList<ParseUser> votedUsers = (ArrayList<ParseUser>) isVotedNoShopRelation.getQuery().find();
                    ArrayList<String> votedUsersIds = new ArrayList<>(votedUsers.size()); //create ArrayList String to be used in queries below
                    int subIndex = 0;
                    for (ParseUser votedUser : votedUsers) { //get each friend id to the list
                        votedUsersIds.add(subIndex, votedUser.getObjectId());
                        subIndex++;
                    }

                    Boolean isContained = votedUsersIds.contains(mCurrentUser.getObjectId());

                    if (isContained){
                        //if the user is already voted. remove the vote
                        mNoShopNumberSet[i]--;
                        int newTotalNoShop = mNoShopNumberSet[i];

                        isVotedNoShopRelation.remove(mCurrentUser); //remove user from relation
                        soShopPost.put(ParseConstants.KEY_TOTAL_NOSHOP, newTotalNoShop); //add updated vote
                        soShopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if ( e ==null) {

                                    mCurrentUserVoteNoShopRelation.remove(soShopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            viewHolder.getNoShopNumberTextView().setText("(" + mNoShopNumberSet[i] + ")");
                                            viewHolder.getNoShopButton().setText("NoShop");
                                            viewHolder.getSoShopButton().setEnabled(true);
                                        }
                                    });

                                } else {

                                }
                            }
                        });

                    } else {
                        // not yet vote add the vote; increase vote and add user to relation in post
                        mNoShopNumberSet[i]++;
                        int newTotalNoShop = mNoShopNumberSet[i];

                        isVotedNoShopRelation.add(mCurrentUser); //remove user from relation
                        soShopPost.put(ParseConstants.KEY_TOTAL_NOSHOP, newTotalNoShop); //add updated vote
                        soShopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if ( e ==null) {

                                    mCurrentUserVoteNoShopRelation.add(soShopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            viewHolder.getNoShopNumberTextView().setText("(" + mNoShopNumberSet[i] + ")");
                                            viewHolder.getNoShopButton().setText("Voted!");
                                            viewHolder.getSoShopButton().setEnabled(false);
                                        }
                                    });

                                } else {

                                }
                            }
                        });
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        //END: SET ACTION and VIEW for NOSHOP BUTTON
    }

    @Override
    public int getItemCount() {
        return numberOfPosts;
    }


}
