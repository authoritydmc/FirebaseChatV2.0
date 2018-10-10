package chatapp.beast.firebasechat;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {


    public void update_device_token() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Toast.makeText(MyFirebaseInstanceIdService.this, "updated Device Token", Toast.LENGTH_SHORT).show();
            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child(CONSTANTS.DATABASE_USER_nodE).child(user.getUid());
            dbreference.child(CONSTANTS.DEVICE_TOKEN).setValue(FirebaseInstanceId.getInstance().getToken());
        }
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        update_device_token();
    }


}
