package soshop.social.soshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

    ParseUser mCurrentUser;
    ParseRelation mCurrentUserVoteSoShopRelation;
    ParseRelation mCurrentUserVoteNoShopRelation;
    private ArrayList<ParseObject> mVotedSoShopByUser;
    private ArrayList<Object> mVotedSoShopByUserIds;
    private ArrayList<ParseObject> mVotedNoShopByUser;
    private ArrayList<Object> mVotedNoShopByUserIds;

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


        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUserVoteSoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_SOSHOP_VOTE_RELATION);
        mCurrentUserVoteNoShopRelation = mCurrentUser.getRelation(ParseConstants.KEY_NOSHOP_VOTE_RELATION);
        try {
            //get relation for SoShop to render and button status
            mVotedSoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteSoShopRelation.getQuery().find();
            mVotedSoShopByUserIds = new ArrayList<>(mVotedSoShopByUser.size());
            if (mVotedSoShopByUser != null) {
                int index = 0;
                for (ParseObject votedSoShop : mVotedSoShopByUser) {
                    mVotedSoShopByUserIds.add(index, votedSoShop.getObjectId());
                    index++;
                }
            }
            //get relation for NoShop to render and init button status
            mVotedNoShopByUser = (ArrayList<ParseObject>) mCurrentUserVoteNoShopRelation.getQuery().find();
            mVotedNoShopByUserIds = new ArrayList<>(mVotedNoShopByUser.size());
            if (mVotedNoShopByUser != null) {
                int index = 0;
                for (ParseObject votedNoShop : mVotedNoShopByUser) {
                    mVotedNoShopByUserIds.add(index, votedNoShop.getObjectId());
                    index++;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        Intent intent = getIntent();
        String soShopPostObjectId = intent.getStringExtra("soShopPostObjectId");
        Toast.makeText(this, soShopPostObjectId, Toast.LENGTH_LONG).show();

        ParseQuery<ParseObject> selectedPostQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_SOSHOPPOST);
        try {

            ParseObject selectedPost = selectedPostQuery.get(soShopPostObjectId);

            //START: add image from Parse to imageView using tool from Picasso
            ParseFile file = selectedPost.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
            Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
            Picasso.with(this).load(fileUri.toString()).into(mImageViewI);
            //END: add image from Parse to imageView using tool from Picasso


            //START: disable vote if it is user's own post
            if (mCurrentUser.getObjectId().equals(selectedPost.get(ParseConstants.KEY_SENDER_IDS))) {
                mSoShopButton.setEnabled(false);
                mNoShopButton.setEnabled(false);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

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
