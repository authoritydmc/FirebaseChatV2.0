package chatapp.beast.firebasechat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CONSTANTS {
    public static String NEW_USER_STATUS="new_user_status";
    public static String NEW_USER_NAME="new_user_name";
    public static String OLD_USER_NAME="old_user_name";
    public  static  String OLD_USER_STATUS="old_user_status";
    public static int gallery_pick = 1;
    public static int change_profile_request_code = 2;
    public static String CHAT_TO_USER_ID = "chat_to_user_id";
    public static String CHAT_TO_USER_NAME = "chat_to_user_name";
    public static String CHAT_TO_USER_DP = "chat_to_user_dp";
    public static String DATABASE_USER_nodE = "Users";
    public static String DATABASE_USER_image = "user_image";
    public static String DATABASE_USER_online="online";
    public static String DATABASE_USER_thumb_image = "user_thumb_image";
    public static String DATABASE_USER_name = "user_name";
    public static String DATABASE_USER_status = "user_status";
    public static String DATABASE_USER_msg = "message";

    public static String DEVICE_TOKEN = "device_token";
    public static String MESSAGE_NODE = "Messages";
    public  static DatabaseReference DatabaseReferenceToCurrentUser=    FirebaseDatabase.getInstance().getReference().child(CONSTANTS.DATABASE_USER_nodE).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

}
