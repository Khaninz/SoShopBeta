package soshop.social.soshop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import java.util.Date;

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

    private ParseObject mSelectedPost;


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


        Intent intent = getIntent();
        String soShopPostObjectId = intent.getStringExtra("soShopPostObjectId");

        final Context mContext = getBaseContext();

        ParseQuery query = ParseQuery.getQuery(ParseConstants.CLASS_SOSHOPPOST);
        query.getInBackground(soShopPostObjectId, new GetCallback() {
            @Override
            public void done(ParseObject selectedPost, ParseException e) {
                Toast.makeText(mContext, selectedPost.getObjectId(), Toast.LENGTH_LONG).show();
                mSelectedPost = selectedPost;
                addDetailToFullPost(selectedPost, mContext);

            }
        });

        ParseRelation<ParseUser> isVotedSoShopRelation = mSelectedPost.getRelation(ParseConstants.KEY_IS_VOTE_SOSHOP_RELATION);

        mSoShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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

}
