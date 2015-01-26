package soshop.social.soshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;


public class LoginActivity extends ActionBarActivity {

   public static final String TAG = LoginActivity.class.getSimpleName();

    protected EditText mEmaiAsUserEditText;
    protected EditText mPasswordEditText;
    protected Button mLogInButton;
    protected TextView mSignUpTextView;
    protected Button mFacebookLogInButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        //Remove ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        mEmaiAsUserEditText = (EditText) findViewById(R.id.emailAsUserNameEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mLogInButton = (Button) findViewById(R.id.signUpButton);
        mSignUpTextView = (TextView) findViewById(R.id.signUpTextView);
        mFacebookLogInButton = (Button) findViewById(R.id.facebook_login_button);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEmaiAsUserEditText.getText().toString().toLowerCase();
                String password = mPasswordEditText.getText().toString();

                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message);
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    mProgressBar.setVisibility(View.VISIBLE);

                    ParseUser.logInInBackground(username,password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            mProgressBar.setVisibility(View.INVISIBLE);

                            if(e == null) {
                                //success



                                //update parseinstallation afther login success
                                SoShopBetaApplication.updateParseInstallation(user);

                                startMainFeed();

                            } else {
                                //Fail
                                //Show dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle(R.string.login_error_title);
                                builder.setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        }
                    });


                }
            }
        });

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mFacebookLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onFacebookLogInButtonClicked();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);

    }

    private void startMainFeed() {
        Intent intent = new Intent(this, MainActivity.class);
        //ADD FLAG SO USER CANNOT PRESS BACK TO SIGN UP ACTIVITY AGAIN
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void onFacebookLogInButtonClicked() {

        ParseFacebookUtils.logIn(Arrays.asList("email", ParseFacebookUtils.Permissions.Friends.ABOUT_ME),this,new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                    Toast.makeText(LoginActivity.this, "ParseUser return Null" ,Toast.LENGTH_SHORT).show();

                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Facebook!");

                    startMainFeed();
                } else {
                    Log.d(TAG, "User logged in through Facebook!");

                    startMainFeed();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
