package soshop.social.soshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import soshop.social.soshop.Utils.FileHelper;
import soshop.social.soshop.Utils.ImageResizer;
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
        mLocationName = (EditText) findViewById(R.id.locationDescriptionEditText);
        mTakePictureAtcion = (ImageView) findViewById(R.id.takePicture);
        mChooseFromGalleryAction = (ImageView) findViewById(R.id.chooseFromGallery);
        mItemPicture1 = (ImageView) findViewById(R.id.imageView);

        //START: currency spinner setting
        mCurrencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter currencySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.currency_array, android.R.layout.simple_spinner_dropdown_item);
        mCurrencySpinner.setAdapter(currencySpinnerAdapter);

        mIsPrivateSpinner = (Spinner) findViewById(R.id.privacySpinner);
        ArrayAdapter privacySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.privacy_array, android.R.layout.simple_spinner_dropdown_item);
        mIsPrivateSpinner.setAdapter(privacySpinnerAdapter);

        //END: currency spinner setting

        mPostMenuItem = (MenuItem) findViewById(R.id.action_post);


        mTakePictureAtcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaUri != null) {
                    mMediaUri = null;
                    mItemPicture1.setImageDrawable(null);
                }

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhotoIntent, TAKE_PICTURE_REQUEST_CODE);
            }
        });

        mChooseFromGalleryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaUri != null) {
                    mMediaUri = null;
                    mItemPicture1.setImageDrawable(null);
                }

                Intent chooseFromGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFromGalleryIntent.setType("image/*");
                startActivityForResult(chooseFromGalleryIntent, CHOOSE_FROM_GALLERY_REQUES_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
                mMediaUri = data.getData();
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            }

            if (requestCode == CHOOSE_FROM_GALLERY_REQUES_CODE) {
                mMediaUri = data.getData();
                Toast.makeText(this, "Image obtained from:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            }

            //Get file bytes to be used in bitmap and reduced for upload.
            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

            //Check if taken from camera or else, if camera then rotate first
            if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
                Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(fileBytes, 320);

                Toast.makeText(this, "Width: " + bitmap.getWidth() + " Height: " + bitmap.getHeight(), Toast.LENGTH_LONG).show();

//                //Picture taken from camera is rotated by 90 degree by default, write code to override the default
//                Matrix matrix = new Matrix();
//                matrix.postRotate(90);
//                bitmap = Bitmap.createBitmap(bitmap, 0,
//                        0, bitmap.getWidth(), bitmap.getHeight(),
//                        matrix, true);


                mItemPicture1.setImageBitmap(bitmap);

            } else {

                Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(fileBytes, 320);
                mItemPicture1.setImageBitmap(bitmap);

            }


            if (fileBytes == null) {
                //return null; //prevent crash and let other user try different files
            } else {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes, requestCode);
            }

            mFile = new ParseFile("image1", fileBytes);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION_FRIENDS);

//        if (mMediaUri != null) {
//            mItemPicture1.setImageURI(mMediaUri);
//        }


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

        if (id == R.id.action_post) {


            String itemName = mItemName.getText().toString();
            String itemPrice = mItemPrice.getText().toString();

            if (itemName.isEmpty() || itemPrice.isEmpty() || mFile == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setMessage(R.string.post_error_message);
                builder.setTitle(R.string.post_error_title);
                builder.setPositiveButton(android.R.string.ok, null);

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

        soShopPost.put(ParseConstants.KEY_POST_SENDER_ID, ParseUser.getCurrentUser().getObjectId()); //add sender Id
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

        String itemName = mItemName.getText().toString();
        String itemPrice = mItemPrice.getText().toString();
        int itemPriceInt = Integer.parseInt(itemPrice);
        String currency = mCurrencySpinner.getSelectedItem().toString();
        String caption = mCaption.getText().toString();
        String locationDescription = mLocationName.getText().toString();


        //create Parse Object for post.
        ParseObject soShopPost = new ParseObject(ParseConstants.CLASS_SOSHOPPOST);
        ParseRelation<ParseUser> soShopPostSender = soShopPost.getRelation(ParseConstants.KEY_RELATION_POST_SENDER);
        ParseUser currentUser = ParseUser.getCurrentUser();

        soShopPost.put(ParseConstants.KEY_POST_SENDER_ID, currentUser.getObjectId()); //add sender Id
        soShopPost.put(ParseConstants.KEY_ITEM_NAME, itemName);
        soShopPost.put(ParseConstants.KEY_ITEM_PRICE, itemPriceInt);
        soShopPost.put(ParseConstants.KEY_CURRENCY, currency);
        soShopPost.put(ParseConstants.KEY_CAPTION, caption);
        soShopPost.put(ParseConstants.KEY_IMAGE_I, mFile);
        soShopPost.put(ParseConstants.KEY_SENDER_FIRST_NAME, currentUser.get(ParseConstants.KEY_FIRST_NAME));
        soShopPost.put(ParseConstants.KEY_SENDER_LAST_NAME, currentUser.get(ParseConstants.KEY_LAST_NAME));
        soShopPost.put(ParseConstants.KEY_LOCATION_DESCRIPTION, locationDescription);
        soShopPostSender.add(currentUser);

        Boolean isPravate = null;
        if (mIsPrivateSpinner.getSelectedItem().toString().equals("PRIVATE")) {
            isPravate = true;
        } else {
            isPravate = false;
        }

        soShopPost.put(ParseConstants.KEY_IS_PRIVATE, isPravate);


        soShopPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Your Item have been post", Toast.LENGTH_LONG).show();
            }
        });

    }


}
