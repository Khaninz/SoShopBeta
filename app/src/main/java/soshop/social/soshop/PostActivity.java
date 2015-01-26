package soshop.social.soshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import soshop.social.soshop.Utils.FileHelper;
import soshop.social.soshop.Utils.ParseConstants;


public class PostActivity extends ActionBarActivity {

    //Member variables
    private EditText mCaption;
    private EditText mItemName;
    private EditText mItemPrice;
    private EditText mLocationName;

    private ImageView mItemPicture1;
    private ImageView mTakePictureAtcion;
    private ImageView mChooseFromGalleryAction;
    protected Uri mMediaUri;
    protected byte[] scaledData;
    protected ParseFile mFile;

    private Spinner mIsPrivateSpinner;
    private Spinner mCurrencySpinner;

    private MenuItem mPostMenuItem;

    ParseUser mCurrentUser;
    ParseRelation<ParseUser> mFriendsRelation;

    private static final int TAKE_PICTURE_REQUEST_CODE = 0;
    private static final int CHOOSE_FROM_GALLERY_REQUES_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mCaption = (EditText) findViewById(R.id.captionEditText);
        mItemName = (EditText) findViewById(R.id.itemNameEditText);
        mItemPrice = (EditText) findViewById(R.id.itemPriceEditText);
        mLocationName = (EditText) findViewById(R.id.locationNameEditText);
        mTakePictureAtcion = (ImageView) findViewById(R.id.takePicture);
        mChooseFromGalleryAction = (ImageView) findViewById(R.id.chooseFromGallery);
        mItemPicture1 = (ImageView) findViewById(R.id.imageView);

        mPostMenuItem = (MenuItem) findViewById(R.id.action_post);

        mTakePictureAtcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhotoIntent, TAKE_PICTURE_REQUEST_CODE);
            }
        });

        mChooseFromGalleryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFromGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFromGalleryIntent.setType("image/*");
                startActivityForResult(chooseFromGalleryIntent, CHOOSE_FROM_GALLERY_REQUES_CODE);
            }
        });
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(resultCode == RESULT_OK){

            if (requestCode == TAKE_PICTURE_REQUEST_CODE) {


                mMediaUri = data.getData();
                mItemPicture1.setImageURI(mMediaUri);
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();

                //Picasso.with(this).load(mMediaUri).into(mItemPicture1);

            }

            if (requestCode == CHOOSE_FROM_GALLERY_REQUES_CODE){
                mMediaUri = data.getData();
                mItemPicture1.setImageURI(mMediaUri);
                Toast.makeText(this, "Image obtained from:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                //Picasso.with(this).load(mMediaUri).into(mItemPicture1);
            }

            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

            if(fileBytes == null){
               // return null; //prevent crash and let other user try different files
            } else{
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }

                 mFile = new ParseFile("image1", fileBytes);



        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        if(mMediaUri != null){
            mItemPicture1.setImageURI(mMediaUri);
        }




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

            if (itemName.isEmpty() || itemPrice.isEmpty() || mFile == null){

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
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    soShopPost.put(ParseConstants.KEY_SENDER_IDS, currentUser.getObjectId()); //add sender Id
                    soShopPost.put(ParseConstants.KEY_ITEM_NAME, itemName);
                    soShopPost.put(ParseConstants.KEY_ITEM_PRICE, itemPriceInt);
                    soShopPost.put(ParseConstants.KEY_CURRENCY, currency);
                    soShopPost.put(ParseConstants.KEY_CAPTION, caption);
                    soShopPost.put(ParseConstants.KEY_RECIPIENT_IDS, recipientsIds);
                    soShopPost.put(ParseConstants.KEY_IMAGE_I,mFile);
                    soShopPost.put(ParseConstants.KEY_SENDER_FIRST_NAME,currentUser.get(ParseConstants.KEY_FIRST_NAME));
                    soShopPost.put(ParseConstants.KEY_SENDER_LAST_NAME,currentUser.get(ParseConstants.KEY_LAST_NAME));

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
