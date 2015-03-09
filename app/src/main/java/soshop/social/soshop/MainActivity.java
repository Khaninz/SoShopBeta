package soshop.social.soshop;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import soshop.social.soshop.Fragment.InviteFriendsFragment;
import soshop.social.soshop.Fragment.MainFeedFragment;
import soshop.social.soshop.Fragment.SettingsFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //START: Log for facebook key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "soshop.social.soshop",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
        //END: Log for facebook key hash

        //START: CHECK IF THERE IS CURRENT USER LOGIN IN.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {

            navigateToLogin();

        } else {
            Log.i(TAG, currentUser.getUsername());
        }
        //END: CHECK IF THERE IS CURRENT USER LOGIN IN.

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        //add flag to intent, so user can't use 'back' to the main activity
        //read more about Flag in the api intent api document
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ActionBar actionBar = getSupportActionBar();

//        fragmentTransaction.replace(R.id.container, PlaceholderFragment.newInstance(position +1));
        switch (position) {
            case 0:
                actionBar.setTitle((R.string.title_section1));
                fragmentTransaction.replace(R.id.container, new MainFeedFragment());
                fragmentTransaction.commit();
                break;
            case 1:
                actionBar.setTitle((R.string.title_section2));
                fragmentTransaction.replace(R.id.container, new InviteFriendsFragment());
                fragmentTransaction.commit();
                break;
            case 2:
                actionBar.setTitle((R.string.title_section3));
                fragmentTransaction.replace(R.id.container, new SettingsFragment());
                fragmentTransaction.commit();
                break;

        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break; //must be add in every case
        }

        return super.onOptionsItemSelected(item);
    }

}
