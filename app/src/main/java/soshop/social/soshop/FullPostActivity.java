package soshop.social.soshop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import soshop.social.soshop.Adapter.CommentAdapter;
import soshop.social.soshop.Utils.ParseConstants;


public class FullPostActivity extends ActionBarActivity {

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

    private Button mSendCommentButton;
    private EditText mCommentInputEditText;
    private ArrayList<String> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;

    private ParseObject mSelectedPost;
    private ParseUser mCurrentUser;
    private ParseRelation<ParseObject> mCurrentUserVoteSoShopRelation;
    private ParseRelation<ParseObject> mCurrentUserVoteNoShopRelation;
    private ArrayList<ParseObject> mPostVotedSoShopByUser;
    private ArrayList<String> mPostIdsVotedSoShopByUser;
    private ArrayList<ParseObject> mPostVotedNoShopByUser;
    private ArrayList<String> mPostIdsVotedNoShopByUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_post);

        mCaptionTextView = (TextView) findViewById(R.id.captionText);
        mSoShopNumberTextView = (TextView) findViewById(R.id.totalSoShop);
        mNoShopNumberTextView = (TextView) findViewById(R.id.totalNoShop);
        mSoShopButton = (Button) findViewById(R.id.soShopButton);
        mNoShopButton = (Button) findViewById(R.id.noShopButton);
        mImageViewI = (ImageView) findViewById(R.id.itemImage1);
        mItemName = (TextView) findViewById(R.id.itemName);
        mItemPrice = (TextView) findViewById(R.id.itemPrice);
        mItemOptionalLocation = (TextView) findViewById(R.id.itemOptionalLocation);
        mSenderName = (TextView) findViewById(R.id.senderName);
        mCreatedAt = (TextView) findViewById(R.id.createdAt);
        mSoShopBar = (ImageView) findViewById(R.id.soShopBar);
        mCommentButton = (Button) findViewById(R.id.commentButton);

        mSendCommentButton = (Button) findViewById(R.id.sendCommentButton);
        mCommentInputEditText = (EditText) findViewById(R.id.commentInputEditText);


        Intent intent = getIntent();
        String soShopPostObjectId = intent.getStringExtra("soShopPostObjectId");
        mPostIdsVotedSoShopByUser = intent.getStringArrayListExtra("PostIdsVotedSoShopByUser");
        mPostIdsVotedNoShopByUser = intent.getStringArrayListExtra("PostIdsVotedNoShopByUser");

        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_SOSHOP_VOTE);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_NOSHOP_VOTE);
        
        //getVoteStatusOfCurrentUser();

        mRecyclerView = (RecyclerView) findViewById(R.id.commentRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        final Context mContext = getBaseContext();

        ParseQuery query = ParseQuery.getQuery(ParseConstants.CLASS_SOSHOPPOST);
        query.fromLocalDatastore();
        query.getInBackground(soShopPostObjectId, new GetCallback() {
            @Override
            public void done(ParseObject selectedPost, ParseException e) {
                Toast.makeText(mContext, selectedPost.getObjectId(), Toast.LENGTH_LONG).show();
                mSelectedPost = selectedPost;
                addDetailToFullPost(selectedPost, mContext);

                if (mSelectedPost.get(ParseConstants.KEY_SENDER_IDS).equals(ParseUser.getCurrentUser().getObjectId())) {
                    mSoShopButton.setEnabled(false);
                    mNoShopButton.setEnabled(false);
                } else {

                    mSoShopButton.setEnabled(true);
                    mNoShopButton.setEnabled(true);
                    initSoShopButtonStatus(mSelectedPost);
                    initNoShopButtonStatus(mSelectedPost);

                }

                try {
                    loadCurrentComment();
                } catch (ParseException exception) {
                    exception.printStackTrace();
                }


            }
        });


        mSendCommentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String comment = mCommentInputEditText.getText().toString();
                Toast.makeText(FullPostActivity.this,comment,Toast.LENGTH_LONG).show();
                mCommentList.add(comment);

                mCommentAdapter = new CommentAdapter(mCommentList);
                mCommentInputEditText.setText("");
                mRecyclerView.setAdapter(mCommentAdapter);

                //create new class for comment
                //set target post and sender id
                // save
                ParseObject commentItem = new ParseObject(ParseConstants.CLASS_COMMENT);
                commentItem.put(ParseConstants.KEY_COMMENT_TEXT, comment);
                commentItem.put(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());

                ParseRelation<ParseUser> senderRelation = commentItem.getRelation(ParseConstants.KEY_RELATION_COMMENT_SENDER);
                senderRelation.add(mCurrentUser);

                ParseRelation<ParseObject> targetPost = commentItem.getRelation(ParseConstants.KEY_RELATION_TARGET_POST);
                targetPost.add(mSelectedPost);

                ParseRelation<ParseObject> commentRelationOnPost = mSelectedPost.getRelation(ParseConstants.KEY_RELATION_COMMENT);
                commentRelationOnPost.add(commentItem);

                commentItem.saveEventually();
                mSelectedPost.saveEventually();





            }
        });



        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentInputEditText.setFocusableInTouchMode(true);
            }
        });




    }

    private void loadCurrentComment() throws ParseException {

        ParseRelation<ParseObject> currentComments = mSelectedPost.getRelation(ParseConstants.KEY_RELATION_COMMENT);
        ArrayList<ParseObject> currentCommentsList = (ArrayList<ParseObject>) currentComments.getQuery().find();
        int i = 0;
        for (ParseObject currentComment : currentCommentsList){
            mCommentList.add(currentComment.getString(ParseConstants.KEY_COMMENT_TEXT));

            i++;
        }

        mCommentAdapter = new CommentAdapter(mCommentList);
        mCommentInputEditText.setText("");
        mRecyclerView.setAdapter(mCommentAdapter);


    }

    private void initNoShopButtonStatus(final ParseObject shopPost) {
        //START: SET ACTION and VIEW for NOSHOP BUTTON

        if (mPostIdsVotedNoShopByUser.contains(shopPost.getObjectId())){
            mNoShopButton.setText("Voted!");
            mSoShopButton.setEnabled(false);
        }

        mNoShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseRelation<ParseUser> isVotedNoShopRelation = shopPost.getRelation(ParseConstants.KEY_IS_VOTE_NOSHOP_RELATION);

                mNoShopButton.setEnabled(false);
                mSoShopButton.setEnabled(false);

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

                    if (isContained) {
                        //if the user is already voted. remove the vote

                        int tempNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                        tempNoShopNumber--;
                        mNoShopNumberTextView.setText("(" + tempNoShopNumber + ")");
                        mNoShopButton.setText("NoShop");

                        shopPost.increment(ParseConstants.KEY_TOTAL_NOSHOP, -1);
                        isVotedNoShopRelation.remove(mCurrentUser);

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {


                                if (e == null) {

                                    mCurrentUserVoteNoShopRelation.remove(shopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                                            mNoShopNumberTextView.setText("(" + actualNoShopNumber + ")");
                                            mNoShopButton.setEnabled(true);
                                            mNoShopButton.setText("NoShop");
                                            mSoShopButton.setEnabled(true);
                                        }
                                    });

                                } else {

                                }
                            }
                        });

                    } else {
                        // not yet vote add the vote; increase vote and add user to relation in post

                        int tempNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                        tempNoShopNumber++;
                        mNoShopNumberTextView.setText("(" + tempNoShopNumber + ")");
                        shopPost.increment(ParseConstants.KEY_TOTAL_NOSHOP, +1);
                        isVotedNoShopRelation.add(mCurrentUser);
                        mNoShopButton.setText("Voted!");

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {
                                    mCurrentUserVoteNoShopRelation.add(shopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualNoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
                                            mNoShopNumberTextView.setText("(" + actualNoShopNumber + ")");
                                            mNoShopButton.setEnabled(true);
                                            mNoShopButton.setText("Voted!");
                                            mSoShopButton.setEnabled(false);
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

    private void initSoShopButtonStatus(final ParseObject shopPost) {
        //START: SET ACTION and VIEW for SOSHOP BUTTON


        if (mPostIdsVotedSoShopByUser.contains(shopPost.getObjectId())){
            mSoShopButton.setText("Voted!");
            mNoShopButton.setEnabled(false);
        }

        mSoShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean isContained;

                ParseRelation<ParseUser> isVotedSoShopRelation = shopPost.getRelation(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION);

                //disable to avoid rapid duplicate request
                mSoShopButton.setEnabled(false);
                mNoShopButton.setEnabled(false);

                try {
                    //Check if user is already voted.
                    ArrayList<ParseUser> votedUsers = (ArrayList<ParseUser>) isVotedSoShopRelation.getQuery().find();
                    ArrayList<String> votedUsersIds = new ArrayList<>(votedUsers.size()); //create ArrayList String to be used in queries below
                    int subIndex = 0;
                    for (ParseUser votedUser : votedUsers) { //get each voted user id to the list
                        votedUsersIds.add(subIndex, votedUser.getObjectId());
                        subIndex++;
                    }

                    isContained = votedUsersIds.contains(mCurrentUser.getObjectId());

                    if (isContained) {
                        //if the user is already voted. remove the vote

                        int tempSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                        tempSoShopNumber--;
                        mSoShopNumberTextView.setText("(" + tempSoShopNumber + ")");
                        mSoShopButton.setText("SoShop");
                        shopPost.increment(ParseConstants.KEY_TOTAL_SOSHOP, -1);
                        isVotedSoShopRelation.remove(mCurrentUser);

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {


                                if (e == null) {

                                    mCurrentUserVoteSoShopRelation.remove(shopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                                            mSoShopNumberTextView.setText("(" + actualSoShopNumber + ")");
                                            mSoShopButton.setEnabled(true);
                                            mSoShopButton.setText("SoShop");
                                            mNoShopButton.setEnabled(true);
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
                        mSoShopNumberTextView.setText("(" + tempSoShopNumber + ")");
                        shopPost.increment(ParseConstants.KEY_TOTAL_SOSHOP, +1);
                        isVotedSoShopRelation.add(mCurrentUser);
                        mSoShopButton.setText("Voted!");

                        shopPost.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {


                                if (e == null) {
                                    mCurrentUserVoteSoShopRelation.add(shopPost);
                                    mCurrentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            int actualSoShopNumber = shopPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
                                            mSoShopNumberTextView.setText("(" + actualSoShopNumber + ")");
                                            mSoShopButton.setEnabled(true);
                                            mSoShopButton.setText("Voted!");
                                            mNoShopButton.setEnabled(false);
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

    private void addDetailToFullPost(ParseObject selectedPost, Context context) {

        String caption = (String) selectedPost.get(ParseConstants.KEY_CAPTION);
        mCaptionTextView.setText(caption);
        //SoShop number
        int soShopNumber = selectedPost.getInt(ParseConstants.KEY_TOTAL_SOSHOP);
        mSoShopNumberTextView.setText("(" + soShopNumber + ")");
        //NoShop number
        int noShopNumber = selectedPost.getInt(ParseConstants.KEY_TOTAL_NOSHOP);
        mNoShopNumberTextView.setText("(" + noShopNumber + ")");
        //item name
        String itemName = (String) selectedPost.get(ParseConstants.KEY_ITEM_NAME);
        mItemName.setText(itemName);
        //item price with currency
        int itemPriceInt = (int) selectedPost.get(ParseConstants.KEY_ITEM_PRICE);
        String itemPrice = itemPriceInt +"";
        String currency = (String) selectedPost.get(ParseConstants.KEY_CURRENCY);
        mItemPrice.setText(currency + " " + itemPrice);

        //item location description
        String itemLocation = (String) selectedPost.get(ParseConstants.KEY_LOCATION_DESCRIPTION);
        if (itemLocation == null ){
           mItemOptionalLocation.setVisibility(View.INVISIBLE);
        } else {
            mItemOptionalLocation.setText(itemLocation);
            mItemOptionalLocation.setVisibility(View.VISIBLE);
        }

        //first name + last name
        String firstName = (String) selectedPost.get(ParseConstants.KEY_SENDER_FIRST_NAME);
        String lastName = (String) selectedPost.get(ParseConstants.KEY_SENDER_LAST_NAME);
        mSenderName.setText(firstName + " " + lastName);

        //time created
        Date createdAt = selectedPost.getCreatedAt(); //object from parse
        //best way to get time, for now. 17/12/14
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
        mCreatedAt.setText(convertedDate);

        //START: add image from Parse to imageView using tool from Picasso
        ParseFile file = selectedPost.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
        Picasso.with(context).load(fileUri.toString()).into(mImageViewI);
        //END: add image from Parse to imageView using tool from Picasso

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getVoteStatusOfCurrentUser() {
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_SOSHOP_VOTE);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_NOSHOP_VOTE);
        try {
            //get relation for SoShop to render and button status
            mPostVotedSoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteSoShopRelation.getQuery().find();
            mPostIdsVotedSoShopByUser = new ArrayList<>(mPostVotedSoShopByUser.size());
            if (mPostVotedSoShopByUser != null){
                int index = 0;
                for(ParseObject votedSoShop: mPostVotedSoShopByUser){
                    mPostIdsVotedSoShopByUser.add(index, votedSoShop.getObjectId());
                    index++;
                }
            }
            //get relation for NoShop to render and init button status
            mPostVotedNoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteNoShopRelation.getQuery().find();
            mPostIdsVotedNoShopByUser = new ArrayList<>(mPostVotedNoShopByUser.size());
            if (mPostVotedNoShopByUser != null){
                int index = 0;
                for(ParseObject votedNoShop: mPostVotedNoShopByUser){
                    mPostIdsVotedNoShopByUser.add(index, votedNoShop.getObjectId());
                    index++;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
