package soshop.social.soshop.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFriendsFragment extends android.support.v4.app.ListFragment{

    //create member variable of list the contain ParseUser data.
    protected List<ParseUser> mUsers;
    //declare relation members to restore parse relations.
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    public InviteFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_invite_friends, container, false);



        return rootView;



    }



    @Override
    public void onResume() {
        super.onResume();

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        //query all user except current user
        ParseQuery<ParseUser> allUserQuery = ParseQuery.getUserQuery();
        allUserQuery.whereNotEqualTo(ParseConstants.KEY_OBJECT_ID, mCurrentUser.getObjectId()); //exclude user self from the list
        allUserQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    //success
                    mUsers = users;
                    String[] userEmail = new String[mUsers.size()];

                    int i = 0;
                    for (ParseUser user : mUsers) {
                        userEmail[i] = user.getEmail();
                        i++;
                    }

                    if (getListView().getAdapter() == null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, userEmail);
                        setListAdapter(adapter);

                        addFriendsCheckmarks();

                    } else {

                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListView().getAdapter();
                        adapter.notifyDataSetChanged();
                    }



                }
            }
        });


    }

    private void addFriendsCheckmarks() {

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null){

                    for (int i = 0 ; i < mUsers.size() ; i++){

                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend: friends){
                            if (friend.getObjectId().equals(user.getObjectId())){
                                getListView().setItemChecked(i, true);
                            }
                        }


                    }

                }
            }
        });

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {
            mFriendsRelation.add(mUsers.get(position));

        } else {
            mFriendsRelation.remove(mUsers.get(position));

        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });

    }
}
