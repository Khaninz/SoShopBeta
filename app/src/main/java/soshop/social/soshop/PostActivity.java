package soshop.social.soshop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;


public class PostActivity extends ActionBarActivity {

    //Member variables
    private EditText mCaption;
    private EditText mItemName;
    private EditText mItemPrice;
    private EditText mLocationName;

    private Spinner mIsPrivateSpinner;
    private Spinner mCurrencySpinner;

    private MenuItem mPostMenuItem;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);


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

            String caption = mCaption.getText().toString();
            String itemName = mItemName.getText().toString();
            String itemPrice = mItemPrice.getText().toString();
//            int finalItemPrice = Integer.parseInt(itemPrice);

            if (itemName.isEmpty() || itemPrice.isEmpty()){

                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setMessage(R.string.post_error_message);
                builder.setTitle(R.string.post_error_title);
                builder.setPositiveButton(android.R.string.ok,null);

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                createPost();
                sendPost(null);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ParseObject createPost() {



        return null;
    }

    private void sendPost(ParseObject post) {

        Toast.makeText(getApplicationContext(), "Test post menu item click", Toast.LENGTH_LONG).show();

    }
}
