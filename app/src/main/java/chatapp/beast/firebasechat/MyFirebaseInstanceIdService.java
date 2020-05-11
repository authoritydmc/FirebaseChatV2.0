package chatapp.beast.firebasechat;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MyFirebaseInstanceIdService extends FirebaseMessagingService {


    public void update_device_token(String token) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Toast.makeText(MyFirebaseInstanceIdService.this, "updated Device Token", Toast.LENGTH_SHORT).show();
            DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference().child(CONSTANTS.DATABASE_USER_nodE).child(user.getUid());
            dbreference.child(CONSTANTS.DEVICE_TOKEN).setValue(token);
        }
    }



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        update_device_token(s);
    }
}
