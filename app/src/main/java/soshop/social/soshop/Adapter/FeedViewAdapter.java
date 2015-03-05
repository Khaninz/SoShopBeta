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
import android.widget.Toast;

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

    private static final  int MAX_FEED_SHOW = 15;

    //member variable of data set
    int numberOfPosts;
    List<ParseObject> mShopPosts;
    Context mContext;

    //member for Parse
    ParseUser mCurrentUser;
    ParseRelation<ParseObject> mCurrentUserVoteSoShopRelation;
    ArrayList<ParseObject> mPostVotedSoShopByUser;
    ParseRelation<ParseObject> mCurrentUserVoteNoShopRelation;
    ArrayList<ParseObject> mPostVotedNoShopByUser;

    //MAIN CONSTRUCTOR
    public FeedViewAdapter (List<ParseObject> soShopPosts, Context context, ArrayList<ParseObject> postVotedSoShopByUser, ArrayList<ParseObject> postVotedNoShopByUser){

        mContext = context;
        mShopPosts = soShopPosts;
        numberOfPosts = mShopPosts.size();

        mPostVotedSoShopByUser = postVotedSoShopByUser;
        mPostVotedNoShopByUser = postVotedNoShopByUser;

        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_SOSHOP_VOTE);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_NOSHOP_VOTE);

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

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        final ParseObject shopPost = mShopPosts.get(i);

        addDetailToView(viewHolder, i, shopPost);

        //START: comment button for each post
        viewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempSoShopButtonText = (String) viewHolder.getSoShopButton().getText();
                String tempNoShopButtonText = (String) viewHolder.getNoShopButton().getText();
                Boolean tempSoShopButtonStatus;
                Boolean tempNoShopButtonStatus;
                if (shopPost.get(ParseConstants.KEY_SENDER_IDS).equals(ParseUser.getCurrentUser().getObjectId())) {
                    tempNoShopButtonStatus = false;
                    tempSoShopButtonStatus = false;
                }else{
                    if (mPostVotedSoShopByUser.contains(shopPost)) {
                        //if soshop is voted, disable noShopButton
                        tempNoShopButtonStatus = false;
                    } else {
                        tempNoShopButtonStatus = true;
                    }
                    if (mPostVotedNoShopByUser.contains(shopPost)) {
                        tempSoShopButtonStatus = false;
                    } else {
                        tempSoShopButtonStatus = true;
                    }
                }

                String soShopPostObjectId = shopPost.getObjectId();
                Intent fullPostIntent = new Intent(mContext, FullPostActivity.class);
                fullPostIntent.putExtra("soShopPostObjectId", soShopPostObjectId);
                fullPostIntent.putExtra("SoShopButtonStatus", tempSoShopButtonStatus);
                fullPostIntent.putExtra("SoShopButtonText",tempSoShopButtonText);
                fullPostIntent.putExtra("NoShopButtonStatus", tempNoShopButtonStatus);
                fullPostIntent.putExtra("NoShopButtonText",tempNoShopButtonText);
                mContext.startActivity(fullPostIntent);
            }
        });
        //END: comment button for each post

        //START: add image from Parse to imageView using tool from Picasso
        ParseFile file = shopPost.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
        Picasso.with(mContext).load(fileUri.toString()).into(viewHolder.getImageViewI());
        //END: add image from Parse to imageView using tool from Picasso

        //START: disable vote if it is user's own post, and get status of voted button for each post relate to user
        if (shopPost.get(ParseConstants.KEY_SENDER_IDS).equals(ParseUser.getCurrentUser().getObjectId())) {

            viewHolder.getSoShopButton().setEnabled(false);
            viewHolder.getSoShopButton().setText("SoShop");

            viewHolder.getNoShopButton().setEnabled(false);
            viewHolder.getNoShopButton().setText("NoShop");
        } else {

            viewHolder.getSoShopButton().setEnabled(true);
            viewHolder.getNoShopButton().setEnabled(true);
            initSoShopButtonStatus(viewHolder, shopPost);
            initNoShopButtonStatus(viewHolder, shopPost);

        }
        //END: disable vote if it is user's own post, and get status of voted button for each post relate to user



    }

    private void initNoShopButtonStatus(final ViewHolder viewHolder, final ParseObject shopPost) {
        //START: SET ACTION and VIEW for NOSHOP BUTTON

        if (mPostVotedNoShopByUser.contains(shopPost)){
            viewHolder.getNoShopButton().setText("Voted!");
            viewHolder.getSoShopButton().setEnabled(false);
        } else{
            viewHolder.getNoShopButton().setText("NoShop");

        }

        viewHolder.getNoShopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseRelation<ParseUser> isVotedNoShopRelation = shopPost.getRelation(ParseConstants.KEY_IS_VOTE_NOSHOP_RELATION);

                viewHolder.getNoShopButton().setEnabled(false);
                viewHolder.getSoShopButton().setEnabled(false);

                try {
                    //Check if user is already voted.
                    ArrayList<ParseUser> votedUsers = (ArrayList<ParseUser>) isVotedNoShopRelation.getQuery().find();

                    Boolean isContained = votedUsers.contains(mCurrentUser);

                    if (isContained){
                        //if the user is already voted. remove the vote

                        int tempNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                        tempNoShopNumber--;
                        viewHolder.getNoShopNumberTextView().setText("(" + tempNoShopNumber + ")");
                        viewHolder.getNoShopButton().setText("NoShop");

                        shopPost.increment(ParseConstants.KEY_TOTAL_NOSHOP, -1);
                        isVotedNoShopRelation.remove(mCurrentUser);

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {

                                    mCurrentUserVoteNoShopRelation.remove(shopPost);
                                    mPostVotedNoShopByUser.remove(shopPost.getObjectId()); //update the list to be used for rendering the button
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                                            viewHolder.getNoShopNumberTextView().setText("(" + actualNoShopNumber + ")");
                                            viewHolder.getNoShopButton().setEnabled(true);
                                            viewHolder.getNoShopButton().setText("NoShop");
                                            viewHolder.getSoShopButton().setEnabled(true);
                                        }
                                    });

                                } else {
                                    //return button to before press.
                                }
                            }
                        });

                    } else {
                        // not yet vote add the vote; increase vote and add user to relation in post

                        int tempNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                        tempNoShopNumber++;
                        viewHolder.getNoShopNumberTextView().setText("(" + tempNoShopNumber + ")");
                        shopPost.increment(ParseConstants.KEY_TOTAL_NOSHOP, +1);
                        isVotedNoShopRelation.add(mCurrentUser);
                        viewHolder.getNoShopButton().setText("Voted!");

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {
                                    mCurrentUserVoteNoShopRelation.add(shopPost);
                                    mPostVotedNoShopByUser.add(shopPost); //update the list to be used for rendering the button
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                                            viewHolder.getNoShopNumberTextView().setText("(" + actualNoShopNumber + ")");
                                            viewHolder.getNoShopButton().setEnabled(true);
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
                    Toast.makeText(mContext, "Error Getting List of Voted user for " + shopPost.get(ParseConstants.KEY_ITEM_NAME),Toast.LENGTH_SHORT).show();
                }

            }
        });
        //END: SET ACTION and VIEW for NOSHOP BUTTON
    }

    private void initSoShopButtonStatus(final ViewHolder viewHolder, final ParseObject shopPost) {
        //START: SET ACTION and VIEW for SOSHOP BUTTON


        if (mPostVotedSoShopByUser.contains(shopPost)){
            viewHolder.getSoShopButton().setText("Voted!");
            viewHolder.getNoShopButton().setEnabled(false);
        } else{
            viewHolder.getSoShopButton().setText("SoShop");

        }

        viewHolder.getSoShopButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ParseRelation<ParseUser> isVotedSoShopRelation = shopPost.getRelation(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION);

                //disable to avoid rapid duplicate request
                viewHolder.getSoShopButton().setEnabled(false);
                viewHolder.getNoShopButton().setEnabled(false);

                try {
                    //Check if user is already voted.
                    ArrayList<ParseUser> votedUsers = (ArrayList<ParseUser>) isVotedSoShopRelation.getQuery().find();

                    Boolean isContained = votedUsers.contains(mCurrentUser);

                    if (isContained){
                        //if the user is already voted. remove the vote

                        int tempSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                        tempSoShopNumber--;
                        viewHolder.getSoShopNumberTextView().setText("(" + tempSoShopNumber + ")");
                        viewHolder.getSoShopButton().setText("SoShop");
                        shopPost.increment(ParseConstants.KEY_TOTAL_SOSHOP, -1);
                        isVotedSoShopRelation.remove(mCurrentUser);

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {

                                    mCurrentUserVoteSoShopRelation.remove(shopPost);
                                    mPostVotedSoShopByUser.remove(shopPost.getObjectId()); //update the list to be used for rendering the button
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                                            viewHolder.getSoShopNumberTextView().setText("(" + actualSoShopNumber + ")");
                                            viewHolder.getSoShopButton().setEnabled(true);
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

                        int tempSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                        tempSoShopNumber++;
                        viewHolder.getSoShopNumberTextView().setText("(" + tempSoShopNumber + ")");
                        shopPost.increment(ParseConstants.KEY_TOTAL_SOSHOP, +1);
                        isVotedSoShopRelation.add(mCurrentUser);
                        viewHolder.getSoShopButton().setText("Voted!");

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {



                                if (e == null) {
                                    mCurrentUserVoteSoShopRelation.add(shopPost);
                                    mPostVotedSoShopByUser.add(shopPost); //update the list to be used for rendering the button
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                                            viewHolder.getSoShopNumberTextView().setText("(" + actualSoShopNumber + ")");
                                            viewHolder.getSoShopButton().setEnabled(true);
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
                    Toast.makeText(mContext, "Error Getting List of Voted user for " + shopPost.get(ParseConstants.KEY_ITEM_NAME),Toast.LENGTH_SHORT).show();
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
        //mSoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
        int soShopNumber = soShopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
        viewHolder.getSoShopNumberTextView().setText("("+soShopNumber+")");
        //NoShop number
        //mNoShopNumberSet[i] = soShopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
        int noShopNumber = soShopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
        viewHolder.getNoShopNumberTextView().setText("("+noShopNumber+")");
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

        if (numberOfPosts <= MAX_FEED_SHOW){
            return numberOfPosts;
        } else {
            return MAX_FEED_SHOW;
        }

    }


}
