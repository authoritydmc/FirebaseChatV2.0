package chatapp.beast.firebasechat;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {


    static String refreshedToken;

    public static void update_device_token() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {


            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child(CONSTANTS.DATABASE_USER_nodE).child(user.getUid());
            dbreference.child(CONSTANTS.DEVICE_TOKEN).setValue(refreshedToken);
        }
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        MyFirebaseInstanceIdService.update_device_token();

    }


}
