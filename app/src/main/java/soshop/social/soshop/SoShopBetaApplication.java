package soshop.social.soshop;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import soshop.social.soshop.Utils.ParseConstants;

/**
 * Created by Ninniez on 1/11/2015.
 */
public class SoShopBetaApplication extends Application{
    public void onCreate() {


            //Parse.enableLocalDatastore(this);
            Parse.initialize(this, "L15MX9Hlc0aJ1uG2UUD7k9k46go5Mvm1FzaDs2BB", "ptnznlu6PRJtdvc7kBYutxKWgEHeVNWIsekvZGTB");

    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID,user.getObjectId());
        installation.saveInBackground();
    }
}
