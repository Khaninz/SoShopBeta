package soshop.social.soshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import soshop.social.soshop.Adapter.PostGridViewAdapter;
import soshop.social.soshop.Adapter.UserGridViewAdapter;
import soshop.social.soshop.Utils.IntentConstants;
import soshop.social.soshop.Utils.ParseConstants;


public class ProfileActivity extends ActionBarActivity {

    //member for UI
    private TextView mFriendsNumber;
    private TextView mPostNumber;
    private TextView mVoteNumber;
    private TextView mProfileName;

    //member for Recycler View
    protected RecyclerView mRecyclerView;
    protected PostGridViewAdapter mPostAdapter;
    protected UserGridViewAdapter mUserAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    //member for List of ParseObjects
    private List<ParseUser> mFrieds;
    private List<ParseObject> mPostByUser;
    private List<ParseObject> mVoteByUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFriendsNumber = (TextView) findViewById(R.id.friendsNumber);
        mPostNumber = (TextView) findViewById(R.id.postNumber);
        mVoteNumber = (TextView) findViewById(R.id.voteNumber);
        mProfileName = (TextView) findViewById(R.id.profileName);

        //START: Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.gridRecyclerView);
        mLayoutManager = new GridLayoutManager(ProfileActivity.this, 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //END: Recycler View

        Intent intent = getIntent();
        String senderId = intent.getStringExtra(IntentConstants.KEY_USER_ID);
//        String senderName = intent.getStringExtra("SENDER_FIRST_NAME");
//
//        Toast.makeText(this, senderName + ": " + senderId, Toast.LENGTH_LONG).show();

        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseConstants.KEY_OBJECT_ID, senderId);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {

                    ParseUser displayUser = users.get(0);
                    String displayUserId = displayUser.getObjectId();

                    String firstName = displayUser.getString(ParseConstants.KEY_FIRST_NAME);
                    String lastName = displayUser.getString(ParseConstants.KEY_LAST_NAME);

                    mProfileName.setText(firstName + " " + lastName);

                    ParseRelation<ParseUser> friendsRelation = displayUser.getRelation(ParseConstants.KEY_RELATION_FRIENDS);

                    friendsRelation.getQuery().addAscendingOrder(ParseConstants.KEY_FIRST_NAME).findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> friends, ParseException e) {

                            mFriendsNumber.setText(friends.size() + "");
                            mFrieds = friends;
                        }
                    });

                    ParseQuery<ParseObject> queryForUserPost = ParseQuery.getQuery(ParseConstants.CLASS_SOSHOPPOST);
                    queryForUserPost.whereEqualTo(ParseConstants.KEY_POST_SENDER_ID, displayUserId);
                    queryForUserPost.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
                    queryForUserPost.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> userPosts, ParseException e) {
                            if (e == null) {
                                mPostNumber.setText(userPosts.size() + "");
                                mPostByUser = userPosts;
                            }
                        }
                    });

                    ParseQuery<ParseObject> queryForUserSoShopVote = ParseQuery.getQuery(ParseConstants.CLASS_SOSHOPPOST);
                    queryForUserSoShopVote.whereEqualTo(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION, displayUser);

                    ParseQuery<ParseObject> queryForUserNoShopVote = ParseQuery.getQuery(ParseConstants.CLASS_SOSHOPPOST);
                    queryForUserNoShopVote.whereEqualTo(ParseConstants.KEY_IS_VOTE_NOSHOP_RELATION, displayUser);

                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(queryForUserSoShopVote);
                    queries.add(queryForUserNoShopVote);

                    ParseQuery<ParseObject> queryForAllUserVote = ParseQuery.or(queries);

                    // arrange to newest post at the top
                    queryForAllUserVote.addDescendingOrder(ParseConstants.KEY_CREATED_AT);

                    queryForAllUserVote.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            mVoteNumber.setText(parseObjects.size() + "");

                            mVoteNumber.setTextColor(getResources().getColor(R.color.selected_text));
                            mPostNumber.setTextColor(getResources().getColor(R.color.normal_text));
                            mFriendsNumber.setTextColor(getResources().getColor(R.color.normal_text));

                            mVoteByUser = parseObjects;

                            mPostAdapter = new PostGridViewAdapter(ProfileActivity.this, mVoteByUser);
                            mRecyclerView.setAdapter(mPostAdapter);
                        }
                    });


                } else {
                    Toast.makeText(ProfileActivity.this, "error loading profile page: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        mPostNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPostByUser != null) {
                    //mPost by user is not empty
                    mPostAdapter = new PostGridViewAdapter(ProfileActivity.this, mPostByUser);
                    mRecyclerView.setAdapter(mPostAdapter);

                    mPostNumber.setTextColor(getResources().getColor(R.color.selected_text));
                    mVoteNumber.setTextColor(getResources().getColor(R.color.normal_text));
                    mFriendsNumber.setTextColor(getResources().getColor(R.color.normal_text));
                } else {
                    //if empty
                    //show UI and Link to add friends or post

                }
            }
        });

        mVoteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVoteByUser != null) {
                    //if not empty
                    mPostAdapter = new PostGridViewAdapter(ProfileActivity.this, mVoteByUser);
                    mRecyclerView.setAdapter(mPostAdapter);

                    mPostNumber.setTextColor(getResources().getColor(R.color.normal_text));
                    mVoteNumber.setTextColor(getResources().getColor(R.color.selected_text));
                    mFriendsNumber.setTextColor(getResources().getColor(R.color.normal_text));
                }
            }
        });

        mFriendsNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFrieds != null) {
                    mUserAdapter = new UserGridViewAdapter(ProfileActivity.this, mFrieds);
                    mRecyclerView.setAdapter(mUserAdapter);

                    mPostNumber.setTextColor(getResources().getColor(R.color.normal_text));
                    mVoteNumber.setTextColor(getResources().getColor(R.color.normal_text));
                    mFriendsNumber.setTextColor(getResources().getColor(R.color.selected_text));
                }
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
}
