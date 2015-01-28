package soshop.social.soshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import java.util.Date;
import java.util.List;

import soshop.social.soshop.FullPostActivity;
import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * Created by Ninniez on 1/18/2015.
 */
public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.ViewHolder> {

    //member variable of data set
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

        mSoShopNumberSet = new int[numberOfPosts];
        mNoShopNumberSet = new int[numberOfPosts];

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mCaptionTextView;
        private TextView mSoShopNumberTextView;
        private TextView mNoShopNumberTextView;
        private Button mSoShopButton;
        private Button mNoShopButton;
        private ImageView mImageViewI;
        private TextView mItemName;
        private TextView mItemPrice;
        private TextView mItemOptionalLocation;
        private TextView mSenderName;
        private TextView mCreatedAt;
        private ImageView mSoShopBar;
        private Button mCommentButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mCaptionTextView = (TextView) itemView.findViewById(R.id.captionText);
            mSoShopNumberTextView = (TextView) itemView.findViewById(R.id.totalSoShop);
            mNoShopNumberTextView = (TextView) itemView.findViewById(R.id.totalNoShop);
            mSoShopButton = (Button) itemView.findViewById(R.id.soShopButton);
            mNoShopButton = (Button) itemView.findViewById(R.id.noShopButton);
            mImageViewI = (ImageView) itemView.findViewById(R.id.itemImage1);
            mItemName = (TextView) itemView.findViewById(R.id.itemName);
            mItemPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            mItemOptionalLocation = (TextView) itemView.findViewById(R.id.itemOptionalLocation);
            mSenderName = (TextView) itemView.findViewById(R.id.senderName);
            mCreatedAt = (TextView) itemView.findViewById(R.id.createdAt);
            mSoShopBar = (ImageView) itemView.findViewById(R.id.soShopBar);
            mCommentButton = (Button) itemView.findViewById(R.id.commentButton);

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
        public TextView getItemName (){
               return mItemName;
        }
        public TextView getItemPrice (){
            return mItemPrice;
        }
        public TextView getItemOptionalLocation (){
            return mItemOptionalLocation;
        }
        public TextView getSenderName(){
            return mSenderName;
        }
        public TextView getCreateAt(){
            return  mCreatedAt;
        }
        public ImageView getSoShopBar(){
            return mSoShopBar;
        }
        public Button getCommentButton(){
            return mCommentButton;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.soshop_feed_items, viewGroup, false);

        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_SOSHOP_VOTE_RELATION);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_NOSHOP_VOTE_RELATION);
        try {
            //get relation for SoShop to render and button status
            mVotedSoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteSoShopRelation.getQuery().find();
            mVotedSoShopByUserIds = new ArrayList<>(mVotedSoShopByUser.size());
            if (mVotedSoShopByUser != null){
                int index = 0;
                for(ParseObject votedSoShop: mVotedSoShopByUser){
                    mVotedSoShopByUserIds.add(index,votedSoShop.getObjectId());
                    index++;
                }
            }
            //get relation for NoShop to render and init button status
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

        addDetailToView(viewHolder, i, soShopPost);

        viewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String soShopPostObjectId = soShopPost.getObjectId();
                Intent fullPostIntent = new Intent(mContext, FullPostActivity.class);
                fullPostIntent.putExtra("soShopPostObjectId", soShopPostObjectId);
                mContext.startActivity(fullPostIntent);

            }
        });


        //START: add image from Parse to imageView using tool from Picasso
        ParseFile file = soShopPost.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
        Picasso.with(mContext).load(fileUri.toString()).into(viewHolder.getImageViewI());
        //END: add image from Parse to imageView using tool from Picasso

        //START: disable vote if it is user's own post
        if (mCurrentUser.getObjectId().equals(soShopPost.get(ParseConstants.KEY_SENDER_IDS)) ){
            viewHolder.getSoShopButton().setEnabled(false);
            viewHolder.getNoShopButton().setEnabled(false);
        } else {
            initSoShopButtonStatus(viewHolder, i, soShopPost);
            initNoShopButtongStatus(viewHolder, i, soShopPost);

        }
        //END: disable vote if it is user's own post



    }

    private void initNoShopButtongStatus(final ViewHolder viewHolder, final int i, final ParseObject soShopPost) {
        //START: SET ACTION and VIEW for NOSHOP BUTTON
        final ParseRelation<ParseUser> isVotedNoShopRelation = soShopPost.getRelation(ParseConstants.KEY_IS_VOTE_NOSHOP_RELATION);

        if (mVotedNoShopByUserIds.contains(soShopPost.getObjectId())){
            viewHolder.getNoShopButton().setText("Voted!");
            viewHolder.getSoShopButton().setEnabled(false);
        }

        viewHolder.getNoShopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.getNoShopButton().setEnabled(false);

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

                                viewHolder.getNoShopButton().setEnabled(true);

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

                                    viewHolder.getNoShopButton().setEnabled(true);

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

    private void initSoShopButtonStatus(final ViewHolder viewHolder, final int i, final ParseObject soShopPost) {
        //START: SET ACTION and VIEW for SOSHOP BUTTON
        final ParseRelation<ParseUser> isVotedSoShopRelation = soShopPost.getRelation(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION);
        if (mVotedSoShopByUserIds.contains(soShopPost.getObjectId())){
            viewHolder.getSoShopButton().setText("Voted!");
            viewHolder.getNoShopButton().setEnabled(false);
        }

        viewHolder.getSoShopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //disable to avtoid duplicate request

                viewHolder.getSoShopButton().setEnabled(false);

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

                                viewHolder.getSoShopButton().setEnabled(true);

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

                                viewHolder.getSoShopButton().setEnabled(true);

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
    }

    private void addDetailToView(ViewHolder viewHolder, int i, ParseObject soShopPost) {

        //caption
        String caption = (String) soShopPost.get(ParseConstants.KEY_CAPTION);
        viewHolder.getCaptionTextView().setText(caption);
        //SoShop number
        mSoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
        viewHolder.getSoShopNumberTextView().setText("("+mSoShopNumberSet[i]+")");
        //NoShop number
        mNoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
        viewHolder.getNoShopNumberTextView().setText("("+mNoShopNumberSet[i]+")");
        //item name
        String itemName = (String) soShopPost.get(ParseConstants.KEY_ITEM_NAME);
        viewHolder.getItemName().setText(itemName);
        //item price with currency
        int itemPriceInt = (int) soShopPost.get(ParseConstants.KEY_ITEM_PRICE);
        String itemPrice = itemPriceInt +"";
        String currency = (String) soShopPost.get(ParseConstants.KEY_CURRENCY);
        viewHolder.getItemPrice().setText(currency + " " + itemPrice);

        //item location description
        String itemLocation = (String) soShopPost.get(ParseConstants.KEY_LOCATION_DESCRIPTION);
        if (itemLocation == null ){
            viewHolder.getItemOptionalLocation().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getItemOptionalLocation().setText(itemLocation);
            viewHolder.getItemOptionalLocation().setVisibility(View.VISIBLE);
        }

        //first name + last name
        String firstName = (String) soShopPost.get(ParseConstants.KEY_SENDER_FIRST_NAME);
        String lastName = (String) soShopPost.get(ParseConstants.KEY_SENDER_LAST_NAME);
        viewHolder.getSenderName().setText(firstName+" "+lastName);

        //time created
        Date createdAt = soShopPost.getCreatedAt(); //object from parse
        //best way to get time, for now. 17/12/14
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
        viewHolder.getCreateAt().setText(convertedDate);
    }

    @Override
    public int getItemCount() {
        return numberOfPosts;
    }


}
