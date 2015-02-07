package soshop.social.soshop.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import soshop.social.soshop.Adapter.FeedViewAdapter;
import soshop.social.soshop.LoginActivity;
import soshop.social.soshop.PostActivity;
import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFeedFragment extends android.support.v4.app.Fragment {

    //member variable
    protected Button mPostButton;
    protected ParseUser mCurrentUser;
    protected ParseRelation mFriendsRelation;
    protected ProgressBar mProgressBar;
    List<ParseObject> mSoShopPostObjects = null;

    //member for Recycler View
    protected RecyclerView mRecyclerView;
    protected FeedViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    //member for swipe refresh layout
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private ParseRelation<ParseObject> mCurrentUserVoteSoShopRelation;
    private ParseRelation<ParseObject> mCurrentUserVoteNoShopRelation;
    private ArrayList<ParseObject> mPostVotedSoShopByUser;
    private ArrayList<String> mPostIdsVotedSoShopByUser;
    private ArrayList<ParseObject> mPostVotedNoShopByUser;
    private ArrayList<String> mPostIdsVotedNoShopByUser;


    public MainFeedFragment() {
        // Required empty public constructor
//        ParseQuery<ParseUser> queryCurrentUser = ParseQuery.getQuery(ParseConstants.CLASS_USER);
//        queryCurrentUser.include(ParseConstants.KEY_RELATION_FRIENDS);
//        queryCurrentUser.include(ParseConstants.KEY_RELATION_SOSHOP_VOTE);
//        queryCurrentUser.include(ParseConstants.KEY_RELATION_NOSHOP_VOTE);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        //START:Initialize the button for posting.
        mPostButton = (Button) rootView.findViewById(R.id.postButton);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
            }
        });
        //END:Initialize the button for posting.

        //START: init and code for swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "We are refreshing!", Toast.LENGTH_LONG).show();
                try {

                    ParseObject.unpinAll();
                    getVoteStatusOfCurrentUser();
                    retrieveFeedFromServerAndPinOnly();
                    retrieveFeedFromLocalAndStartViewAdapter();


                    //temp test
                    //estQueryWithRelation();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        //END: init and code for swipe refresh layout

        //START: Recycler View
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //END: Recycler View

        getVoteStatusOfCurrentUser();

        //START guery and save objects from server
        mProgressBar.setVisibility(View.VISIBLE);
        try {
            retrieveFeedFromServerAndPinOnly();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Load from server fail, please try again", Toast.LENGTH_LONG).show();
            ;
        }
        //END: guery and save objects from server

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        getVoteStatusOfCurrentUser();

        mCurrentUser = ParseUser.getCurrentUser();
        if (mCurrentUser != null) {

            try {

                retrieveFeedFromLocalAndStartViewAdapter();

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Load from local fail, please try again", Toast.LENGTH_LONG).show();
                ;

            }

        } else {

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            //add flag to intent, so user can't use 'back' to the main activity
            //read more about Flag in the api intent api document
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        //testQueryWithRelation();

    }

    private void testQueryWithRelation() {

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_FRIENDS);

        List<ParseObject> tempUserPostList;
        List<ParseObject> tempUserFriendsPostList;

        ParseQuery<ParseObject> userPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        //userPostQuery.whereEqualTo(ParseConstants.KEY_SENDER_IDS, mCurrentUser.getObjectId());
        userPostQuery.whereEqualTo(ParseConstants.KEY_RELATION_POST_SENDER, mCurrentUser);
        userPostQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                Toast.makeText(getActivity(), "User Post: "+parseObjects.size()+"",Toast.LENGTH_LONG).show();

            }
        });


        try {
            ArrayList<ParseUser> friends = (ArrayList<ParseUser>) mFriendsRelation.getQuery().find();

            ParseQuery<ParseObject> userFriendsPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
            userFriendsPostQuery.whereContainedIn(ParseConstants.KEY_RELATION_POST_SENDER, friends);
            userFriendsPostQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    Toast.makeText(getActivity(), "Friend Post: "+parseObjects.size()+"",Toast.LENGTH_LONG).show();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        ParseQuery<ParseUser> queryCurrentUser = ParseQuery.getQuery(ParseConstants.CLASS_USER);
//        queryCurrentUser.include(ParseConstants.KEY_RELATION_FRIENDS);
//        queryCurrentUser.include(ParseConstants.KEY_RELATION_SOSHOP_VOTE);
//        queryCurrentUser.include(ParseConstants.KEY_RELATION_NOSHOP_VOTE);
//
//        queryCurrentUser.getInBackground(mCurrentUser.getObjectId(), new GetCallback<ParseUser>() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (e==null){
//                List<ParseUser> userFriendsList = user.getList(ParseConstants.KEY_RELATION_FRIENDS);
//                Toast.makeText(getActivity(),userFriendsList.size()+"",Toast.LENGTH_LONG).show();
//            }else{
//                    Toast.makeText(getActivity(),"error get friend list for" + mCurrentUser.getUsername() + ", id: " + mCurrentUser.getObjectId(),Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });

    }

    private void getVoteStatusOfCurrentUser() {
        mCurrentUser = ParseUser.getCurrentUser();

        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_SOSHOP_VOTE);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_NOSHOP_VOTE);
        try {
            //get relation for SoShop to render and button status
            mPostVotedSoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteSoShopRelation.getQuery().find();

            //get relation for NoShop to render and init button status
            mPostVotedNoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteNoShopRelation.getQuery().find();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Error getting vote status of User",Toast.LENGTH_LONG).show();
        }
    }

    private void retrieveFeedFromLocalAndStartViewAdapter() throws ParseException {

        ParseQuery<ParseObject> mainFeedQuery = createMainFeedQuery();

        //start finding from local
        mainFeedQuery.fromLocalDatastore();
        mainFeedQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> soShopPostObjects, ParseException e) {

                if (e == null) {

                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (mAdapter == null) {
                        mAdapter = new FeedViewAdapter(soShopPostObjects, getActivity(), mPostVotedSoShopByUser, mPostVotedNoShopByUser);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {

                }
            }
        });

    }

    private void retrieveFeedFromServerAndPinOnly() throws ParseException {

        if (mSoShopPostObjects != null) { //if not empty clear it the list of objects first.
            mSoShopPostObjects.clear();
        }

        ParseQuery<ParseObject> mainFeedQuery = createMainFeedQuery();

        mainFeedQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> soShopPostObjects, ParseException e) {

                if (e == null) {
                    //Query from server success
                    mSoShopPostObjects = soShopPostObjects;
                    //pin all object in list to be used for local query
                    ParseObject.pinAllInBackground(mSoShopPostObjects);

                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

//                    if (mAdapter == null) {
//                    mAdapter = new FeedViewAdapter(mSoShopPostObjects, getActivity(), mPostIdsVotedSoShopByUser, mPostIdsVotedNoShopByUser);
//                    mRecyclerView.setAdapter(mAdapter);
//                    }
                } else {
                    //Query from server fail

                }
            }
        });


    }

    private ParseQuery<ParseObject> createMainFeedQuery() throws ParseException {

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_FRIENDS);

        ArrayList<ParseUser> friends = (ArrayList<ParseUser>) mFriendsRelation.getQuery().find(); //program auto add throws for getQuery.find
        ArrayList<String> friendsIds = new ArrayList<>(friends.size()); //create ArrayList String to be used in queries below
        int i = 0;
        for (ParseUser friend : friends) { //get each friend id to the list
            friendsIds.add(i, friend.getObjectId());
            i++;
        }

        // 2 conditions in query have to work as Or operator
        //query for user's post
        ParseQuery<ParseObject> userPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        userPostQuery.whereEqualTo(ParseConstants.KEY_SENDER_IDS, mCurrentUser.getObjectId());
        //userPostQuery.whereEqualTo(ParseConstants.KEY_RELATION_POST_SENDER, mCurrentUser);


        //query for friend of user post.
        ParseQuery<ParseObject> userFriendsPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        //userFriendsPostQuery.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, mCurrentUser.getObjectId()); //query to match Post Recipient Ids with current user Ids.
        userFriendsPostQuery.whereContainedIn(ParseConstants.KEY_SENDER_IDS, friendsIds); // query to match Sender Ids of post to the friend Ids of user. this is add to fix when post is still show when user unfriend the poster.
        //userFriendsPostQuery.whereContainedIn(ParseConstants.KEY_RELATION_POST_SENDER, friends);

        // add or operator
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(userPostQuery);
        queries.add(userFriendsPostQuery);

        ParseQuery<ParseObject> mainFeedQuery = ParseQuery.or(queries);

        // arrange to newest post at the top
        mainFeedQuery.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        return mainFeedQuery;
    }

}
