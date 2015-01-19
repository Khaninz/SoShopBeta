package soshop.social.soshop.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import soshop.social.soshop.Adapter.FeedViewAdapter;
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

    //member for Recycler View
    protected RecyclerView mRecyclerView;
    protected FeedViewAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public MainFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //START: Recycler View


        //END: Recycler View

        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            retrieveMainFeed();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    private void retrieveMainFeed() throws ParseException {

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);




        ArrayList<ParseUser> friends = (ArrayList<ParseUser>) mFriendsRelation.getQuery().find(); //program auto add throws for getQuery.find
        ArrayList<String> friendsIds = new ArrayList<>(friends.size()); //create ArrayList String to be used in queries below
        int i = 0;
        for (ParseUser friend : friends){ //get each friend id to the list
            friendsIds.add(i, friend.getObjectId());
            i++;
        }


        // 2 conditions in query have to work as Or operator
        //query for user's post
        ParseQuery<ParseObject> userPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        userPostQuery.whereEqualTo(ParseConstants.KEY_SENDER_IDS, mCurrentUser.getObjectId());

        //query for friend of user post. User id appear in recipients and sender is still friend with a user.
        ParseQuery<ParseObject> userFriendsPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        //userFriendsPostQuery.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, mCurrentUser.getObjectId());
        userFriendsPostQuery.whereContainedIn(ParseConstants.KEY_SENDER_IDS, friendsIds ); // this is add to fix when post is still show when user unfriend the poster.

        // add or operator
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(userPostQuery);
        queries.add(userFriendsPostQuery);

        ParseQuery<ParseObject> mainFeedQuery = ParseQuery.or(queries);

        // arrange to newest post at the top
        mainFeedQuery.addDescendingOrder(ParseConstants.KEY_CREATED_AT);

        //start finding
        mainFeedQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> soShopPostObjects, ParseException e) {
                    if (e == null){
                    //Query Success
                    //Toast.makeText(getActivity(), "Query success, " + soShopPostObjects.size() + " objects get", Toast.LENGTH_LONG).show();

//                    String[] userCaption = new String[soShopPostObjects.size()];
//                    int i = 0;
//                    for (ParseObject soShopPostObject: soShopPostObjects){
//                        userCaption[i] = soShopPostObject.getString(ParseConstants.KEY_SENDER_CAPTION);
//                        i++;
//                    }
//
//                    //add condition so list view adapter does not create every time it is resume , WHICH CAN PREVENT APP CRASH.
//                    if(getListView().getAdapter()==null) {
//
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, userCaption);
//                        setListAdapter(adapter);
//                    } else {
//
//                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListView().getAdapter();
//                        adapter.notifyDataSetChanged();



//                    }
                        mAdapter = new FeedViewAdapter(soShopPostObjects);
                        mRecyclerView.setAdapter(mAdapter);
                }

            }
        });


    }


}
