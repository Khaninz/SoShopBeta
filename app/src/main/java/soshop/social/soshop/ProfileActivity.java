package soshop.social.soshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
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

import soshop.social.soshop.Utils.ParseConstants;


public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final TextView mFriendsNumber;
        final TextView mPostNumber;
        final TextView mVoteNumber;
        final TextView mProfileName;


        mFriendsNumber = (TextView) findViewById(R.id.friendsNumber);
        mPostNumber = (TextView) findViewById(R.id.postNumber);
        mVoteNumber = (TextView) findViewById(R.id.voteNumber);
        mProfileName = (TextView) findViewById(R.id.profileName);

        Intent intent = getIntent();
        String senderId = intent.getStringExtra("SENDER_ID");
        String senderName = intent.getStringExtra("SENDER_FIRST_NAME");

        Toast.makeText(this, senderName + ": " + senderId, Toast.LENGTH_LONG).show();

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

                    mProfileName.setText(firstName+" "+lastName);

                    ParseRelation<ParseUser> friendsRelation = displayUser.getRelation(ParseConstants.KEY_RELATION_FRIENDS);
                    friendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> friends, ParseException e) {

                            mFriendsNumber.setText(friends.size() + "");
                        }
                    });

                    ParseQuery<ParseObject> queryForUserPost = ParseQuery.getQuery(ParseConstants.CLASS_SOSHOPPOST);
                    queryForUserPost.whereEqualTo(ParseConstants.KEY_POST_SENDER_ID,displayUserId);
                    queryForUserPost.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
                    queryForUserPost.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> userPosts, ParseException e) {
                            if(e==null){
                            mPostNumber.setText(userPosts.size()+"");
                        }}
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
                            mVoteNumber.setText(parseObjects.size()+"");
                        }
                    });



                } else {
                    Toast.makeText(ProfileActivity.this, "error loading profile page: " + e.toString(), Toast.LENGTH_LONG).show();
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
