package soshop.social.soshop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import soshop.social.soshop.Utils.ParseConstants;


public class PostActivity extends ActionBarActivity {

    //Member variables
    private EditText mCaption;
    private EditText mItemName;
    private EditText mItemPrice;
    private EditText mLocationName;

    private Spinner mIsPrivateSpinner;
    private Spinner mCurrencySpinner;

    private MenuItem mPostMenuItem;

    ParseUser mCurrentUser;
    ParseRelation<ParseUser> mFriendsRelation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mCaption = (EditText) findViewById(R.id.captionEditText);
        mItemName = (EditText) findViewById(R.id.itemNameEditText);
        mItemPrice = (EditText) findViewById(R.id.itemPriceEditText);
        mLocationName = (EditText) findViewById(R.id.locationNameEditText);

        mPostMenuItem = (MenuItem) findViewById(R.id.action_post);


        
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);

        mPostMenuItem = menu.getItem(0);

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

        if (id == R.id.action_post){


            String itemName = mItemName.getText().toString();
            String itemPrice = mItemPrice.getText().toString();

            if (itemName.isEmpty() || itemPrice.isEmpty()){

                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setMessage(R.string.post_error_message);
                builder.setTitle(R.string.post_error_title);
                builder.setPositiveButton(android.R.string.ok,null);

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

//                ParseObject soShopPost = createPost(); //return Parse Object to post
//                sendPost(soShopPost); // receive Parse Object

                createAndSendPost();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }



    private ParseObject createPost() {

        String itemName = mItemName.getText().toString();
        String itemPrice = mItemPrice.getText().toString();
        int itemPriceInt = Integer.parseInt(itemPrice);
        String currency = "Thai Baht"; //temporary currency
        String caption = mCaption.getText().toString();

        //create Parse Object for post.
         ParseObject soShopPost = new ParseObject(ParseConstants.CLASS_SOSHOPPOST);

        soShopPost.put(ParseConstants.KEY_SENDER_IDS, ParseUser.getCurrentUser().getObjectId()); //add sender Id
        soShopPost.put(ParseConstants.KEY_ITEM_NAME, itemName);
        soShopPost.put(ParseConstants.KEY_ITEM_PRICE, itemPriceInt);
        soShopPost.put(ParseConstants.KEY_CURRENCY, currency);
        soShopPost.put(ParseConstants.KEY_CAPTION, caption);
//        soShopPost.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());




        return soShopPost;
    }



    private void sendPost(ParseObject soShopPost) {


        soShopPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Your Item have been post", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createAndSendPost() {

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e == null) {

                    String itemName = mItemName.getText().toString();
                    String itemPrice = mItemPrice.getText().toString();
                    int itemPriceInt = Integer.parseInt(itemPrice);
                    String currency = "Thai Baht"; //temporary currency
                    String caption = mCaption.getText().toString();
                    ArrayList recipientsIds = new ArrayList<String>();

                    for( int i = 0; i < friends.size(); i++){

                        recipientsIds.add(friends.get(i).getObjectId());

                    }

                    //create Parse Object for post.
                    ParseObject soShopPost = new ParseObject(ParseConstants.CLASS_SOSHOPPOST);

                    soShopPost.put(ParseConstants.KEY_SENDER_IDS, ParseUser.getCurrentUser().getObjectId()); //add sender Id
                    soShopPost.put(ParseConstants.KEY_ITEM_NAME, itemName);
                    soShopPost.put(ParseConstants.KEY_ITEM_PRICE, itemPriceInt);
                    soShopPost.put(ParseConstants.KEY_CURRENCY, currency);
                    soShopPost.put(ParseConstants.KEY_CAPTION, caption);
                    soShopPost.put(ParseConstants.KEY_RECIPIENT_IDS, recipientsIds);

                    soShopPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(getApplicationContext(), "Your Item have been post", Toast.LENGTH_LONG).show();
                        }
                    });


                }


            }

        });

    }





}
