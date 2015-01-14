package soshop.social.soshop.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import soshop.social.soshop.PostActivity;
import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFeedFragment extends android.support.v4.app.ListFragment {

    //member variable
    protected Button mPostButton;

    public MainFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_main_feed, container, false);

        mPostButton = (Button) rootView.findViewById(R.id.postButton);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
            }
        });

        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveMainFeed();
    }



    private void retrieveMainFeed() {

        // 2 conditions in query have to work as Or operator
        //query for user's post
        ParseQuery<ParseObject> userPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        userPostQuery.whereEqualTo(ParseConstants.KEY_SENDER_IDS, ParseUser.getCurrentUser().getObjectId());

        //query for friend of user post.
        ParseQuery<ParseObject> userFriendsPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        userFriendsPostQuery.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());

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
                    Toast.makeText(getActivity(), "Query success, " + soShopPostObjects.size() + " objects get", Toast.LENGTH_LONG).show();

                    String[] userCaption = new String[soShopPostObjects.size()];
                    int i = 0;
                    for (ParseObject soShopPostObject: soShopPostObjects){
                        userCaption[i] = soShopPostObject.getString(ParseConstants.KEY_SENDER_CAPTION);
                        i++;
                    }

                    //add condition so list view adapter does not create every time it is resume , WHICH CAN PREVENT APP CRASH.
                    if(getListView().getAdapter()==null) {

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, userCaption);
                        setListAdapter(adapter);
                    } else {

                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListView().getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });


    }
}
