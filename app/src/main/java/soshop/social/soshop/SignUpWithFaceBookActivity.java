package soshop.social.soshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import soshop.social.soshop.Utils.ParseConstants;


public class SignUpWithFaceBookActivity extends ActionBarActivity {

    protected EditText mEmaiAsUserEditText;
    protected EditText mPasswordEditText;
    protected EditText mConfirmPasswordEditText;
    protected EditText mFirstname;
    protected EditText mLastName;
    protected Button mSignUpButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_facebook);

        //Remove ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mFirstname = (EditText) findViewById(R.id.firstNameEditText);
        mLastName = (EditText) findViewById(R.id.lastNameEditText);
        mEmaiAsUserEditText = (EditText) findViewById(R.id.emailAsUserNameEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAsUser = mEmaiAsUserEditText.getText().toString().toLowerCase();
                String password = mPasswordEditText.getText().toString();
                String confirmPassword = mConfirmPasswordEditText.getText().toString();
                String firstName = mFirstname.getText().toString();
                String lastName = mLastName.getText().toString();


                //trim to delete white space.
                emailAsUser = emailAsUser.trim();
                password = password.trim();


                if (emailAsUser.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpWithFaceBookActivity.this);
                    builder.setMessage(R.string.signup_error_message);
                    builder.setTitle(R.string.singup_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {
                    //check that two field of password is match
                    if (password.equals(confirmPassword)) {

                        mProgressBar.setVisibility(View.VISIBLE);

                        //create new user in from Parse library
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(emailAsUser);
                        newUser.setPassword(password);
                        newUser.setEmail(emailAsUser);
                        newUser.put(ParseConstants.KEY_FIRST_NAME, firstName);
                        newUser.put(ParseConstants.KEY_LAST_NAME, lastName);

                        //sign up perform like Async task method
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {

                                mProgressBar.setVisibility(View.INVISIBLE);

                                if (e == null) {
                                    //Success!

                                    //update parseinstallation afther login success
                                    SoShopBetaApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                    Intent intent = new Intent(SignUpWithFaceBookActivity.this, MainActivity.class);
                                    //ADD FLAG SO USER CANNOT PRESS BACK TO SIGN UP ACTIVITY AGAIN
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);

                                } else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpWithFaceBookActivity.this);
                                    builder.setMessage(e.getMessage());
                                    builder.setTitle(R.string.singup_error_title);
                                    builder.setPositiveButton(android.R.string.ok, null);

                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                }
                            }
                        });


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpWithFaceBookActivity.this);
                        builder.setMessage(getString(R.string.confirm_password_not_match_alert_message));
                        builder.setTitle(R.string.singup_error_title);
                        builder.setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();


                    }
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
